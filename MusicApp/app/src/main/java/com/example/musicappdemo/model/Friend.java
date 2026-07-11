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

    @SerializedName("isOnline")
    private boolean isOnline;

    @SerializedName("currentSong")
    private String currentSong;

    @SerializedName("today_listening_time")
    private int todayListeningTime;

    public int getTodayListeningTime() {
        return todayListeningTime;
    }

    public void setTodayListeningTime(int todayListeningTime) {
        this.todayListeningTime = todayListeningTime;
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

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getCurrentSong() {
        return currentSong;
    }

    public void setCurrentSong(String currentSong) {
        this.currentSong = currentSong;
    }
}
