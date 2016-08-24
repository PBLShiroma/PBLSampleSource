package com.example.instructor.sampleproject;

/**
 * Created by instructor on 2016/08/05.
 */
public class UserTypeValue {
    private String userTypeCd;
    private String userTypeName;

    public UserTypeValue(String userTypeCd, String userTypeName) {
        this.userTypeCd = userTypeCd;
        this.userTypeName =userTypeName;
    }
    public String getUserTypeCd() {
        return userTypeCd;
    }

    public void setUserTypeCd(String userTypeCd) {
        this.userTypeCd = userTypeCd;
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    @Override
    public String toString() {
        return userTypeName;
    }
}
