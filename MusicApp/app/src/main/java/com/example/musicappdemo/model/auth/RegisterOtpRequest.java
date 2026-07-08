package com.example.musicappdemo.model.auth;

public class RegisterOtpRequest
{
    private String email;
    private String token;
    private String password;
    private String gender;

    public RegisterOtpRequest(String email, String token, String password, String gender) {
        this.email = email;
        this.token = token;
        this.password = password;
        this.gender = gender;
    }
}
