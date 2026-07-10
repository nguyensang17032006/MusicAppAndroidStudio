package com.example.musicappdemo.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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

public class ArtistDetailActivity extends AppCompatActivity {

    private ActivityArtistDetailBinding binding;
    private SearchResultAdapter adapter;
    private List<Song> artistSongs = new ArrayList<>();
    private String artistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtistDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        artistName = getIntent().getStringExtra("artist_name");
        if (artistName == null) {
            finish();
            return;
        }

        binding.tvArtistName.setText(artistName);
        binding.btnBack.setOnClickListener(v -> finish());

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
                            if (song.getArtists()!= null && song.getArtists().contains(artistName)) {
                                artistSongs.add(song);
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
