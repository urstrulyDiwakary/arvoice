package com.arvoice.model;

import org.json.JSONException;
import org.json.JSONObject;

public class CallLogItem {
    public String userName;
    public String contactName;
    public String contactPhoneNo;
    public String simInfo;
    public String startTime;
    public String endTime;
    public String callType;
    public String callStatus;
    public int callDuration;
    boolean answered;
    boolean unanswered;
    public String  callerPhone;
    public  String callerName;

    public CallLogItem(String userName, String contactName,String contactPhoneNo, String simInfo,
                       String startTime, String endTime,String callType, String callStatus,
                       int callDuration, boolean answered, boolean unanswered,String callerPhone,String callerName) {
        this.userName = userName;
        this.contactName = contactName;
        this.contactPhoneNo = contactPhoneNo;
        this.simInfo = simInfo;
        this.startTime = startTime;
        this.endTime = endTime;
        this.callType = callType;
        this.callStatus = callStatus;
        this.callDuration = callDuration;
        this.answered = answered;
        this.unanswered = unanswered;
        this.callerPhone = callerPhone;
        this.callerName = callerName;
    }
    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("callerName", callerName);
            obj.put("startTime", startTime);
            obj.put("contactName", contactName); // or set another contact field
            obj.put("contactPhoneNumber", contactPhoneNo);
            obj.put("callType", callType);
            obj.put("callDuration", callDuration);
            obj.put("feedback", "");
            obj.put("notes", "");
            obj.put("callerPhoneNo", callerPhone);
            obj.put("endTime", endTime); // calculate or set
            obj.put("lead", "");
            obj.put("answered", answered);
            obj.put("unanswered", unanswered);
            obj.put("size", 0);
            obj.put("callStatus", callStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
