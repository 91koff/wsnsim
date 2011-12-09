package ru.network.field;

import ru.network.wireless.sensor.Network;
import ru.network.wireless.sensor.NetworkBuilder;
import ru.network.wireless.sensor.Node;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Yakov
 * Date: 15.05.2010
 * Time: 23:30:39
 * To change this template use File | Settings | File Templates.
 */
public class RandomAir extends Air {

    public RandomAir() {
        Network dummyNetwork = new Network();
        NetworkBuilder.loadProperties(dummyNetwork);
        Random random = new Random();
        //создаем на поле заданное число узлов
        while (dummyNetwork.getAIR().size() < dummyNetwork.getNumberOfNodes()) {
            double randomX = Math.abs(random.nextDouble() * dummyNetwork.getFieldBorderX());
            double randomY = Math.abs(random.nextDouble() * dummyNetwork.getFieldBorderY());
            double roundedX = new BigDecimal(randomX).setScale(2, RoundingMode.HALF_UP).doubleValue();
            double roundedY = new BigDecimal(randomY).setScale(2, RoundingMode.HALF_UP).doubleValue();
            boolean isRouter = Math.random() < dummyNetwork.getRouterCapableProbability();
            dummyNetwork.getAIR().add(new Node(roundedX, roundedY, isRouter));
        }
        nodes = dummyNetwork.getAIR();
    }

}
