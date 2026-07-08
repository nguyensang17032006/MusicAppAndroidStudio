package com.example.musicappdemo.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.example.musicappdemo.model.Song;

import java.io.IOException;

public class MusicManager {
    private static MusicManager instance;
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private OnMusicStatusListener listener;

    public interface OnMusicStatusListener {
        void onSongChanged(Song song);
        void onStatusChanged(boolean isPlaying);
    }

    public static synchronized MusicManager getInstance() {
        if (instance == null) instance = new MusicManager();
        return instance;
    }

    public void setListener(OnMusicStatusListener listener) {
        this.listener = listener;
    }

    public void playSong(Context context, Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        currentSong = song;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());

        try {
            mediaPlayer.setDataSource(context, Uri.parse(song.getFile_url()));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                if (listener != null) {
                    listener.onSongChanged(song);
                    listener.onStatusChanged(true);
                }
            });
        } catch (IOException e) {
            Log.e("MusicManager", "Lỗi phát nhạc: " + e.getMessage());
        }
    }

    public void togglePause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                if (listener != null) listener.onStatusChanged(false);
            } else {
                mediaPlayer.start();
                if (listener != null) listener.onStatusChanged(true);
            }
        }
    }

    public Song getCurrentSong() { return currentSong; }
    public boolean isPlaying() { return mediaPlayer != null && mediaPlayer.isPlaying(); }
}
