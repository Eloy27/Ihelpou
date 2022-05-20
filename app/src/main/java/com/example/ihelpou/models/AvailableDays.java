package com.example.ihelpou.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class AvailableDays implements Serializable {

    private String key;
    private ArrayList<HashMap<String, String>> availableDays = new ArrayList<>();
    private String startTime, finishTime;

    public AvailableDays(){
    }

    public AvailableDays(String key, ArrayList<HashMap<String, String>> availableDays, String startTime, String finishTime) {
        this.key = key;
        this.availableDays = availableDays;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public AvailableDays(ArrayList<HashMap<String, String>> availableDays, String startTime, String finishTime) {
        this.availableDays = availableDays;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<HashMap<String, String>> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(ArrayList<HashMap<String, String>> availableDays) {
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

    @Override
    public String toString() {
        return "AvailableDays{" +
                "key='" + key + '\'' +
                ", availableDays=" + availableDays +
                ", startTime='" + startTime + '\'' +
                ", finishTime='" + finishTime + '\'' +
                '}';
    }
}
