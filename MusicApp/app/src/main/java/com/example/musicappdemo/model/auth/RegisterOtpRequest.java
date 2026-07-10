package com.example.musicappdemo.model.auth;

import com.google.gson.annotations.SerializedName;

public class RegisterOtpRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("otp")
    private String otp;

    @SerializedName("password")
    private String password;

    @SerializedName("gender")
    private String gender;

    public RegisterOtpRequest(String email, String otp, String password, String gender) {
        this.email = email;
        this.otp = otp;
        this.password = password;
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
