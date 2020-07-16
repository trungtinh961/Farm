package com.example.farm.model;

/**
 * Created by Trung Tinh on 7/15/2020.
 */
public class Alert {
    public int temparature;
    public String time;

    public Alert() {}

    public Alert(int temparature, String time) {
        this.temparature = temparature;
        this.time = time;
    }

    public int getTemparature() {
        return temparature;
    }

    public void setTemparature(int temparature) {
        this.temparature = temparature;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
