package com.example.capstoneproject.Model;

public class DeliverOrPickup {
    private String orderType;

    public DeliverOrPickup() {

    }
    public DeliverOrPickup(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
