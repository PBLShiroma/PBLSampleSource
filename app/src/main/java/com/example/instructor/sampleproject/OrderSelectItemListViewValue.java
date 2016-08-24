package com.example.instructor.sampleproject;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by instructor on 2016/08/05.
 */

public class OrderSelectItemListViewValue implements Serializable {

    private String itemCd;
    private String itemName;
    private Integer price;
    private Integer zaiko;
    private boolean isChecked;

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

    public Integer getZaiko() {
        return zaiko;
    }

    public void setZaiko(Integer zaiko) {
        this.zaiko = zaiko;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
