package com.example.musicappdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.databinding.ActivityMainBinding;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.ui.HomeFragment;
import com.example.musicappdemo.ui.LibraryFragment;
import com.example.musicappdemo.ui.SearchFragment;
import com.example.musicappdemo.utils.MusicManager;

public class MainActivity extends AppCompatActivity implements MusicManager.OnMusicStatusListener {

    private ActivityMainBinding binding;
    private Handler progressHandler = new Handler();
    private Runnable progressRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        com.example.musicappdemo.utils.LibraryManager.getInstance(this).syncAll();

        MusicManager.getInstance().setListener(this);

        updateMiniPlayerVisibility();

        // Hiển thị Trang Chủ đầu tiên
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.fragmentContainer.getId(), new HomeFragment())
                    .commit();
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (id == R.id.nav_library) {
                selectedFragment = new LibraryFragment();
            } else {
                return false;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(binding.fragmentContainer.getId(), selectedFragment)
                    .commit();
            return true;
        });

        // Xử lý khi click vào Mini Player
        binding.miniPlayerContainer.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PlayerActivity.class));
        });

        binding.miniPlayerPlay.setOnClickListener(v -> MusicManager.getInstance().togglePause());

        binding.miniPlayerClose.setOnClickListener(v -> {
            MusicManager.getInstance().stopMusic();
            updateMiniPlayerVisibility();
        });

        setupProgressUpdate();
    }

    private void setupProgressUpdate() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (MusicManager.getInstance().isPlaying()) {
                    int currentPosition = MusicManager.getInstance().getCurrentPosition();
                    int duration = MusicManager.getInstance().getDuration();
                    if (duration > 0) {
                        binding.miniPlayerProgress.setMax(duration);
                        binding.miniPlayerProgress.setProgress(currentPosition);
                    }
                }
                progressHandler.postDelayed(this, 1000);
            }
        };
        progressHandler.post(progressRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.getInstance().setListener(this);
        Song currentSong = MusicManager.getInstance().getCurrentSong();
        if (currentSong != null) {
            onSongChanged(currentSong);
            onStatusChanged(MusicManager.getInstance().isPlaying());
        }
        updateMiniPlayerVisibility();
    }

    private void updateMiniPlayerVisibility() {
        if (MusicManager.getInstance().getCurrentSong() != null) {
            binding.miniPlayerContainer.setVisibility(android.view.View.VISIBLE);
        } else {
            binding.miniPlayerContainer.setVisibility(android.view.View.GONE);
        }
    }

    @Override
    public void onSongChanged(Song song) {
        updateMiniPlayerVisibility();
        if (song != null) {
            binding.miniPlayerTitle.setText(song.getTitle());
            binding.miniPlayerArtist.setText(song.getArtist_names());
            if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
                Glide.with(this).load(song.getCover_url()).into(binding.miniPlayerImg);
            } else {
                binding.miniPlayerImg.setImageResource(R.drawable.placeholder_img);
            }
            binding.miniPlayerProgress.setProgress(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressHandler.removeCallbacks(progressRunnable);
    }

    @Override
    public void onStatusChanged(boolean isPlaying) {
        binding.miniPlayerPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }
}
