package com.example.ihelpou.models;

import java.io.Serializable;

public class Aid implements Serializable {

    private String key;
    private String description, startTime, finishTime, day;
    private String done;
    private String idHelper;

    public Aid(){
    }

    public Aid(String description, String startTime, String finishTime, String day) {
        this.description = description;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.day = day;
    }

    public Aid(String description, String startTime, String finishTime, String day, String done, String idHelper) {
        this.description = description;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.day = day;
        this.done = done;
        this.idHelper = idHelper;
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

    public String getIdHelper() {
        return idHelper;
    }

    public void setIdHelper(String idHelper) {
        this.idHelper = idHelper;
    }

    @Override
    public String toString() {
        return "Aid{" +
                "key='" + key + '\'' +
                ", description='" + description + '\'' +
                ", startTime='" + startTime + '\'' +
                ", finishTime='" + finishTime + '\'' +
                ", day='" + day + '\'' +
                ", done='" + done + '\'' +
                ", idHelper='" + idHelper + '\'' +
                '}';
    }
}
