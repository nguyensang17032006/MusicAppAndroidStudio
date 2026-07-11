package com.example.musicappdemo.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.MainActivity;
import com.example.musicappdemo.R;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.utils.MusicManager;

public class MusicService extends Service implements MusicManager.OnMusicStatusListener {
    private static final String CHANNEL_ID = "music_channel";
    private static final int NOTIFICATION_ID = 101;

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_PREV = "ACTION_PREV";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        MusicManager.getInstance().setServiceListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            Log.d("MusicService", "Received Action: " + action);
            switch (action) {
                case ACTION_PLAY:
                case ACTION_PAUSE:
                    MusicManager.getInstance().togglePause();
                    break;
                case ACTION_NEXT:
                    MusicManager.getInstance().nextSong(getApplicationContext());
                    break;
                case ACTION_PREV:
                    MusicManager.getInstance().previousSong(getApplicationContext());
                    break;
            }
        }

        // Show/Update Notification
        Song currentSong = MusicManager.getInstance().getCurrentSong();
        if (currentSong != null) {
            showNotification(currentSong, MusicManager.getInstance().isPlaying());
        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void showNotification(Song song, boolean isPlaying) {
        // 1. Build initial notification (with placeholder cover or no cover yet)
        NotificationCompat.Builder builder = createNotificationBuilder(song, isPlaying, null);
        startForeground(NOTIFICATION_ID, builder.build());

        // 2. Load cover image asynchronously using Glide
        if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
            new Thread(() -> {
                try {
                    Bitmap bitmap = Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(song.getCover_url())
                            .submit()
                            .get();
                    
                    // Update notification with the loaded cover art
                    NotificationCompat.Builder updatedBuilder = createNotificationBuilder(song, isPlaying, bitmap);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (manager != null) {
                        manager.notify(NOTIFICATION_ID, updatedBuilder.build());
                    }
                } catch (Exception e) {
                    Log.e("MusicService", "Error loading cover art: " + e.getMessage());
                }
            }).start();
        }
    }

    private NotificationCompat.Builder createNotificationBuilder(Song song, boolean isPlaying, Bitmap coverBitmap) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        PendingIntent playPausePending = getPendingIntentAction(isPlaying ? ACTION_PAUSE : ACTION_PLAY);
        PendingIntent nextPending = getPendingIntentAction(ACTION_NEXT);
        PendingIntent prevPending = getPendingIntentAction(ACTION_PREV);

        int playPauseIcon = isPlaying ? R.drawable.ic_pause : R.drawable.ic_play;
        String playPauseTitle = isPlaying ? "Pause" : "Play";

        String artistName = (song.getArtists() != null && !song.getArtists().isEmpty()) ? song.getArtists().get(0).getName() : "Unknown Artist";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(song.getTitle())
                .setContentText(artistName)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2));

        if (coverBitmap != null) {
            builder.setLargeIcon(coverBitmap);
        } else {
            try {
                Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder_img);
                if (placeholder != null) {
                    builder.setLargeIcon(placeholder);
                }
            } catch (Exception e) {
                // Ignore fallback
            }
        }

        builder.addAction(R.drawable.ic_previous, "Previous", prevPending);
        builder.addAction(playPauseIcon, playPauseTitle, playPausePending);
        builder.addAction(R.drawable.ic_next, "Next", nextPending);

        return builder;
    }

    private PendingIntent getPendingIntentAction(String action) {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(action);
        int requestCode = 0;
        if (action != null) {
            switch (action) {
                case ACTION_PLAY:
                case ACTION_PAUSE:
                    requestCode = 1;
                    break;
                case ACTION_NEXT:
                    requestCode = 2;
                    break;
                case ACTION_PREV:
                    requestCode = 3;
                    break;
            }
        }
        return PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MusicManager.getInstance().setServiceListener(null);
        stopForeground(true);
    }

    @Override
    public void onSongChanged(Song song) {
        if (song == null) {
            stopSelf();
        } else {
            showNotification(song, MusicManager.getInstance().isPlaying());
        }
    }

    @Override
    public void onStatusChanged(boolean isPlaying) {
        Song currentSong = MusicManager.getInstance().getCurrentSong();
        if (currentSong != null) {
            showNotification(currentSong, isPlaying);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
