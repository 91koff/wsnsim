package ru.network.field;

import ru.network.field.Air;
import ru.network.wireless.sensor.Network;
import ru.network.wireless.sensor.NetworkBuilder;
import ru.network.wireless.sensor.Node;

/**
 * Created by IntelliJ IDEA.
 * User: Yakov
 * Date: 15.05.2010
 * Time: 23:50:53
 * To change this template use File | Settings | File Templates.
 */
public class UniformAir extends Air {

    public UniformAir() {

        Network dummyNetwork = new Network();
        NetworkBuilder.loadProperties(dummyNetwork);
        final double size = Math.sqrt(dummyNetwork.getNumberOfNodes()) * dummyNetwork.getStepBetweenNodes();
        dummyNetwork.setFieldBorderX((int) size);
        dummyNetwork.setFieldBorderY((int) size);
        for (int i = 0; i < Math.sqrt(dummyNetwork.getNumberOfNodes()); i++) {
            for (int j = 0; j < Math.sqrt(dummyNetwork.getNumberOfNodes()); j++) {
                Node node = new Node(i * dummyNetwork.getStepBetweenNodes(), j * dummyNetwork.getStepBetweenNodes(), Math.random() < dummyNetwork.getRouterCapableProbability());
                dummyNetwork.getAIR().add(node);
            }
        }
        nodes = dummyNetwork.getAIR();
    }

}
