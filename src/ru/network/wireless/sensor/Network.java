package ru.network.wireless.sensor;

import java.util.HashSet;
import java.util.Set;

/**
 * User: Yakov
 * Date: 24.02.2010
 * Time: 1:21:32
 */
public class Network {
    //маленькими буквами
    private int nwkMaxChildren;
    private int nwkMaxRouters;
    private int nwkMaxDepth;
    private int nodeRange;
    //
    private int stepBetweenNodes;
    private int fieldBorderX;
    private int fieldBorderY;
    private int numberOfNodes;

    /**
     * где находится PAN Coordinator
     * true - выбирается случайный узел
     * false - выбирается узел в центре
     * todo:реализовать алгоритм, который будет выбирать главный узел - см. диссертацию из СПб.
     */
    boolean randomStartingPoint = false;

    private double routerCapableProbability = 1;

    private Algorithm algorithm;

    private Set<Node> AIR = new HashSet<Node>();

    public int getFieldBorderX() {
        return fieldBorderX;
    }

    public void setFieldBorderX(int fieldBorderX) {
        this.fieldBorderX = fieldBorderX;
    }

    public int getFieldBorderY() {
        return fieldBorderY;
    }

    public void setFieldBorderY(int fieldBorderY) {
        this.fieldBorderY = fieldBorderY;
    }

    public int getNodeCount() {
        return AIR.size();
    }

    public int getNodeRange() {
        return nodeRange;
    }

    public void setNodeRange(int nodeRange) {
        this.nodeRange = nodeRange;
    }

    public Set<Node> getAIR() {
        return AIR;
    }

    public int getNwkMaxDepth() {
        return nwkMaxDepth;
    }

    public void setNwkMaxDepth(int nwkMaxDepth) {
        this.nwkMaxDepth = nwkMaxDepth;
    }

    public int getNwkMaxChildren() {
        return nwkMaxChildren;
    }

    public void setNwkMaxChildren(int nwkMaxChildren) {
        this.nwkMaxChildren = nwkMaxChildren;
    }

    public int getNwkMaxRouters() {
        return nwkMaxRouters;
    }

    public int getNwkMaxEndDevices() {
        return nwkMaxChildren - nwkMaxRouters;
    }

    public void setNwkMaxRouters(int nwkMaxRouters) {
        this.nwkMaxRouters = nwkMaxRouters;
    }


    public int getStepBetweenNodes() {
        return stepBetweenNodes;
    }

    public void setStepBetweenNodes(int stepBetweenNodes) {
        this.stepBetweenNodes = stepBetweenNodes;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public double getRouterCapableProbability() {
        return routerCapableProbability;
    }

    public void setRouterCapableProbability(double routerCapableProbability) {
        this.routerCapableProbability = routerCapableProbability;
    }

    public boolean isRandomStartingPoint() {
        return randomStartingPoint;
    }

    public void setRandomStartingPoint(boolean randomStartingPoint) {
        this.randomStartingPoint = randomStartingPoint;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setAir(Set<Node> air) {
        this.AIR = air;
    }
}
