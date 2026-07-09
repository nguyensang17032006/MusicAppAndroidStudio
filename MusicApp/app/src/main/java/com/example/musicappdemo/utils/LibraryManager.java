package com.example.musicappdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.musicappdemo.model.Artist;
import com.example.musicappdemo.model.Playlist;
import com.example.musicappdemo.model.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LibraryManager {
    private static final String PREFS_NAME = "LibraryPrefs";
    private static final String KEY_LIKED_SONGS = "LikedSongs";
    private static final String KEY_PLAYLISTS = "UserPlaylists";
    private static final String KEY_FOLLOWED_ARTISTS = "FollowedArtists";

    private static LibraryManager instance;
    private SharedPreferences prefs;
    private Gson gson;

    private LibraryManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized LibraryManager getInstance(Context context) {
        if (instance == null) {
            instance = new LibraryManager(context);
        }
        return instance;
    }

    public List<Song> getLikedSongs() {
        String json = prefs.getString(KEY_LIKED_SONGS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Song>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void addLikedSong(Song song) {
        List<Song> songs = getLikedSongs();
        for (Song s : songs) {
            if (s.getId().equals(song.getId())) return;
        }
        songs.add(song);
        prefs.edit().putString(KEY_LIKED_SONGS, gson.toJson(songs)).apply();
    }

    public void removeLikedSong(String songId) {
        List<Song> songs = getLikedSongs();
        songs.removeIf(s -> s.getId().equals(songId));
        prefs.edit().putString(KEY_LIKED_SONGS, gson.toJson(songs)).apply();
    }

    public boolean isLiked(String songId) {
        List<Song> songs = getLikedSongs();
        for (Song s : songs) {
            if (s.getId().equals(songId)) return true;
        }
        return false;
    }

    public List<Playlist> getPlaylists() {
        String json = prefs.getString(KEY_PLAYLISTS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Playlist>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void createPlaylist(String name) {
        List<Playlist> playlists = getPlaylists();
        String id = String.valueOf(System.currentTimeMillis());
        playlists.add(new Playlist(id, name));
        prefs.edit().putString(KEY_PLAYLISTS, gson.toJson(playlists)).apply();
    }

    public void addSongToPlaylist(String playlistId, Song song) {
        List<Playlist> playlists = getPlaylists();
        for (Playlist p : playlists) {
            if (p.getId().equals(playlistId)) {
                p.addSong(song);
                break;
            }
        }
        prefs.edit().putString(KEY_PLAYLISTS, gson.toJson(playlists)).apply();
    }

    public List<Artist> getFollowedArtists() {
        String json = prefs.getString(KEY_FOLLOWED_ARTISTS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Artist>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void followArtist(Artist artist) {
        List<Artist> artists = getFollowedArtists();
        for (Artist a : artists) {
            if (a.getName().equalsIgnoreCase(artist.getName())) return;
        }
        artists.add(artist);
        prefs.edit().putString(KEY_FOLLOWED_ARTISTS, gson.toJson(artists)).apply();
    }
}
