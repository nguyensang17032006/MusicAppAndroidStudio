package com.example.musicappdemo.model;

import java.io.Serializable;

public class Song implements Serializable {
    private String id;
    private String title;
    private String file_url;
    private String cover_url;
    private int duration;
    private int views;
    private String artist_names;
    private String genre_names;

    public Song() {}

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getFile_url() { return file_url; }
    public String getCover_url() { return cover_url; }
    public int getDuration() { return duration; }
    public int getViews() { return views; }
    public String getArtist_names() { return artist_names; }
    public String getGenre_names() { return genre_names; }
}
