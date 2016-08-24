package com.example.instructor.sampleproject;

/**
 * Created by instructor on 2016/08/05.
 */
public class OrderListViewValue {

    private Integer orderId;
    private Integer kaiinId;
    private String userId;
    private String name;
    private String userAddress;

    private String itemCd;
    private String itemName;
    private Integer price;
    private Integer zaiko;
    private Integer kosu;
    private String orderStatus;
    private String orderDate;


    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getKaiinId() {
        return kaiinId;
    }

    public void setKaiinId(Integer kaiinId) {
        this.kaiinId = kaiinId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getItemCd() {
        return itemCd;
    }

    public void setItemCd(String itemCd) {
        this.itemCd = itemCd;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getKosu() {
        return kosu;
    }

    public void setKosu(Integer kosu) {
        this.kosu = kosu;
    }

    public Integer getZaiko() {
        return zaiko;
    }

    public void setZaiko(Integer zaiko) {
        this.zaiko = zaiko;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

}
