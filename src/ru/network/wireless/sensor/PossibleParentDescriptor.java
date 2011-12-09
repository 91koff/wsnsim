package ru.network.wireless.sensor;

/**
* Created by IntelliJ IDEA.
* User: Yakov
* Date: 28.03.2010
* Time: 14:56:41
* To change this template use File | Settings | File Templates.
*/
class PossibleParentDescriptor {
    Node possibleParent;
    Integer freeSlots;

    PossibleParentDescriptor(Node possibleParent, Integer freeSlots) {
        this.possibleParent = possibleParent;
        this.freeSlots = freeSlots;
    }
}
