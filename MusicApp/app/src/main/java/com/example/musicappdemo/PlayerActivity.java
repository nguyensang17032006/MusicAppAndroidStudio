package com.example.musicappdemo;

import android.os.Bundle;
import android.os.Handler;
import android.widget.SeekBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.adapter.PlaylistAdapter;
import com.example.musicappdemo.databinding.ActivityPlayerBinding;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.utils.MusicManager;

import java.util.Locale;

public class PlayerActivity extends AppCompatActivity implements MusicManager.OnMusicStatusListener {

    private ActivityPlayerBinding binding;
    private PlaylistAdapter playlistAdapter;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MusicManager.getInstance().setListener(this);

        binding.btnCollapse.setOnClickListener(v -> finish());
        binding.fabPlay.setOnClickListener(v -> MusicManager.getInstance().togglePause());
        binding.btnNext.setOnClickListener(v -> MusicManager.getInstance().nextSong(this));
        binding.btnPrevious.setOnClickListener(v -> MusicManager.getInstance().previousSong(this));

        binding.btnShuffle.setOnClickListener(v -> {
            MusicManager.getInstance().toggleShuffle(this);
            updateControlButtons();
            if (playlistAdapter != null) {
                playlistAdapter.notifyDataSetChanged(); // Playlist order might change
            }
        });

        binding.btnRepeat.setOnClickListener(v -> {
            MusicManager.getInstance().toggleRepeat();
            updateControlButtons();
        });

        setupPlaylist();
        updateControlButtons();

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    MusicManager.getInstance().seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        updateUI(MusicManager.getInstance().getCurrentSong());
        updatePlayStatus(MusicManager.getInstance().isPlaying());
        startSeekBarUpdate();
    }

    private void setupPlaylist() {
        playlistAdapter = new PlaylistAdapter(this, MusicManager.getInstance().getPlaylist());
        binding.rvPlaylist.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPlaylist.setAdapter(playlistAdapter);
        playlistAdapter.setPlayingIndex(MusicManager.getInstance().getCurrentIndex());
    }

    private void updateUI(Song song) {
        if (song != null) {
            String artistName = (song.getArtists() != null && !song.getArtists().isEmpty()) ? song.getArtists().get(0).getName() : "Unknown Artist";

            binding.tvSongTitle.setText(song.getTitle());
            binding.tvArtistName.setText(artistName);
            if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
                Glide.with(this).load(com.example.musicappdemo.data.RetrofitClient.getFullUrl(song.getCover_url())).into(binding.imgArtwork);
            } else {
                binding.imgArtwork.setImageResource(R.drawable.placeholder_img);
            }

            // Reset progress UI immediately to avoid showing old values during startup lag
            binding.seekBar.setProgress(0);
            binding.tvCurrentTime.setText("00:00");

            int duration = MusicManager.getInstance().getDuration();
            binding.seekBar.setMax(duration);
            binding.tvTotalTime.setText(formatTime(duration));
        }
    }

    private void updatePlayStatus(boolean isPlaying) {
        binding.fabPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    private void updateControlButtons() {
        int colorActive = ContextCompat.getColor(this, R.color.P60);
        int colorInactive = ContextCompat.getColor(this, R.color.white);

        binding.btnShuffle.setColorFilter(MusicManager.getInstance().isShuffle() ? colorActive : colorInactive);
        binding.btnRepeat.setColorFilter(MusicManager.getInstance().isRepeat() ? colorActive : colorInactive);
    }

    private void startSeekBarUpdate() {
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (MusicManager.getInstance().isPlaying()) {
                    int currentPosition = MusicManager.getInstance().getCurrentPosition();
                    binding.seekBar.setProgress(currentPosition);
                    binding.tvCurrentTime.setText(formatTime(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateSeekBarRunnable);
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    public void onSongChanged(Song song) {
        updateUI(song);
        if (song != null) {
            String currentUserId = com.example.musicappdemo.data.SessionManager.get(this).getUserId();
            com.example.musicappdemo.data.SocketManager.getInstance().emitPlayingSong(currentUserId, song.getTitle());
        }
        
        if (playlistAdapter != null) {
            playlistAdapter.setPlayingIndex(MusicManager.getInstance().getCurrentIndex());
            binding.rvPlaylist.smoothScrollToPosition(MusicManager.getInstance().getCurrentIndex());
        }
    }

    @Override
    public void onStatusChanged(boolean isPlaying) {
        updatePlayStatus(isPlaying);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.getInstance().setListener(this);
        Song currentSong = MusicManager.getInstance().getCurrentSong();
        if (currentSong != null) {
            updateUI(currentSong);
            updatePlayStatus(MusicManager.getInstance().isPlaying());
            updateControlButtons();

            // Sync with current playing position immediately on resume
            int currentPos = MusicManager.getInstance().getCurrentPosition();
            binding.seekBar.setProgress(currentPos);
            binding.tvCurrentTime.setText(formatTime(currentPos));

            if (playlistAdapter != null) {
                playlistAdapter.setPlayingIndex(MusicManager.getInstance().getCurrentIndex());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateSeekBarRunnable);
    }
}
