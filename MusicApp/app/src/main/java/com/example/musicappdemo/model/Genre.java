package com.example.musicappdemo.model;

public class Genre {
    private String id;
    private String name;

    public Genre() {}

    public Genre(String name) {
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
