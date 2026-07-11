package com.example.musicappdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.R;
import com.example.musicappdemo.adapter.SearchResultAdapter;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.databinding.ActivityArtistDetailBinding;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.utils.MusicManager;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.data.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistDetailActivity extends AppCompatActivity implements MusicManager.OnMusicStatusListener {

    private ActivityArtistDetailBinding binding;
    private SearchResultAdapter adapter;
    private List<Song> artistSongs = new ArrayList<>();
    private String artistName;
    private String artistAvatar;
    private Handler progressHandler = new Handler();
    private Runnable progressRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtistDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        artistName = getIntent().getStringExtra("artist_name");
        artistAvatar = getIntent().getStringExtra("artist_avatar");

        if (artistName == null) {
            finish();
            return;
        }

        binding.tvArtistNameLarge.setText(artistName);
        binding.btnBack.setOnClickListener(v -> finish());

        if (artistAvatar != null && !artistAvatar.isEmpty()) {
            Glide.with(this)
                    .load(RetrofitClient.getFullUrl(artistAvatar))
                    .placeholder(R.drawable.placeholder_img)
                    .into(binding.ivArtistHeader);
        }

        binding.fabPlayArtist.setOnClickListener(v -> {
            if (!artistSongs.isEmpty()) {
                com.example.musicappdemo.utils.MusicManager.getInstance().playPlaylist(this, artistSongs, 0);
            } else {
                Toast.makeText(this, "Nghệ sĩ chưa có bài hát nào", Toast.LENGTH_SHORT).show();
            }
        });

        setupRecyclerView();
        setupMiniPlayer();
        fetchSongs();
    }

    private void setupMiniPlayer() {
        MusicManager.getInstance().setListener(this);
        updateMiniPlayerUI();

        binding.miniPlayer.miniPlayerContainer.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.musicappdemo.PlayerActivity.class));
        });

        binding.miniPlayer.miniPlayerPlay.setOnClickListener(v -> MusicManager.getInstance().togglePause());

        binding.miniPlayer.miniPlayerClose.setOnClickListener(v -> {
            MusicManager.getInstance().stopMusic();
            updateMiniPlayerUI();
        });

        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (MusicManager.getInstance().isPlaying()) {
                    int currentPosition = MusicManager.getInstance().getCurrentPosition();
                    int duration = MusicManager.getInstance().getDuration();
                    if (duration > 0) {
                        binding.miniPlayer.miniPlayerProgress.setMax(duration);
                        binding.miniPlayer.miniPlayerProgress.setProgress(currentPosition);
                    }
                }
                progressHandler.postDelayed(this, 1000);
            }
        };
        progressHandler.post(progressRunnable);
    }

    private void updateMiniPlayerUI() {
        Song current = MusicManager.getInstance().getCurrentSong();
        if (current != null) {
            binding.miniPlayer.miniPlayerContainer.setVisibility(android.view.View.VISIBLE);
            binding.miniPlayer.miniPlayerTitle.setText(current.getTitle());
            String artistName = (current.getArtists() != null && !current.getArtists().isEmpty()) ? current.getArtists().get(0).getName() : "Unknown Artist";
            binding.miniPlayer.miniPlayerArtist.setText(artistName);
            Glide.with(this).load(RetrofitClient.getFullUrl(current.getCover_url()))
                    .placeholder(R.drawable.placeholder_img)
                    .into(binding.miniPlayer.miniPlayerImg);
            binding.miniPlayer.miniPlayerPlay.setImageResource(MusicManager.getInstance().isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
        } else {
            binding.miniPlayer.miniPlayerContainer.setVisibility(android.view.View.GONE);
        }
    }

    @Override
    public void onSongChanged(Song song) {
        updateMiniPlayerUI();
    }

    @Override
    public void onStatusChanged(boolean isPlaying) {
        binding.miniPlayer.miniPlayerPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.getInstance().setListener(this);
        updateMiniPlayerUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressHandler.removeCallbacks(progressRunnable);
    }

    private void setupRecyclerView() {
        adapter = new SearchResultAdapter(this, artistSongs);
        binding.rvArtistSongs.setLayoutManager(new LinearLayoutManager(this));
        binding.rvArtistSongs.setAdapter(adapter);
    }

    private void fetchSongs() {
        RetrofitClient.getApiService().getSongs().enqueue(new Callback<SimpleResponse<List<Song>>>() {
            @Override
            public void onResponse(Call<SimpleResponse<List<Song>>> call, Response<SimpleResponse<List<Song>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> allSongs = response.body().getData();
                    if (allSongs != null) {
                        artistSongs.clear();
                        for (Song song : allSongs) {
                            if (song.getArtists() != null) {
                                for (com.example.musicappdemo.model.Artist artist : song.getArtists()) {
                                    if (artist.getName() != null && artist.getName().equalsIgnoreCase(artistName)) {
                                        artistSongs.add(song);
                                        break;
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse<List<Song>>> call, Throwable t) {
                Toast.makeText(ArtistDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
