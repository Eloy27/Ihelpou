package com.example.ihelpou.models;

import android.graphics.Bitmap;
import java.util.ArrayList;

public class Aid_Days {

    ArrayList<String> day;
    String startTime, finishTime;
    Bitmap picture;

    public Aid_Days(ArrayList<String> day, String startTime, String finishTime, Bitmap picture) {
        this.day = day;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.picture = picture;
    }

    public ArrayList<String> getDay() {
        return day;
    }

    public void setDay(ArrayList<String> day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
