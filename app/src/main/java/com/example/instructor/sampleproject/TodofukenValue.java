package com.example.instructor.sampleproject;

/**
 * Created by instructor on 2016/08/05.
 */
public class TodofukenValue {
    private String todofukenCd;
    private String todofukenName;

    public TodofukenValue(String todofukenCd, String todofukenName) {
        this.todofukenCd = todofukenCd;
        this.todofukenName =todofukenName;
    }

    public String getTodofukenCd() {
        return todofukenCd;
    }

    public void setTodofukenCd(String todofukenCd) {
        this.todofukenCd = todofukenCd;
    }

    public String getTodofukenName() {
        return todofukenName;
    }

    public void setTodofukenName(String todofukenName) {
        this.todofukenName = todofukenName;
    }

    @Override
    public String toString() {
        return todofukenName;
    }
}
