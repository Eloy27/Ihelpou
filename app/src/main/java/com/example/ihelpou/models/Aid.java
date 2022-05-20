package com.example.ihelpou.models;

import java.io.Serializable;

public class Aid implements Serializable {

    private String key;
    private String description, startTime, finishTime, day;
    private String done;

    public Aid(){
    }

    public Aid(String key, String description, String startTime, String finishTime, String day, String done) {
        this.description = description;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.day = day;
        this.done = done;
    }

    public Aid(String description, String startTime, String finishTime, String day, String done) {
        this.description = description;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.day = day;
        this.done = done;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDone() {
        return done;
    }

    public void setDone(String done) {
        this.done = done;
    }
}
