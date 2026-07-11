package com.example.musicappdemo.model;

import com.google.gson.annotations.SerializedName;

public class UserStreak {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("current_streak")
    private int currentStreak;

    @SerializedName("max_streak")
    private int maxStreak;

    @SerializedName("last_completed_date")
    private String lastCompletedDate;

    @SerializedName("today_listening_time")
    private int todayListeningTime;

    public UserStreak() {}

    public UserStreak(String userId, int currentStreak, int maxStreak, String lastCompletedDate, int todayListeningTime) {
        this.userId = userId;
        this.currentStreak = currentStreak;
        this.maxStreak = maxStreak;
        this.lastCompletedDate = lastCompletedDate;
        this.todayListeningTime = todayListeningTime;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getMaxStreak() { return maxStreak; }
    public void setMaxStreak(int maxStreak) { this.maxStreak = maxStreak; }

    public String getLastCompletedDate() { return lastCompletedDate; }
    public void setLastCompletedDate(String lastCompletedDate) { this.lastCompletedDate = lastCompletedDate; }

    public int getTodayListeningTime() { return todayListeningTime; }
    public void setTodayListeningTime(int todayListeningTime) { this.todayListeningTime = todayListeningTime; }
}
