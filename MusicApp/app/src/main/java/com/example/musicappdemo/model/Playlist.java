package com.example.musicappdemo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Serializable {
    private String id;
    private String name;
    private List<Song> songs;

    public Playlist(String id, String name) {
        this.id = id;
        this.name = name;
        this.songs = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Song> getSongs() { return songs; }
    public void setSongs(List<Song> songs) { this.songs = songs; }

    public void addSong(Song song) {
        if (songs == null) songs = new ArrayList<>();
        // Check if song already exists to avoid duplicates
        for (Song s : songs) {
            if (s.getId().equals(song.getId())) return;
        }
        songs.add(song);
    }
}
