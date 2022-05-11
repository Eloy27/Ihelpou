package com.example.ihelpou.models;

import java.util.ArrayList;

public class AvailableDays {

    ArrayList<String> availableDays;
    String startTime, finishTime;

    public AvailableDays(){
    }

    public AvailableDays(ArrayList<String> availableDays, String startTime, String finishTime) {
        this.availableDays = availableDays;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public ArrayList<String> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(ArrayList<String> availableDays) {
        this.availableDays = availableDays;
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
}
