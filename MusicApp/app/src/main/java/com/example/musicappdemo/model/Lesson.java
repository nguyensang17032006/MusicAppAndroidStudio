package com.example.musicappdemo.model;

public class Lesson {
    private String title;
    private String info;

    public Lesson(String title, String info) {
        this.title = title;
        this.info = info;
    }

    public String getTitle() { return title; }
    public String getInfo() { return info; }
}