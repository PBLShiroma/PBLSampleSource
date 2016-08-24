package com.example.instructor.sampleproject;

import android.app.Application;

/**
 * Created by instructor on 2016/08/09.
 */
public class LoginUser extends Application{

    private String kaiinID;
    private String userID;
    private String userType;
    private String userTypeName;
    private String userName;
    private String userAddress1;
    private String userAddress2;
    private String birthday;
    private Integer seikyu;
    private Integer point;
    private String menuIchiran;
    private boolean birthdayFlg;


    public void globalsAllInit()
    {
        kaiinID = "";
        userID = "";
        userType = "";
        userTypeName = "";
        userName = "";
        menuIchiran="";
        userAddress1="";
        userAddress2="";
        birthday="";
        seikyu=0;
        point=0;
        birthdayFlg=false;
    }

    public String getKaiinID() {
        return kaiinID;
    }

    public void setKaiinID(String kaiinID) {
        this.kaiinID = kaiinID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) { this.userName = userName; }

    public String getMenuIchiran() {
        return menuIchiran;
    }

    public void setMenuIchiran(String menuIchiran) {
        this.menuIchiran = menuIchiran;
    }

    public String getUserAddress1() {
        return userAddress1;
    }

    public void setUserAddress1(String userAddress1) {
        this.userAddress1 = userAddress1;
    }

    public String getUserAddress2() {
        return userAddress2;
    }

    public void setUserAddress2(String userAddress2) {
        this.userAddress2 = userAddress2;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getSeikyu() {
        return seikyu;
    }

    public void setSeikyu(Integer seikyu) {
        this.seikyu = seikyu;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public boolean isBirthdayFlg() {
        return birthdayFlg;
    }

    public void setBirthdayFlg(boolean birthdayFlg) {
        this.birthdayFlg = birthdayFlg;
    }
}
