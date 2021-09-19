package com.example.capstoneproject.Model;

public class DeliverOrDineIn {
    private String orderType;

    public DeliverOrDineIn() {

    }
    public DeliverOrDineIn(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
