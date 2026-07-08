package com.example.musicappdemo.network;

import com.example.musicappdemo.model.Genre;
import com.example.musicappdemo.model.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MusicApiService {
    @GET("api/songs")
    Call<List<Song>> getSongs();

    @GET("api/genres")
    Call<List<Genre>> getGenres();
}
