package ru.network.wireless.sensor.nodeTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.network.wireless.sensor.*;

import java.lang.reflect.Field;

/**
 * Created by IntelliJ IDEA.
 * User: Yakov
 * Date: 07.03.2010
 * Time: 21:47:21
 * To change this template use File | Settings | File Templates.
 */
public class CalculateAddressTest {

    private Algorithm algorithm;
    private Network networkSpec;
    private Node parent_0;
    private int uniqueCoordinate = 1;

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {

        algorithm = new AlgorithmDAAM();

        networkSpec = new Network();
        networkSpec.setNwkMaxChildren(6);
        networkSpec.setNwkMaxRouters(4);
        networkSpec.setNwkMaxDepth(3);

        parent_0 = new Node(10, 10, true);
        Field nodeDepth = Node.class.getDeclaredField("nwkCurDepth");
        nodeDepth.setAccessible(true);
        nodeDepth.set(parent_0, 0);
        parent_0.setAddress(0);

    }

    @Test
    public void calcAddressTest() {

        Node parent_1 = add(parent_0, true, 1);
        add(parent_0, false, 125);
        add(parent_0, false, 126);
        Node parent_32 = add(parent_0, true, 32);
        add(parent_0, true, 63);
        Node parent_94 = add(parent_0, true, 94);


        add(parent_1, true, 2);
        add(parent_32, true, 33);
        add(parent_32, true, 40);
        add(parent_94, true, 95);
        Node parent_102 = add(parent_94, true, 102);

        add(parent_102, true, 103);

    }

    private Node add(Node parent, boolean routerCapable, int expectedAddress) {
        Node child = new Node(11, uniqueCoordinate++, routerCapable);
        Assert.assertEquals(new Integer(expectedAddress), algorithm.calculateAddress(parent, child, networkSpec, routerCapable));
        child.setAddress(expectedAddress);
        child.setParent(parent);
        child.setNwkCurDepth(parent.getNwkCurDepth() + 1);
        parent.getRoutingItems().add(new RoutingItem(child, null, null));
        return child;
    }
}
