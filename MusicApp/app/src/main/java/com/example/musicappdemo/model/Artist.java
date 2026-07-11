package com.example.musicappdemo.model;

import java.io.Serializable;

public class Artist implements Serializable {
    private String id;
    private String name;
    private String avatar_url;
    private long addedTime;
    private boolean is_main_artist;

    public Artist() {}

    public Artist(String id, String name, String avatar_url, long addedTime, boolean is_main_artist) {
        this.id = id;
        this.name = name;
        this.avatar_url = avatar_url;
        this.addedTime = addedTime;
        this.is_main_artist = is_main_artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(long addedTime) {
        this.addedTime = addedTime;
    }

    public boolean isIs_main_artist() {
        return is_main_artist;
    }

    public void setIs_main_artist(boolean is_main_artist) {
        this.is_main_artist = is_main_artist;
    }
}
