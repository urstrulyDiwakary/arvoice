package com.arvoice.model;

import com.google.gson.annotations.SerializedName;

// LoginResponse.java
public class LoginResponse {
    @SerializedName("token")
    private String token;

    @SerializedName("userName")
    private String username;

    @SerializedName("role")
    private String role;

    @SerializedName("userId")
    private String userId;

    @SerializedName("email")
    private String email;

    @SerializedName("error")
    private String error;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
