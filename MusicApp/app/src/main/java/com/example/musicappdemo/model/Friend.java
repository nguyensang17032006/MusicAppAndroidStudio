package com.example.musicappdemo.model;

import com.google.gson.annotations.SerializedName;

public class Friend {
    @SerializedName("id")
    private String id;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("avatar_url")
    private String avatarUrl;
    
    @SerializedName("streak")
    private int streak;

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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }
}
