package com.example.musicappdemo.model.auth;

import com.google.gson.annotations.SerializedName;

public class SupabaseUser {
    @SerializedName("id")
    private String id;
    @SerializedName("email")
    private String email;
    @SerializedName("email_confirmed_at")
    private String emailConfirmedAt;
    @SerializedName("last_sign_in_at")
    private String lastSignInAt;
    @SerializedName("created_at")
    private String createdAt;

    public SupabaseUser() {
    }

    public SupabaseUser(String id, String email, String emailConfirmedAt, String lastSignInAt, String createdAt) {
        this.id = id;
        this.email = email;
        this.emailConfirmedAt = emailConfirmedAt;
        this.lastSignInAt = lastSignInAt;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailConfirmedAt() {
        return emailConfirmedAt;
    }

    public void setEmailConfirmedAt(String emailConfirmedAt) {
        this.emailConfirmedAt = emailConfirmedAt;
    }

    public String getLastSignInAt() {
        return lastSignInAt;
    }

    public void setLastSignInAt(String lastSignInAt) {
        this.lastSignInAt = lastSignInAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
