package com.example.musicappdemo.model;

import java.io.Serializable;

public class Artist implements Serializable {
    private String id;
    private String name;
    private String image_url;
    private long addedTime;
    private boolean is_main_artist;

    public Artist() {}

    public Artist(String id, String name, String image_url, long addedTime, boolean is_main_artist) {
        this.id = id;
        this.name = name;
        this.image_url = image_url;
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

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
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
