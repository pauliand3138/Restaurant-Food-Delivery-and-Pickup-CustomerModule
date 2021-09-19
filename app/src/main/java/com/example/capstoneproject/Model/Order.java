package com.example.capstoneproject.Model;

import java.util.List;

public class Order {
    private String custID;
    private String custTelNo;
    private String custName;
    private String orderAddress;
    private String orderType;
    private String orderPrice;
    private List<CartDetail> foods; //List of food order

    public Order() {
    }

    public Order(String custID, String custTelNo, String custName, String orderAddress, String orderType, String orderPrice, List<CartDetail> foods) {
        this.custID = custID;
        this.custTelNo = custTelNo;
        this.custName = custName;
        this.orderAddress = orderAddress;
        this.orderType = orderType;
        this.orderPrice = orderPrice;
        this.foods = foods;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public String getCustTelNo() {
        return custTelNo;
    }

    public void setCustTelNo(String custTelNo) {
        this.custTelNo = custTelNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public List<CartDetail> getFoods() {
        return foods;
    }

    public void setFoods(List<CartDetail> foods) {
        this.foods = foods;
    }
}
