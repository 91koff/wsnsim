package ru.network.wireless.sensor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Yakov
 * Date: 28.03.2010
 * Time: 14:17:52
 * To change this template use File | Settings | File Templates.
 */
public abstract class Algorithm {

    /**
     * @param network
     * @return возвращаем пары "узел - число свободных мест" в зависимости от типа узла, который хочет войти в сеть
     */
    public List<PossibleParentDescriptor> chooseCandidatesForParentNode(Node newbieNode, Network network) {
        List<PossibleParentDescriptor> candidateNodes = new ArrayList<PossibleParentDescriptor>();
        //выбиарем всех, кто может быть родителем
        for (Node neighbour : newbieNode.getNeighbours()) {
            if (neighbour.isRouterCapable() && neighbour.isConnected() && neighbour.isEnabled()) {
                candidateNodes.add(new PossibleParentDescriptor(neighbour, neighbour.getFreeSlots(network, newbieNode)));
            }
        }
        return candidateNodes;
    }

    //выбираем родителя в зависимости от алгоритма
    public abstract Node chooseParentNode(List<PossibleParentDescriptor> candidatesList, Node newbieNode, Network network);

    public void addRoutingItem(int address, Node parent, Node child, Node lender) {
        child.setAddress(address);
        child.setParent(parent);
        child.setConnected(true);
        //todo: не отражено в статье!!!!!!!!!!!!
        if(lender!=null){
            child.setNwkCurDepth(lender.getNwkCurDepth() + 1);
        }else {
            child.setNwkCurDepth(parent.getNwkCurDepth() + 1);
        }
        parent.getRoutingItems().add(new RoutingItem(child, lender, null));
        //System.out.println(Main.iteration + " " + parent.toString() +  "-->" + child + " l=" + lender);
    }

    public RSPMessage getRSP(Node chosenParent, Node newbieNode, Network network){
        //Если узел, получивший REQ, не включен, не в сети или не является роутером, то он не может ответить на REQ - возвращаем null.
        if (!chosenParent.isEnabled() || !chosenParent.isConnected() || !chosenParent.isRouterCapable()) {
            return null;
        }

        return doGetRSP(chosenParent, newbieNode, network);

    }

    protected abstract RSPMessage doGetRSP(Node parent, Node newbieNode, Network network);

    public abstract Integer calculateAddress(Node parent, Node newbieNode, Network network, boolean borrowed);



}
