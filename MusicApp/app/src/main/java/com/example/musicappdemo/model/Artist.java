package com.example.musicappdemo.model;

import java.io.Serializable;

public class Artist implements Serializable {
    private String id;
    private String name;
    private String image_url;
    private long addedTime; // For "Recently Added" sorting

    public Artist() {}

    public Artist(String id, String name) {
        this.id = id;
        this.name = name;
        this.addedTime = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImage_url() { return image_url; }
    public long getAddedTime() { return addedTime; }
}
