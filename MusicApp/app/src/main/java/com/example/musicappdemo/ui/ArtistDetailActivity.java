package com.example.musicappdemo.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.R;
import com.example.musicappdemo.adapter.SearchResultAdapter;
import com.example.musicappdemo.databinding.ActivityArtistDetailBinding;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.data.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistDetailActivity extends AppCompatActivity implements com.example.musicappdemo.utils.MusicManager.OnMusicStatusListener {

    private ActivityArtistDetailBinding binding;
    private SearchResultAdapter adapter;
    private List<Song> artistSongs = new ArrayList<>();
    private String artistName;
    private String artistAvatar;

    private android.os.Handler progressHandler = new android.os.Handler();
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
                Toast.makeText(this, "Đang phát nhạc của " + artistName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Nghệ sĩ chưa có bài hát nào", Toast.LENGTH_SHORT).show();
            }
        });

        // Mini player events
        binding.miniPlayer.getRoot().setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, com.example.musicappdemo.PlayerActivity.class));
        });

        binding.miniPlayer.miniPlayerPlay.setOnClickListener(v -> com.example.musicappdemo.utils.MusicManager.getInstance().togglePause());

        binding.miniPlayer.miniPlayerClose.setOnClickListener(v -> {
            com.example.musicappdemo.utils.MusicManager.getInstance().stopMusic();
            updateMiniPlayerVisibility();
        });

        setupProgressUpdate();

        setupRecyclerView();
        fetchSongs();
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

    private void setupProgressUpdate() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (com.example.musicappdemo.utils.MusicManager.getInstance().isPlaying()) {
                    int currentPosition = com.example.musicappdemo.utils.MusicManager.getInstance().getCurrentPosition();
                    int duration = com.example.musicappdemo.utils.MusicManager.getInstance().getDuration();
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

    @Override
    protected void onResume() {
        super.onResume();
        com.example.musicappdemo.utils.MusicManager.getInstance().setListener(this);
        Song currentSong = com.example.musicappdemo.utils.MusicManager.getInstance().getCurrentSong();
        if (currentSong != null) {
            onSongChanged(currentSong);
            onStatusChanged(com.example.musicappdemo.utils.MusicManager.getInstance().isPlaying());
        }
        updateMiniPlayerVisibility();
    }

    private void updateMiniPlayerVisibility() {
        if (com.example.musicappdemo.utils.MusicManager.getInstance().getCurrentSong() != null) {
            binding.miniPlayer.getRoot().setVisibility(android.view.View.VISIBLE);
        } else {
            binding.miniPlayer.getRoot().setVisibility(android.view.View.GONE);
        }
    }

    @Override
    public void onSongChanged(Song song) {
        updateMiniPlayerVisibility();
        if (song != null) {
            String artistText = (song.getArtists() != null && !song.getArtists().isEmpty()) ? song.getArtists().get(0).getName() : "Unknown Artist";
            binding.miniPlayer.miniPlayerTitle.setText(song.getTitle());
            binding.miniPlayer.miniPlayerArtist.setText(artistText);
            if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
                com.bumptech.glide.Glide.with(this).load(com.example.musicappdemo.data.RetrofitClient.getFullUrl(song.getCover_url())).into(binding.miniPlayer.miniPlayerImg);
            } else {
                binding.miniPlayer.miniPlayerImg.setImageResource(com.example.musicappdemo.R.drawable.placeholder_img);
            }
            binding.miniPlayer.miniPlayerProgress.setProgress(0);
        }
    }

    @Override
    public void onStatusChanged(boolean isPlaying) {
        binding.miniPlayer.miniPlayerPlay.setImageResource(isPlaying ? com.example.musicappdemo.R.drawable.ic_pause : com.example.musicappdemo.R.drawable.ic_play);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressHandler.removeCallbacks(progressRunnable);
    }
}
