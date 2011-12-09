package ru.init;

import ru.common.JungVisualizer;
import ru.network.field.Air;
import ru.network.field.RandomAir;
import ru.network.wireless.sensor.*;

import javax.swing.*;


/**
 * User: Yakov
 * Date: 26.02.2010
 * Time: 1:16:58
 */
public class Main {

    public static void main(String[] args) {

        Algorithm algorithmDIBA = new AlgorithmDIBA();
        Algorithm algorithmDAAM = new AlgorithmDAAM();

        for (int i = 0; i < 1; i++) {
            Air air = new RandomAir();
//            Air air = new UniformAir();
            int daamConnected = build(algorithmDAAM, air, i);
            int dibaConnected = build(algorithmDIBA, air, i);
            System.out.println("DAAM: " + daamConnected + ", DIBA: " + dibaConnected);
        }

//        goUniform(algorithmDAAM,100,false);
//        goUniform(algorithmDIBA,100,false);

    }

    private static int build(Algorithm algorithm, Air air, int index) {
        int connectedNodes = 0;
        Network randomNetwork = new NetworkBuilder().generate(algorithm, air);
        for (Node node : randomNetwork.getAIR()) {
            if (node.isConnected()) {
                connectedNodes++;
            }
        }
        paint(randomNetwork, algorithm, connectedNodes);
        return connectedNodes;
    }

    private static void paint(final Network network, final Algorithm algorithm, final int index) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JungVisualizer visualizer = new JungVisualizer(algorithm.getClass().getName() + " " + index);
                visualizer.draw(network);
            }
        });
    }

}

