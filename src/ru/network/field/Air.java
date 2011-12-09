package ru.network.field;

import ru.network.wireless.sensor.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Yakov
 * Date: 15.05.2010
 * Time: 23:30:31
 * To change this template use File | Settings | File Templates.
 */
public class Air {

    protected Set<Node> nodes;

    public Set<Node> getNodes() {
        Set cloned = new HashSet();
        for (Node node : nodes) {
            cloned.add(node.clone());
        }
        return cloned;
    }

}
