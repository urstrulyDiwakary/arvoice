package com.arvoice.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentUserModel {

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("profile")
    @Expose
    private String profile;

    @SerializedName("role")
    @Expose
    private String role;

    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("reportingTo")
    @Expose
    private String reportingTo;

    @SerializedName("userStatus")
    @Expose
    private String userStatus;

    @SerializedName("loginAllowed")
    @Expose
    private String loginAllowed;

    @SerializedName("isLocked")
    @Expose
    private Boolean isLocked;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("userid")
    @Expose
    private String userid;

    // Getters and Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getReportingTo() {
        return reportingTo;
    }

    public void setReportingTo(String reportingTo) {
        this.reportingTo = reportingTo;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getLoginAllowed() {
        return loginAllowed;
    }

    public void setLoginAllowed(String loginAllowed) {
        this.loginAllowed = loginAllowed;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
