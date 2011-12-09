package ru.network.wireless.sensor.nodeTests;

import org.junit.Assert;
import org.junit.Test;
import ru.network.wireless.sensor.Network;
import ru.network.wireless.sensor.Node;

import java.lang.reflect.Field;

/**
 * Created by IntelliJ IDEA.
 * User: Yakov
 * Date: 06.03.2010
 * Time: 18:19:45
 * To change this template use File | Settings | File Templates.
 */

public class CskipTest {

    @Test
    public void cskipTest() throws NoSuchFieldException, IllegalAccessException {

        Network network1 = new Network();
        network1.setNwkMaxChildren(20);
        network1.setNwkMaxRouters(6);
        network1.setNwkMaxDepth(5);

        Network networkSpec = new Network();
        networkSpec.setNwkMaxChildren(6);
        networkSpec.setNwkMaxRouters(4);
        networkSpec.setNwkMaxDepth(3);

        Node node = new Node(10,10, true);

        Field nodeDepth = Node.class.getDeclaredField("nwkCurDepth");
        nodeDepth.setAccessible(true);


        nodeDepth.set(node, 0);
        Assert.assertEquals(5181, node.cskip(network1));
        Assert.assertEquals(31, node.cskip(networkSpec));

        nodeDepth.set(node, 1);
        Assert.assertEquals(861, node.cskip(network1));
        Assert.assertEquals(7, node.cskip(networkSpec));

        nodeDepth.set(node, 2);
        Assert.assertEquals(141, node.cskip(network1));
        Assert.assertEquals(1, node.cskip(networkSpec));

        nodeDepth.set(node, 3);
        Assert.assertEquals(21, node.cskip(network1));
        Assert.assertEquals(0, node.cskip(networkSpec));

        nodeDepth.set(node, 4);
        Assert.assertEquals(1, node.cskip(network1));

        nodeDepth.set(node, 5);
        Assert.assertEquals(0, node.cskip(network1));


    }
}
