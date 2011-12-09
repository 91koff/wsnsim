package ru.network.wireless.sensor;

/**
* Created by IntelliJ IDEA.
* User: Yakov
* Date: 28.03.2010
* Time: 14:48:38
* To change this template use File | Settings | File Templates.
*/
class RSPMessage {

    Node lender;
    Integer address;

    RSPMessage(Integer address, Node lender) {
        this.lender = lender;
        this.address = address;
    }

    public Node getLender() {
        return lender;
    }

    public Integer getAddress() {
        return address;
    }


}
