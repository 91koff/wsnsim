package ru.common;

import ru.network.wireless.sensor.Network;
import ru.network.wireless.sensor.Node;

/**
 * User: Yakov
 * Date: 28.02.2010
 * Time: 13:42:58
 */
public class Logger {

    public static void printNodesWithNeighbours(Network network) {
        int totalNeighboursCount = 0;
        for (Node node : network.getAIR()) {
            System.out.println(node);
            for (Node node1 : node.getNeighbours()) {
                System.out.println(">>>>>>>>>>>>> " + node1);

            }
            totalNeighboursCount += node.getNeighbours().size();
            System.out.println("");
        }
        System.out.println("AVG N = " + (double) totalNeighboursCount / network.getAIR().size());
    }
}
