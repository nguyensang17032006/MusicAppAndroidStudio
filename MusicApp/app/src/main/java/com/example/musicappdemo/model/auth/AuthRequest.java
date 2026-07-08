package com.example.musicappdemo.model.auth;

public class AuthRequest {
    private String email;
    private String password;
    private String gender;

    public AuthRequest() {}

    public AuthRequest(String email, String password, String gender) {
        this.email = email;
        this.password = password;
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
