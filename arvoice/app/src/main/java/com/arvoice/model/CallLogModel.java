package com.arvoice.model;

import java.util.Date;

public class CallLogModel {
    public String number;
    public String type;
    public int duration;
    public Date date;
    public String simInfo; // NEW

    public CallLogModel(String number, String type, int duration, Date date, String simInfo) {
        this.number = number;
        this.type = type;
        this.duration = duration;
        this.date = date;
        this.simInfo = simInfo;
    }
}
