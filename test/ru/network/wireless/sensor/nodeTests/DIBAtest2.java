package ru.network.wireless.sensor.nodeTests;

import org.junit.Assert;
import org.junit.Test;
import ru.network.wireless.sensor.Network;
import ru.network.wireless.sensor.Node;
import ru.network.wireless.sensor.RoutingItem;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 03.04.2010
 * Time: 20:47:07
 */

public class DIBAtest2 {

    private int coo = 1;

    @Test
    public void dibaAssignmentTest() {

        Network network = new Network();
        network.setNwkMaxChildren(3);
        network.setNwkMaxRouters(3);
        network.setNwkMaxDepth(4);

        Node node_0 = createConnectedNode(0, 0);
        Node node_1 = createConnectedNode(1, 1);
        Node node_41 = createConnectedNode(41, 1);
        Node node_81 = createConnectedNode(81, 1);
        Node node_82 = createConnectedNode(82, 2);
        Node node_83 = createConnectedNode(83, 3);
        Node node_95 = createConnectedNode(95, 2);
        Node node_108 = createConnectedNode(108, 2);
        Node node_104 = createDisconnectedNode();
        
        Node node_82_fake1 = createConnectedNode(990, 3);
        Node node_82_fake2 = createConnectedNode(991, 3);
        
        Node node_95_fake1 = createConnectedNode(992, 3);
        Node node_95_fake2 = createConnectedNode(993, 3);
        Node node_95_fake3 = createConnectedNode(994, 3);
        
        Node node_108_fake1 = createConnectedNode(995, 3);
        Node node_108_fake2 = createConnectedNode(996, 3);
        Node node_108_fake3 = createConnectedNode(997, 3);

        node_1.setParent(node_0);
        node_41.setParent(node_0);
        node_81.setParent(node_0);
        node_82.setParent(node_81);
        node_95.setParent(node_81);
        node_108.setParent(node_81);
        node_83.setParent(node_82);

        node_0.getNeighbours().add(node_1);
        node_0.getNeighbours().add(node_41);
        node_0.getNeighbours().add(node_81);
        node_0.getRoutingItems().add(new RoutingItem(node_1, null, null));
        node_0.getRoutingItems().add(new RoutingItem(node_41, null, null));
        node_0.getRoutingItems().add(new RoutingItem(node_81, null, null));

        node_81.getNeighbours().add(node_0);
        node_81.getNeighbours().add(node_82);
        node_81.getNeighbours().add(node_95);
        node_81.getNeighbours().add(node_108);
        node_81.getNeighbours().add(node_104);
        node_81.getRoutingItems().add(new RoutingItem(node_82, null, null));
        node_81.getRoutingItems().add(new RoutingItem(node_95, null, null));
        node_81.getRoutingItems().add(new RoutingItem(node_108, null, null));

        node_82.getNeighbours().add(node_81);
        node_82.getNeighbours().add(node_83);
        node_82.getNeighbours().add(node_82_fake1);
        node_82.getNeighbours().add(node_82_fake2);
        node_82.getRoutingItems().add(new RoutingItem(node_83, null, null));
        node_82.getRoutingItems().add(new RoutingItem(node_82_fake1, null, null));
        node_82.getRoutingItems().add(new RoutingItem(node_82_fake2, null, null));

        node_95.getNeighbours().add(node_81);
        node_95.getNeighbours().add(node_95_fake1);
        node_95.getNeighbours().add(node_95_fake2);
        node_95.getNeighbours().add(node_95_fake3);
        node_95.getRoutingItems().add(new RoutingItem(node_95_fake1, null, null));
        node_95.getRoutingItems().add(new RoutingItem(node_95_fake2, null, null));
        node_95.getRoutingItems().add(new RoutingItem(node_95_fake3, null, null));

        node_104.getNeighbours().add(node_81);

        node_108.getNeighbours().add(node_81);
        node_108.getNeighbours().add(node_108_fake1);
        node_108.getNeighbours().add(node_108_fake2);
        node_108.getNeighbours().add(node_108_fake3);
        node_108.getRoutingItems().add(new RoutingItem(node_108_fake1, null, null));
        node_108.getRoutingItems().add(new RoutingItem(node_108_fake2, null, null));
        node_108.getRoutingItems().add(new RoutingItem(node_108_fake3, null, null));

        node_104.REQ(network);

        Assert.assertEquals(-1, node_104.getAddress());
        Assert.assertEquals(false, node_104.isConnected());
        Assert.assertEquals(null, node_104.getParent());

        Assert.assertEquals(3, node_81.getRoutingItems().size());
        Assert.assertEquals(3, node_81.getRoutingItems().size());

        Assert.assertEquals(3, node_95.getRoutingItems().size());


//        Field addressField = Node.class.getDeclaredField("address");
//        addressField.setAccessible(true);
//
//        addressField.setInt(node_1_0, 1);
//        addressField.setInt(node_2_1, 2);
//        addressField.setInt(node_3_1, 3);
//        addressField.setInt(node_4_0, 4);
//
//        testMap.put(node_1_0, 0);
//        testMap.put(node_2_1, 1);
//        testMap.put(node_3_1, 0);
//        testMap.put(node_4_0, 0);

    }

    private Node createConnectedNode(int address, int nwkCurDepth) {
        Node node = new Node(1, coo++, true);
        node.setConnected(true);
        node.setEnabled(true);
        node.setAddress(address);
        node.setNwkCurDepth(nwkCurDepth);
        return node;
    }

    private Node createDisconnectedNode() {
        Node node = new Node(1, coo++, true);
        node.setEnabled(true);
        return node;
    }

}