package ru.network.wireless.sensor;

import ru.network.field.Air;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Yakov
 * Date: 14.03.2010
 * Time: 21:42:29
 * To change this template use File | Settings | File Templates.
 */
public class NetworkBuilder {

    protected void initNeighbours(Network network) {
        for (Node node : network.getAIR()) {
            node.findNeighbours(network);
        }
    }

    public Network generate(Algorithm algorithm, Air air) {

        Network network = new Network();
        network.setAlgorithm(algorithm);
        
        loadProperties(network);
        network.setAir(air.getNodes());
        initNeighbours(network);
        for (Node o : network.getAIR()) {
            o.setEnabled(true);
        }
        initPanCoordinatorNode(network);
        int i = 0;
        while (i++ < 100000 && networkIsNotBuilt(network)) {
            Node randomNode = selectRandomeNode(network);
            if (randomNode.isRouterCapable() || network.getNwkMaxEndDevices() > 0) {
                randomNode.REQ(network);
            }
        }
        return network;
    }

    public static void loadProperties(Network network) {
        Properties proprties = new Properties();
        try {
            proprties.load(new FileInputStream("src/network.properties"));
            network.setFieldBorderX(Integer.valueOf(proprties.getProperty("fieldBorderX")));
            network.setFieldBorderY(Integer.valueOf(proprties.getProperty("fieldBorderY")));
            network.setNodeRange(Integer.valueOf(proprties.getProperty("nodeRange")));
            network.setNumberOfNodes(Integer.valueOf(proprties.getProperty("numberOfNodes")));
            network.setNwkMaxChildren(Integer.valueOf(proprties.getProperty("nwkMaxChildren")));
            network.setNwkMaxDepth(Integer.valueOf(proprties.getProperty("nwkMaxDepth")));
            network.setNwkMaxRouters(Integer.valueOf(proprties.getProperty("nwkMaxRouters")));
            network.setRandomStartingPoint(Boolean.valueOf(proprties.getProperty("randomStartingPoint")));
            network.setRouterCapableProbability(Double.valueOf(proprties.getProperty("routerCapableProbability")));
            network.setStepBetweenNodes(Integer.valueOf(proprties.getProperty("stepBetweenNodes")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param network Инициализация ZigBee-координатора.
     *                todo: реализовать свойство узла coordinatorCapable
     *                todo: после реализации свойства coordinatorCapable реализовать возможность выбора новым узлом сети, к которой будем присоединяться
     */
    private static void initPanCoordinatorNode(Network network) {
        Node startingNode = selectPanCoordinator(network);
        startingNode.setConnected(true);
        startingNode.setAddress(0);
        startingNode.setNwkCurDepth(0);
        //System.out.println("startingNode: address=" + startingNode.getAddress() + " coordinates=(" + startingNode.getX() + "," + startingNode.getY() + ")");
    }

    /**
     * @param network
     * @return Node
     *         Выбор произвольного узла в качестве ZigBee координатора.
     */
    private static Node selectPanCoordinator(Network network) {

        Object[] allNodes = network.getAIR().toArray();
        if (network.isRandomStartingPoint()) {
            Node node;
            Random random = new Random();
            while (!(node = (Node) allNodes[random.nextInt(allNodes.length)]).isRouterCapable()) {
            }
            return node;
        } else {
            double centerX = network.getFieldBorderX() / 2;
            double centerY = network.getFieldBorderY() / 2;
            double minDistance = Integer.MAX_VALUE;
            Node tempNode = new Node(Integer.MAX_VALUE, Integer.MAX_VALUE, true);
            for (Object oNode : allNodes) {
                Node node = (Node) oNode;
                double distance = Math.sqrt(Math.pow(centerX - node.getX(), 2) + Math.pow(centerY - node.getY(), 2));
                if (node.isRouterCapable() && distance < minDistance) {
                    tempNode = node;
                    minDistance = distance;
                }
            }
            return tempNode;
        }
    }

    /**
     * @param network
     * @return true если еще есть узлы, которые не connected
     *         false если выполняются несколько условий завершения построения сети, которые надо сформулировать
     */
    private static boolean networkIsNotBuilt(Network network) {
        return true;
    }


    private static Node selectRandomeNode(Network network) {

        Object[] airArray = network.getAIR().toArray();
        return (Node) airArray[new Random().nextInt(airArray.length)];
    }


}
