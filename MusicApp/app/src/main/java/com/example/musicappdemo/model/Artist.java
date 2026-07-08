package com.example.musicappdemo.model;

import java.io.Serializable;

public class Artist implements Serializable {
    private String name;
    private long addedTime; // For "Recently Added" sorting

    public Artist(String name) {
        this.name = name;
        this.addedTime = System.currentTimeMillis();
    }

    public String getName() { return name; }
    public long getAddedTime() { return addedTime; }
}
