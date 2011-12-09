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

//1 Node{a=0, d=0, r=true,(x,y)=(723.33,735.43}-->Node{a=1, d=1, r=true,(x,y)=(790.7,699.73} l=null
//2 Node{a=0, d=0, r=true,(x,y)=(723.33,735.43}-->Node{a=14, d=1, r=true,(x,y)=(724.9,636.97} l=null
//3 Node{a=0, d=0, r=true,(x,y)=(723.33,735.43}-->Node{a=27, d=1, r=false,(x,y)=(752.99,675.66} l=null
//4 Node{a=14, d=1, r=true,(x,y)=(724.9,636.97}-->Node{a=25, d=2, r=false,(x,y)=(787.12,631.04} l=null
//5 Node{a=0, d=0, r=true,(x,y)=(723.33,735.43}-->Node{a=15, d=1, r=true,(x,y)=(694.98,690.57} l=Node{a=14, d=1, r=true,(x,y)=(724.9,636.97}
//6 Node{a=1, d=1, r=true,(x,y)=(790.7,699.73}-->Node{a=2, d=2, r=true,(x,y)=(864.72,658.8} l=null
//7 Node{a=2, d=2, r=true,(x,y)=(864.72,658.8}-->Node{a=3, d=3, r=true,(x,y)=(908.22,641.56} l=null
//8 Node{a=0, d=0, r=true,(x,y)=(723.33,735.43}-->Node{a=16, d=1, r=true,(x,y)=(710.83,673.81} l=Node{a=15, d=1, r=true,(x,y)=(694.98,690.57}
//9 Node{a=15, d=1, r=true,(x,y)=(694.98,690.57}-->Node{a=26, d=2, r=false,(x,y)=(622.63,732.38} l=null
//10 Node{a=1, d=1, r=true,(x,y)=(790.7,699.73}-->Node{a=7, d=2, r=true,(x,y)=(852.12,735.02} l=null
//11 Node{a=7, d=2, r=true,(x,y)=(852.12,735.02}-->Node{a=8, d=3, r=true,(x,y)=(866.98,794.25} l=null
//12 Node{a=2, d=2, r=true,(x,y)=(864.72,658.8}-->Node{a=4, d=3, r=true,(x,y)=(884.42,563.59} l=null
//13 Node{a=7, d=2, r=true,(x,y)=(852.12,735.02}-->Node{a=10, d=3, r=false,(x,y)=(903.89,770.82} l=null
//14 Node{a=2, d=2, r=true,(x,y)=(864.72,658.8}-->Node{a=9, d=3, r=true,(x,y)=(883.68,569.01} l=Node{a=7, d=2, r=true,(x,y)=(852.12,735.02}
//15 Node{a=14, d=1, r=true,(x,y)=(724.9,636.97}-->Node{a=20, d=2, r=true,(x,y)=(764.71,563.85} l=null
//16 Node{a=20, d=2, r=true,(x,y)=(764.71,563.85}-->Node{a=21, d=3, r=true,(x,y)=(680.05,516.99} l=null
//17 Node{a=14, d=1, r=true,(x,y)=(724.9,636.97}-->Node{a=17, d=2, r=true,(x,y)=(631.29,639.43} l=Node{a=16, d=1, r=true,(x,y)=(710.83,673.81}
//18 Node{a=20, d=2, r=true,(x,y)=(764.71,563.85}-->Node{a=22, d=3, r=true,(x,y)=(750.07,498.11} l=null
//19 Node{a=14, d=1, r=true,(x,y)=(724.9,636.97}-->Node{a=18, d=2, r=true,(x,y)=(780.68,636.56} l=Node{a=17, d=2, r=true,(x,y)=(631.29,639.43}
//20 Node{a=17, d=2, r=true,(x,y)=(631.29,639.43}-->Node{a=19, d=3, r=true,(x,y)=(577.02,710.45} l=null
//Node{a=14, d=1, r=true,(x,y)=(724.9,636.97}--> newbie (803.28,621.64, r=true)
//Address 19 was already assigned!!!!!
public class DIBAtest3 {

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
        Node node_87=createConnectedNode(87,3);
        Node node_91=createConnectedNode(91,3);
        Node node_95 = createConnectedNode(95, 2);
        Node node_108 = createConnectedNode(108, 2);

        Node node_104 = createDisconnectedNode();
        Node node_100 = createDisconnectedNode();

        Node node_108_fake1 = createDisconnectedNode();
        Node node_108_fake2 = createDisconnectedNode();
        Node node_108_fake3 = createDisconnectedNode();

        Node node_104_fake1 = createConnectedNode(105,3);
        Node node_104_fake2 = createConnectedNode(106,3);
        Node node_104_fake3 = createConnectedNode(107,3);


        node_1.setParent(node_0);
        node_41.setParent(node_0);
        node_81.setParent(node_0);
        node_82.setParent(node_81);
        node_83.setParent(node_82);
        node_87.setParent(node_82);
        node_91.setParent(node_82);
        node_95.setParent(node_81);
        node_108.setParent(node_81);
        node_83.setParent(node_82);
        node_108_fake1.setParent(node_108);
        node_108_fake2.setParent(node_108);
        node_108_fake3.setParent(node_108);

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
        node_81.getNeighbours().add(node_100);
        node_81.getRoutingItems().add(new RoutingItem(node_82, null, null));
        node_81.getRoutingItems().add(new RoutingItem(node_95, null, null));
        node_81.getRoutingItems().add(new RoutingItem(node_108, null, null));

        node_82.getNeighbours().add(node_81);
        node_82.getNeighbours().add(node_83);
        node_82.getNeighbours().add(node_87);
        node_82.getNeighbours().add(node_91);
        node_82.getRoutingItems().add(new RoutingItem(node_83, null, null));
        node_82.getRoutingItems().add(new RoutingItem(node_87, null, null));
        node_82.getRoutingItems().add(new RoutingItem(node_91, null, null));

        node_95.getNeighbours().add(node_81);

        node_108.getNeighbours().add(node_108_fake1);
        node_108.getNeighbours().add(node_108_fake2);
        node_108.getNeighbours().add(node_108_fake3);
        node_108.getRoutingItems().add(new RoutingItem(node_108_fake1, null, null));
        node_108.getRoutingItems().add(new RoutingItem(node_108_fake2, null, null));
        node_108.getRoutingItems().add(new RoutingItem(node_108_fake3, null, null));

        node_104.getNeighbours().add(node_81);
        node_104.REQ(network);


        node_104_fake1.getNeighbours().add(node_104);
        node_104_fake2.getNeighbours().add(node_104);
        node_104_fake3.getNeighbours().add(node_104);

        node_104.getNeighbours().add(node_108_fake1);
        node_104.getNeighbours().add(node_108_fake2);
        node_104.getNeighbours().add(node_108_fake3);
        node_104.getRoutingItems().add(new RoutingItem(node_104_fake1, null, null));
        node_104.getRoutingItems().add(new RoutingItem(node_104_fake2, null, null));
        node_104.getRoutingItems().add(new RoutingItem(node_104_fake3, null, null));        

        node_100.getNeighbours().add(node_81);

        node_100.REQ(network);

        Assert.assertEquals(104, node_104.getAddress());
        Assert.assertEquals(100, node_100.getAddress());
        Assert.assertEquals(81, node_104.getParent().getAddress());
        Assert.assertEquals(81, node_100.getParent().getAddress());

        Assert.assertEquals(5, node_81.getRoutingItems().size());
        Assert.assertEquals(node_104, node_81.getRoutingItems().get(3).getChild());
        Assert.assertEquals(node_100, node_81.getRoutingItems().get(4).getChild());
        Assert.assertEquals(node_95, node_81.getRoutingItems().get(3).getLender());
        Assert.assertEquals(node_95, node_81.getRoutingItems().get(4).getLender());

        Assert.assertEquals(2, node_95.getRoutingItems().size());
        Assert.assertEquals(node_104, node_95.getRoutingItems().get(0).getChild());
        Assert.assertEquals(node_100, node_95.getRoutingItems().get(1).getChild());
        Assert.assertEquals(node_81, node_95.getRoutingItems().get(0).getBorrower());
        Assert.assertEquals(node_81, node_95.getRoutingItems().get(1).getBorrower());


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