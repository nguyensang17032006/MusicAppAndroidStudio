package com.example.musicappdemo.model;

import java.io.Serializable;

public class Genre implements Serializable {
    private String id;
    private String name;

    public Genre() {}

    public Genre(String name) {
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
