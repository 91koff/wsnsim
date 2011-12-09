package ru.network.wireless.sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Yakov
 * Date: 28.03.2010
 * Time: 14:18:09
 * To change this template use File | Settings | File Templates.
 */
public class AlgorithmDIBA extends Algorithm {

    /**
     * @param candidatesList
     * @param newbieNode
     * @param network        @return Как в DAAM
     */
    public Node chooseParentNode(List<PossibleParentDescriptor> candidatesList, Node newbieNode, Network network) {

        PossibleParentDescriptor tempDescriptor = null;
        //Проходимся по каждой паре "узел - число свободных мест"
        for (PossibleParentDescriptor possibleParentDescriptor : candidatesList) {
            //Если кандидат не в сети, у него поле будет не инициализировано - пропускаем.
            if (possibleParentDescriptor.freeSlots == null) {
                //по идее, такого быть не может, так как в списке кандидатов только узлы из сети
                throw new RuntimeException("AlgorithmDIBA.chooseParentNode !!!!!! Can't be here!!!!!");
            }

            //Выбираем узел с минимальным значением nwkCurDepth. По спеке, если таких узлов несколько -
            //выбираем произвольно. Мы для этого просто берем первый, у которого глубина минимальна.
            //Если временный узел не задан, выбираем кандидатом первый узел из списка, у которого есть свободные места
            //Здесь, в отличие от DAAM, рассматриваем случаи, когда мест нет.
            if (tempDescriptor == null && possibleParentDescriptor.freeSlots >= 0) {
                tempDescriptor = possibleParentDescriptor;
                continue;
            }

            //Сюда мы попадаем, когда у нас уже есть в списке кандидатов хотя бы один узел
            //todo: выбираем кандидата в родители по глубине, возможно, ввести еще условия
            //в статье говорится о том, что берем того, у кого места больше
            //если места одинаково, будем сравнивать по глубине
            if (possibleParentDescriptor.possibleParent.getFreeSlots(network, newbieNode) > tempDescriptor.possibleParent.getFreeSlots(network, newbieNode)) {
                tempDescriptor = possibleParentDescriptor;
                continue;
            } else if (possibleParentDescriptor.possibleParent.getFreeSlots(network, newbieNode) == tempDescriptor.possibleParent.getFreeSlots(network, newbieNode)) {
                if (possibleParentDescriptor.possibleParent.getNwkCurDepth() < tempDescriptor.possibleParent.getNwkCurDepth()) {
                    tempDescriptor = possibleParentDescriptor;
                } else {
                    continue;
                }
            }

        }

        if (tempDescriptor == null) {
            return null;
        } else {
            return tempDescriptor.possibleParent;
        }


    }

    public Node chooseLenderNode(List<PossibleParentDescriptor> candidatesList, Node newbie, Network network) {
        Node tempNode = null; //todo: при прочих равных выбрать узел с наимаеньшим адресом
        for (PossibleParentDescriptor possibleLenderDescriptor : candidatesList) {
            //Если кандидат не может ничего дать, то его не учитываем, т.к. он может быть не в сети или не иметь свободных мест
            //todo: на будущее: тут можно развить дальше, если запрос будет идти дальше на второй хоп.
            //todo: на будущее: в статье рассматривается сеть, в которой предаставлены только роутреры. Последнее условие в первом if зашито на это предположение.
            //todo: странно, по идее, если у кандидата глубина превышает максимальную, то нам от этого не должно быть плохо
            //Если кандидат по каким-то причинам не может дать адрес
            if (possibleLenderDescriptor.freeSlots == null ||
                    possibleLenderDescriptor.freeSlots == 0 ||
                    possibleLenderDescriptor.possibleParent.getNwkCurDepth() >= network.getNwkMaxDepth() ||
                    possibleLenderDescriptor.possibleParent.getNwkCurRoutersCount() + possibleLenderDescriptor.possibleParent.getNwkCurBorrowedRoutersCount() >= network.getNwkMaxRouters()) {
            } else {
                if (tempNode == null) {
                    tempNode = possibleLenderDescriptor.possibleParent;
                } else {
                    //если есть свободные места
                    if (possibleLenderDescriptor.freeSlots > tempNode.getFreeSlots(network, newbie)) {
                        tempNode = possibleLenderDescriptor.possibleParent;
                    } else if (possibleLenderDescriptor.freeSlots.equals(tempNode.getFreeSlots(network, newbie)) &&
                            possibleLenderDescriptor.possibleParent.getNwkCurDepth() < tempNode.getNwkCurDepth()) {
                        tempNode = possibleLenderDescriptor.possibleParent;
                    }
                }
            }
        }
        return tempNode;
    }

    public RSPMessage doGetRSP
            (Node
                    parent, Node
                    newbie, Network
                    network) {
        //если у parent есть свободные места, то просто считаем адрес
        //иначе начинаем процедуру получения адресов у соседей по DIBA
        if (parent.hasFreeSlotFor(newbie, network) && parent.getNwkCurDepth() < network.getNwkMaxDepth()) {
            Integer address = calculateAddress(parent, newbie, network, false);
            return new RSPMessage(address, null);
        } else {
            //Выбираем узел, у которого будем брать взаймы адрес
            Node lender = ABREQ(parent, newbie, network);
            if (lender == null) {
                return new RSPMessage(null, null);
            } else {
                Integer borrowedAddress = ABRSP(parent, lender, newbie, network);
                return new RSPMessage(borrowedAddress, lender);
            }
        }
    }

    /**
     * @param newbie
     * @param network @return
     */
    private Node ABREQ
            (Node
                    parent, Node
                    newbie, Network
                    network) {

        Node lender = null;
        //отправляем запрос только, если узел включен и в сети
        //по идее, это лишняя проверка, так как к нам уже обратились за адресом, а значит мы включены и в сети
        if (parent.isEnabled() && parent.isConnected()) {
            //Получили список соседей с их свободными местами
            List<PossibleParentDescriptor> candidatesList = new ArrayList<PossibleParentDescriptor>();
            candidatesList.add(new PossibleParentDescriptor(parent, parent.getFreeSlots(network, parent)));
            for (RoutingItem routingItem : parent.getRoutingItems()) {
                candidatesList.add(new PossibleParentDescriptor(routingItem.getChild(), routingItem.getChild().getFreeSlots(network, newbie)));
            }

            //Получаем узел, у которого больше всего свободных мест
            //Если несколько, то выбираем тот, у кого глубина меньше

            lender = chooseLenderNode(candidatesList, newbie, network);
        }
        return lender;
    }

    public Integer ABRSP
            (Node
                    parent, Node
                    lender, Node
                    newbie, Network
                    network) {
        if (!lender.isEnabled() || !lender.isConnected() || !lender.isRouterCapable()) {
            return null;
        }
        Integer address = calculateAddress(lender, newbie, network, true);
        if (address != null) {
            ABACK(parent, lender, newbie, network);
            return address;
        } else {
            return null;
        }

    }

    //узел m, выбрав адрес из пула адресов узла c, отвечает ему сообщением ABACK, сообзая об этом.
    //узел с записывает в свою таблицу маршрутизации информацию о том, кому какой адрес был отдан
    //после отправки этого сообщения адрес, взятый у c, отправляется в сообзении RSP новому узлу, который просит адрес для входа в сеть

    public void ABACK
            (Node
                    borrower, Node
                    lender, Node
                    child, Network
                    network) {
        lender.getRoutingItems().add(new RoutingItem(child, null, borrower));
    }

    //в отличие от DAAM, тут надо учитывать число детей с заборовленными адресами

    /**
     * @throws Exception when network.getNwkMaxRouters() <= numberOfBorrowedAddresses
     */
    public Integer calculateAddress
            (Node
                    parentOrLender, Node
                    newbieNode, Network
                    network, boolean isBorrowingProcess) {

        int result;
        int trueChildren = 0;
        int lendedChildren = 0;
        if (newbieNode.isRouterCapable()) {
            for (RoutingItem routingItem : parentOrLender.getRoutingItems()) {
                if (routingItem.getChild().isRouterCapable()) {
                    if (routingItem.isLended()) {
                        lendedChildren++;
                    } else {
                        trueChildren++;
                    }
                }
            }
        } else {
            //todo: на будущее: учесть endDevices
            throw new RuntimeException(getClass().getName() + "Node " + newbieNode + "is not routerCapable");
//            for (RoutingItem routingItem : parentOrLender.getRoutingItems()) {
//                if (!routingItem.getChild().isRouterCapable() && !routingItem.isBorrowed()) {
//                    childrenCountForChildType++;
//                }
//            }
        }
        if (isBorrowingProcess) {
            int numberOfLendedAddresses = parentOrLender.getLendedRoutingItemsCount();
            numberOfLendedAddresses++;
            if (network.getNwkMaxRouters() > numberOfLendedAddresses) {
                //todo: на будущее: формула не учитывает endDevices!!!!.
                result = parentOrLender.getAddress() + (network.getNwkMaxRouters() - numberOfLendedAddresses) * parentOrLender.cskip(network) + 1;
            } else {
                return null;
            }
        } else {
            if (newbieNode.isRouterCapable()) {
                //todo: на будущее: тут еще будет проверка на isRouterCapable
                result = parentOrLender.getAddress() + parentOrLender.cskip(network) * trueChildren + 1;
            } else {
                return null;
//                result = parentOrLender.getAddress() + parentOrLender.cskip(network) * network.getNwkMaxRouters() + childrenCountForChildType + 1;
            }
        }
        return result;
    }

}
