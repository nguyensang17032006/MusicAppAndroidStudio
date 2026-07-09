package com.example.musicappdemo.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.musicappdemo.adapter.SearchResultAdapter;
import com.example.musicappdemo.databinding.ActivityPlaylistDetailBinding;
import com.example.musicappdemo.model.Song;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailActivity extends AppCompatActivity {

    private ActivityPlaylistDetailBinding binding;
    private List<Song> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String title = getIntent().getStringExtra("playlist_title");
        ArrayList<Song> songs = (ArrayList<Song>) getIntent().getSerializableExtra("playlist_songs");

        if (title != null) binding.tvPlaylistTitle.setText(title);
        if (songs != null) songList.addAll(songs);

        binding.btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        SearchResultAdapter adapter = new SearchResultAdapter(this, songList);
        binding.rvPlaylistSongs.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPlaylistSongs.setAdapter(adapter);
    }
}
