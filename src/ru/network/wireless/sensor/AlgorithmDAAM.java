package ru.network.wireless.sensor;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Yakov
 * Date: 28.03.2010
 * Time: 14:18:16
 * To change this template use File | Settings | File Templates.
 */
public class AlgorithmDAAM extends Algorithm {

    //выбираем родительский узел, которому будем отправлять запрос
    //здесь newbieNode=null, поле важно только для DIBA
    public Node chooseParentNode(List<PossibleParentDescriptor> candidatesList, Node newbieNode, Network network) {

        //дескриптор - пара "узел - число свободных мест"
        PossibleParentDescriptor tempDescriptorDAAM = null;
        //Проходимся по каждой паре
        for (PossibleParentDescriptor possibleParentDescriptor : candidatesList) {
            //Если у кандидат не в сети, то у него поле будет не инициализировано
            //Пропускаем такого кандидата
            if (possibleParentDescriptor.freeSlots == null) {
                continue;
            }
            
            //Выбираем узел с минимальным значением nwkCurDepth. По спеке, если таких узлов несколько - выбираем произвольно.
            //Мы для этого просто берем первый, у которого глубина минимальна.
            //todo: выбирать тот, который ближе (уточнить по спеке, можно ли определить расстояние до узла)
            //Если у узла нет свободных мест - пропускаем его - это условие только для DAAM. Для DIBA такие узлы тоже берутся в расчет.
            if (possibleParentDescriptor.freeSlots == 0) {
                continue;
            }
            
            //Если временный узел не задан, выбираем кандидатом первый узел из списка, у которого есть свободные места
            //и переходим к следующему
            if (tempDescriptorDAAM == null && possibleParentDescriptor.freeSlots > 0) {
                tempDescriptorDAAM = possibleParentDescriptor;
                continue;
            }

            //Сюда мы попадаем, когда у нас уже есть в списке кандидатов хотя бы один узел
            //Если глубина нового узла меньше глубины текущего кандидата - у нас новый кандидат.
            if (possibleParentDescriptor.possibleParent.getNwkCurDepth() < tempDescriptorDAAM.possibleParent.getNwkCurDepth()) {
                tempDescriptorDAAM = possibleParentDescriptor;
            }
        }

        //возвращаем кандидата
        if (tempDescriptorDAAM == null) {
            return null;
        }else {
            return tempDescriptorDAAM.possibleParent; 
        }


    }

    public RSPMessage doGetRSP(Node parent, Node newbieNode, Network network) {

        Integer result;
        //Если текущая глубина узла, получившего REQ меньше Lm, инициируем процедуру RSP
        
        if (parent.getNwkCurDepth() < network.getNwkMaxDepth()) {
            result = calculateAddress(parent, newbieNode, network, false);
        } else {
            result = null;
        }
        return new RSPMessage(result, null);

    }

    public Integer calculateAddress(Node parent, Node newbieNode, Network network, boolean borrowed) {


        if (newbieNode.isRouterCapable()) {

            int routerCapableChildrenCount = 0;
            for (RoutingItem routingItem : parent.getRoutingItems()) {
                if (routingItem.getChild().isRouterCapable()) {
                    routerCapableChildrenCount++;
                }
            }

            int result = parent.getAddress() + parent.cskip(network) * routerCapableChildrenCount + 1;

//            System.out.println(parent.getAddress() + "-> " + result + "  ( cskip: " + parent.cskip(network) + " and " + routerCapableChildrenCount + " router");

            return result;

        } else {

            int endChildrenCount = 0;
            for (RoutingItem routingItem : parent.getRoutingItems()) {
                if (!routingItem.getChild().isRouterCapable()) {
                    endChildrenCount++;
                }
            }

            int result = parent.getAddress() + parent.cskip(network) * network.getNwkMaxRouters() + endChildrenCount + 1;
//            System.out.println(parent.getAddress() + "-> " + result + "  ( cskip: " + parent.cskip(network) + " and " + endChildrenCount + " end )");
            return result;
        }
    }

}


