package com.example.musicappdemo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musicappdemo.PlayerActivity;
import com.example.musicappdemo.adapter.CategoryAdapter;
import com.example.musicappdemo.adapter.SearchResultAdapter;
import com.example.musicappdemo.adapter.SearchHistoryAdapter;
import com.example.musicappdemo.adapter.SongAdapter;
import com.example.musicappdemo.databinding.FragmentSearchBinding;
import com.example.musicappdemo.model.Genre;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.utils.MusicManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private final List<Song> allSongs = new ArrayList<>();
    private final List<Song> filteredSongs = new ArrayList<>();
    private final List<Genre> allGenres = new ArrayList<>();
    private final List<Genre> recommendedGenres = new ArrayList<>();
    private final List<String> searchHistory = new ArrayList<>();
    
    // Đổi SongAdapter sang một cái tương thích RecyclerView nếu cần, 
    // nhưng ở đây SongAdapter cũ là BaseAdapter dùng cho ListView.
    // Để nhanh nhất, mình sẽ dùng một Adapter mới cho RecyclerView hoặc đổi ListView lại.
    // Thực tế SongAdapter đang kế thừa BaseAdapter. Mình sẽ tạo một cái Adapter đơn giản cho Search.
    private SearchResultAdapter songAdapter;
    private CategoryAdapter genreAdapter; 
    private SearchHistoryAdapter historyAdapter;

    private static final String PREFS_NAME = "MusicAppPrefs";
    private static final String KEY_HISTORY = "SearchHistory";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        setupAdapters();
        loadSearchHistory();
        loadAllSongs();
        loadAllGenres();

        binding.btnProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), ProfileActivity.class)));

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    resetUI();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                String query = binding.etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                }
                return true;
            }
            return false;
        });

        return binding.getRoot();
    }

    private void setupAdapters() {
        songAdapter = new SearchResultAdapter(getContext(), filteredSongs);
        binding.rvSearchResults.setAdapter(songAdapter);

        // Sử dụng CategoryAdapter để hiển thị thể loại (Album đề xuất)
        genreAdapter = new CategoryAdapter(getContext(), recommendedGenres);
        binding.gvCategories.setAdapter(genreAdapter);

        binding.gvCategories.setOnItemClickListener((parent, view, position, id) -> {
            Genre genre = recommendedGenres.get(position);
            List<Song> genrePlaylist = new ArrayList<>();
            for (Song s : allSongs) {
                if (s.getGenre_names() != null && s.getGenre_names().contains(genre.getName())) {
                    genrePlaylist.add(s);
                }
            }
            if (!genrePlaylist.isEmpty()) {
                MusicManager.getInstance().playPlaylist(getContext(), genrePlaylist, 0);
                startActivity(new Intent(getActivity(), PlayerActivity.class));
            }
        });

        historyAdapter = new SearchHistoryAdapter(searchHistory, new SearchHistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onItemClick(String query) {
                binding.etSearch.setText(query);
                binding.etSearch.setSelection(query.length());
                performSearch(query);
            }

            @Override
            public void onDeleteClick(int position) {
                searchHistory.remove(position);
                historyAdapter.notifyItemRemoved(position);
                saveHistoryToPrefs();
                updateHistoryVisibility();
            }
        });
        binding.rvSearchHistory.setAdapter(historyAdapter);
    }

    private void loadAllSongs() {
        RetrofitClient.getApiService().getSongs().enqueue(new Callback<SimpleResponse<List<Song>>>() {
            @Override
            public void onResponse(@NonNull Call<SimpleResponse<List<Song>>> call, @NonNull Response<SimpleResponse<List<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allSongs.clear();
                    allSongs.addAll(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<SimpleResponse<List<Song>>> call, @NonNull Throwable t) {}
        });
    }

    private void loadAllGenres() {
        RetrofitClient.getApiService().getGenres().enqueue(new Callback<SimpleResponse<List<Genre>>>() {
            @Override
            public void onResponse(@NonNull Call<SimpleResponse<List<Genre>>> call, @NonNull Response<SimpleResponse<List<Genre>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allGenres.clear();
                    allGenres.addAll(response.body().getData());
                }
            }
            @Override
            public void onFailure(@NonNull Call<SimpleResponse<List<Genre>>> call, @NonNull Throwable t) {}
        });
    }

    private void performSearch(String query) {
        saveSearchHistory(query);
        
        filteredSongs.clear();
        recommendedGenres.clear();
        Set<String> foundGenreNames = new HashSet<>();
        String lowerQuery = query.toLowerCase().trim();

        for (Song song : allSongs) {
            String title = song.getTitle() != null ? song.getTitle().toLowerCase() : "";
            String artists = song.getArtist_names() != null ? song.getArtist_names().toLowerCase() : "";
            
            if (title.contains(lowerQuery) || artists.contains(lowerQuery)) {
                filteredSongs.add(song);
                if (song.getGenre_names() != null && !song.getGenre_names().isEmpty()) {
                    String[] genres = song.getGenre_names().split(",");
                    for (String g : genres) {
                        foundGenreNames.add(g.trim());
                    }
                }
            }
        }

        // Cập nhật kết quả tìm kiếm
        if (filteredSongs.isEmpty()) {
            binding.rvSearchResults.setVisibility(View.GONE);
            binding.tvNoResults.setVisibility(View.VISIBLE);
            binding.defaultContent.setVisibility(View.GONE);
        } else {
            binding.rvSearchResults.setVisibility(View.VISIBLE);
            binding.tvNoResults.setVisibility(View.GONE);
            
            // Tìm các đối tượng Genre tương ứng hoặc tạo mới nếu không thấy
            for (String genreName : foundGenreNames) {
                Genre match = null;
                for (Genre g : allGenres) {
                    if (g.getName().equalsIgnoreCase(genreName)) {
                        match = g;
                        break;
                    }
                }
                if (match != null) {
                    recommendedGenres.add(match);
                } else {
                    // Tạo Genre "ảo" nếu API không trả về nhưng bài hát có thể loại này
                    Genre virtualGenre = new Genre(genreName); 
                    recommendedGenres.add(virtualGenre);
                }
            }
            
            if (!recommendedGenres.isEmpty()) {
                binding.defaultContent.setVisibility(View.VISIBLE);
                genreAdapter.notifyDataSetChanged();
            } else {
                binding.defaultContent.setVisibility(View.GONE);
            }
        }
        binding.rvSearchHistory.setVisibility(View.GONE);
        songAdapter.notifyDataSetChanged();
    }

    private void resetUI() {
        binding.rvSearchResults.setVisibility(View.GONE);
        binding.tvNoResults.setVisibility(View.GONE);
        binding.defaultContent.setVisibility(View.GONE);
        updateHistoryVisibility();
    }

    private void updateHistoryVisibility() {
        if (!searchHistory.isEmpty()) {
            binding.rvSearchHistory.setVisibility(View.VISIBLE);
        } else {
            binding.rvSearchHistory.setVisibility(View.GONE);
        }
    }

    private void loadSearchHistory() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String history = prefs.getString(KEY_HISTORY, "");
        if (!history.isEmpty()) {
            String[] items = history.split("\\|");
            searchHistory.addAll(Arrays.asList(items));
            historyAdapter.notifyDataSetChanged();
        }
        updateHistoryVisibility();
    }

    private void saveSearchHistory(String query) {
        if (query.isEmpty()) return;
        searchHistory.remove(query);
        searchHistory.add(0, query);
        if (searchHistory.size() > 5) {
            searchHistory.remove(5);
        }
        historyAdapter.notifyDataSetChanged();
        saveHistoryToPrefs();
    }

    private void saveHistoryToPrefs() {
        if (getActivity() == null) return;
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < searchHistory.size(); i++) {
            sb.append(searchHistory.get(i));
            if (i < searchHistory.size() - 1) sb.append("|");
        }
        prefs.edit().putString(KEY_HISTORY, sb.toString()).apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
