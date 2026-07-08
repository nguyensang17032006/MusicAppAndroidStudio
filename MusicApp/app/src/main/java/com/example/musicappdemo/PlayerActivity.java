package com.example.musicappdemo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.musicappdemo.databinding.ActivityPlayerBinding;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.utils.MusicManager;

public class PlayerActivity extends AppCompatActivity implements MusicManager.OnMusicStatusListener {

    private ActivityPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MusicManager.getInstance().setListener(this);

        binding.btnCollapse.setOnClickListener(v -> finish());

        binding.fabPlay.setOnClickListener(v -> MusicManager.getInstance().togglePause());

        updateUI(MusicManager.getInstance().getCurrentSong());
        updatePlayStatus(MusicManager.getInstance().isPlaying());
    }

    private void updateUI(Song song) {
        if (song != null) {
            binding.tvSongTitle.setText(song.getTitle());
            binding.tvArtistName.setText(song.getArtist_names());
            if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
                Glide.with(this).load(song.getCover_url()).into(binding.imgArtwork);
            } else {
                binding.imgArtwork.setImageResource(R.drawable.placeholder_img);
            }
        }
    }

    private void updatePlayStatus(boolean isPlaying) {
        binding.fabPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    @Override
    public void onSongChanged(Song song) {
        updateUI(song);
    }

    @Override
    public void onStatusChanged(boolean isPlaying) {
        updatePlayStatus(isPlaying);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.getInstance().setListener(this);
        updateUI(MusicManager.getInstance().getCurrentSong());
        updatePlayStatus(MusicManager.getInstance().isPlaying());
    }
}
