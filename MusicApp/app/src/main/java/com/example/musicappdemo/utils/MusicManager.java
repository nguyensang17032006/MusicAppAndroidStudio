package com.example.musicappdemo.utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.services.MusicService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicManager {
    private static MusicManager instance;
    private MediaPlayer mediaPlayer;
    private Song currentSong;
    private List<Song> playlist = new ArrayList<>();
    private List<Song> originalPlaylist = new ArrayList<>();
    private List<Song> allSongs = new ArrayList<>();
    private int currentIndex = -1;
    private OnMusicStatusListener listener;
    private OnMusicStatusListener serviceListener;

    private boolean isShuffle = false;
    private boolean isRepeat = false;

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

    public void setServiceListener(OnMusicStatusListener listener) {
        this.serviceListener = listener;
        if (listener != null && currentSong != null) {
            listener.onSongChanged(currentSong);
            listener.onStatusChanged(isPlaying());
        }
    }

    public void setAllSongs(List<Song> allSongs) {
        this.allSongs = allSongs;
    }

    public void playSong(Context context, Song song) {
        originalPlaylist.clear();
        originalPlaylist.add(song);
        playlist.clear();
        playlist.add(song);
        currentIndex = 0;
        playCurrentIndex(context);
    }

    public void playPlaylist(Context context, List<Song> songs, int index) {
        this.originalPlaylist = new ArrayList<>(songs);
        this.playlist = new ArrayList<>(songs);
        this.currentIndex = index;
        if (isShuffle) {
            shufflePlaylist();
        }
        playCurrentIndex(context);
    }

    public void addSongToPlaylist(Context context, Song song) {
        originalPlaylist.add(song);
        playlist.add(song);
        if (playlist.size() == 1) {
            currentIndex = 0;
            playCurrentIndex(context);
        }
    }

    public void toggleShuffle(Context context) {
        isShuffle = !isShuffle;
        if (isShuffle) {
            shufflePlaylist();
        } else {
            Song current = currentSong;
            playlist = new ArrayList<>(originalPlaylist);
            if (current != null) {
                currentIndex = playlist.indexOf(current);
            }
        }
    }

    private void shufflePlaylist() {
        if (playlist.isEmpty()) return;
        Song current = currentSong;
        java.util.Collections.shuffle(playlist);
        if (current != null) {
            playlist.remove(current);
            playlist.add(0, current);
            currentIndex = 0;
        }
    }

    public void toggleRepeat() {
        isRepeat = !isRepeat;
    }

    public boolean isShuffle() { return isShuffle; }
    public boolean isRepeat() { return isRepeat; }

    private void playCurrentIndex(Context context) {
        if (currentIndex < 0 || currentIndex >= playlist.size()) {
            if (isRepeat && !playlist.isEmpty()) {
                currentIndex = 0;
            } else {
                return;
            }
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        currentSong = playlist.get(currentIndex);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());

        try {
            Log.d("MusicManager", "Đang phát: " + currentSong.getTitle() + " URL: " + currentSong.getFile_url());
            
            // Start Foreground Service to keep app alive in background
            startMusicService(context);
            
            mediaPlayer.setDataSource(context, Uri.parse(currentSong.getFile_url()));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                if (listener != null) {
                    listener.onSongChanged(currentSong);
                    listener.onStatusChanged(true);
                }
                if (serviceListener != null) {
                    serviceListener.onSongChanged(currentSong);
                    serviceListener.onStatusChanged(true);
                }
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MusicManager", "MediaPlayer Error: what=" + what + " extra=" + extra);
                return true; // Handle error and prevent triggering onCompletion
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                try {
                    int currentPos = mp.getCurrentPosition();
                    int duration = mp.getDuration();
                    if (currentPos < 2000 && duration > 5000) {
                        return;
                    }
                } catch (Exception e) {
                    Log.e("MusicManager", "Error checking duration in onCompletion: " + e.getMessage());
                }

                if (isRepeat && !isShuffle && playlist.size() == 1) {
                    playCurrentIndex(context); // Repeat single song
                } else {
                    nextSong(context);
                }
            });
        } catch (IOException e) {
            Log.e("MusicManager", "Lỗi phát nhạc: " + e.getMessage());
        }
    }

    public void nextSong(Context context) {
        if (playlist.isEmpty()) return;
        if (playlist.size() == 1) {
            playRandomSong(context);
        } else {
            currentIndex = (currentIndex + 1) % playlist.size();
            playCurrentIndex(context);
        }
    }

    public void previousSong(Context context) {
        if (playlist.isEmpty()) return;
        if (playlist.size() == 1) {
            playRandomSong(context);
        } else {
            currentIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
            playCurrentIndex(context);
        }
    }

    private void playRandomSong(Context context) {
        if (allSongs == null || allSongs.isEmpty()) {
            playCurrentIndex(context);
            return;
        }

        java.util.Random random = new java.util.Random();
        Song nextSong = currentSong;

        if (allSongs.size() > 1) {
            int maxAttempts = 10;
            int attempts = 0;
            while (attempts < maxAttempts && (nextSong == null || nextSong.getId().equals(currentSong.getId()))) {
                nextSong = allSongs.get(random.nextInt(allSongs.size()));
                attempts++;
            }
        } else {
            nextSong = allSongs.get(0);
        }

        playSong(context, nextSong);
    }

    public List<Song> getPlaylist() {
        return playlist;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Exception e) {
                Log.e("MusicManager", "Error stopping mediaPlayer: " + e.getMessage());
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        currentSong = null;
        currentIndex = -1;
        playlist.clear();
        originalPlaylist.clear();
        if (listener != null) {
            listener.onSongChanged(null);
            listener.onStatusChanged(false);
        }
        if (serviceListener != null) {
            serviceListener.onSongChanged(null);
            serviceListener.onStatusChanged(false);
        }
    }

    public void togglePause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                if (listener != null) listener.onStatusChanged(false);
                if (serviceListener != null) serviceListener.onStatusChanged(false);
            } else {
                mediaPlayer.start();
                if (listener != null) listener.onStatusChanged(true);
                if (serviceListener != null) serviceListener.onStatusChanged(true);
            }
        }
    }

    private void startMusicService(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), MusicService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public Song getCurrentSong() { return currentSong; }
    public boolean isPlaying() { return mediaPlayer != null && mediaPlayer.isPlaying(); }

    public int getCurrentPosition() {
        if (mediaPlayer != null) return mediaPlayer.getCurrentPosition();
        return 0;
    }

    public int getDuration() {
        if (mediaPlayer != null) return mediaPlayer.getDuration();
        return 0;
    }

    public void seekTo(int position) {
        if (mediaPlayer != null) mediaPlayer.seekTo(position);
    }
}