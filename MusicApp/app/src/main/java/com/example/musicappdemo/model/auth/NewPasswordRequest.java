package com.example.musicappdemo.model.auth;

import com.google.gson.annotations.SerializedName;

public class NewPasswordRequest {

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("newPassword")
    private String newPassword;

    // Constructor không tham số (Bắt buộc cho GSON)
    public NewPasswordRequest() {
    }

    // Constructor đầy đủ tham số để khởi tạo nhanh khi gọi API
    public NewPasswordRequest(String accessToken, String newPassword) {
        this.accessToken = accessToken;
        this.newPassword = newPassword;
    }

    // Getter và Setter
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}