package com.example.instructor.sampleproject;

/**
 * Created by instructor on 2016/08/05.
 */
public class OrderStatusValue {
    private String orderStatusCd;
    private String orderStatusName;

    public OrderStatusValue(String orderStatusCd, String orderStatusName) {
        this.orderStatusCd = orderStatusCd;
        this.orderStatusName =orderStatusName;
    }

    public String getOrderStatusCd() {
        return orderStatusCd;
    }

    public void setOrderStatusCd(String orderStatusCd) {
        this.orderStatusCd = orderStatusCd;
    }

    public String getOrderStatusName() {
        return orderStatusName;
    }

    public void setOrderStatusName(String orderStatusName) {
        this.orderStatusName = orderStatusName;
    }

    @Override
    public String toString() {
        return orderStatusName;
    }
}
