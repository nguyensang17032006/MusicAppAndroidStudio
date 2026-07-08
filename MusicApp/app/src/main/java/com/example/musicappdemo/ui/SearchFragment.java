package com.example.musicappdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicappdemo.adapter.SongAdapter;
import com.example.musicappdemo.databinding.FragmentSearchBinding;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private final List<Song> allSongs = new ArrayList<>();
    private final List<Song> filteredSongs = new ArrayList<>();
    private SongAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        adapter = new SongAdapter(getContext(), filteredSongs);
        binding.rvSearchResults.setAdapter(adapter);

        binding.btnProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), ProfileActivity.class)));

        loadAllSongs();

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSongs(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return binding.getRoot();
    }

    private void loadAllSongs() {
        RetrofitClient.getApiService().getSongs().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<Song>> call, @NonNull Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allSongs.clear();
                    allSongs.addAll(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Song>> call, @NonNull Throwable t) {}
        });
    }

    private void filterSongs(String query) {
        filteredSongs.clear();
        if (query.isEmpty()) {
            binding.rvSearchResults.setVisibility(View.GONE);
            binding.defaultContent.setVisibility(View.VISIBLE);
        } else {
            binding.rvSearchResults.setVisibility(View.VISIBLE);
            binding.defaultContent.setVisibility(View.GONE);
            for (Song song : allSongs) {
                if (song.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    song.getArtist_names().toLowerCase().contains(query.toLowerCase())) {
                    filteredSongs.add(song);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
