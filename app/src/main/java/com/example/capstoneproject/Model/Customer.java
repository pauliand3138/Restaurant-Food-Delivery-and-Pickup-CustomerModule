package com.example.capstoneproject.Model;

public class Customer {
    private String custID;
    private String custName;
    private String custPassword;
    private String custTelNo;


    public Customer() {

    }

    public Customer(String n, String p, String t) {
        custName = n;
        custPassword = p;
        custTelNo = t;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public String getCustName() {
        return custName;
    }

    public String getCustPassword() {
        return custPassword;
    }

    public String getCustTelNo() {
        return custTelNo;
    }

    public void setCustName(String n) {
        custName = n;
    }

    public void setCustPassword(String p) {
        custPassword = p;
    }

    public void setCustTelNo(String t) { custTelNo = t; }

}
