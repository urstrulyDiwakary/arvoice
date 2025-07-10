package com.arvoice.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("username")
    private final String username;

    @SerializedName("password")
    private final String password;

    @SerializedName("fcm_token")
    private final String fcmToken;

    public LoginRequest(String username, String password, String fcmToken) {
        this.username = username;
        this.password = password;
        this.fcmToken = fcmToken;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFcmToken() {
        return fcmToken;
    }
}
