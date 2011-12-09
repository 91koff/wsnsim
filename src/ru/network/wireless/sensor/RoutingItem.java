package ru.network.wireless.sensor;

public class RoutingItem {

    private Node child;
    private Node lender;
    private Node borrower;

    public RoutingItem(Node child, Node lender, Node borrower) {
        this.child = child;
        this.lender = lender;
        this.borrower = borrower;
    }

    public Node getChild() {
        return child;
    }

    public Node getLender() {
        return lender;
    }

    public Node getBorrower() {
        return borrower;
    }

    public boolean isBorrowed() {
        return lender != null;
    }

    public boolean isLended() {
        return borrower != null;
    }

}
