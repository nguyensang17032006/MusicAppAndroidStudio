package com.example.musicappdemo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

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

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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
    private SearchResultAdapter songAdapter;
    private CategoryAdapter genreAdapter;
    private SearchHistoryAdapter historyAdapter;

    private static final String PREFS_NAME = "MusicAppPrefs";
    private static final String KEY_HISTORY = "SearchHistory";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        setupAdapters();
        loadSearchHistory();
        loadAllSongs();
        loadAllGenres();

        binding.btnProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), ProfileActivity.class)));

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    resetUI();
                } else {
                    performLiveSearch(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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
        genreAdapter = new CategoryAdapter(getContext(), recommendedGenres, (genre, position) -> {
            ArrayList<Song> genreSongs = new ArrayList<>();
            for (Song s : allSongs) {
                if (s.getGenres() != null && !s.getGenres().isEmpty()) {
                    for (Genre g : s.getGenres()) {
                        if (g.getName() != null && g.getName().equalsIgnoreCase(genre.getName())) {
                            genreSongs.add(s);
                            break;
                        }
                    }
                }
            }
            if (!genreSongs.isEmpty()) {
                Intent intent = new Intent(getActivity(), PlaylistDetailActivity.class);
                intent.putExtra("playlist_title", genre.getName());
                intent.putExtra("playlist_songs", genreSongs);
                intent.putExtra("is_genre", true);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Không có bài hát nào thuộc thể loại này", Toast.LENGTH_SHORT).show();
            }
        });
        binding.rvCategories.setAdapter(genreAdapter);

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
            public void onResponse(@NonNull Call<SimpleResponse<List<Song>>> call,
                    @NonNull Response<SimpleResponse<List<Song>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allSongs.clear();
                    allSongs.addAll(response.body().getData());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SimpleResponse<List<Song>>> call, @NonNull Throwable t) {
            }
        });
    }

    private void loadAllGenres() {
        RetrofitClient.getApiService().getGenres().enqueue(new Callback<SimpleResponse<List<Genre>>>() {
            @Override
            public void onResponse(@NonNull Call<SimpleResponse<List<Genre>>> call,
                    @NonNull Response<SimpleResponse<List<Genre>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allGenres.clear();
                    allGenres.addAll(response.body().getData());
                    if (binding != null && (binding.etSearch.getText() == null
                            || binding.etSearch.getText().toString().trim().isEmpty())) {
                        recommendedGenres.clear();
                        recommendedGenres.addAll(allGenres);
                        genreAdapter.notifyDataSetChanged();
                        binding.defaultContent.setVisibility(View.VISIBLE);
                        binding.tvGenreTitle.setText("Tất cả thể loại");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SimpleResponse<List<Genre>>> call, @NonNull Throwable t) {
            }
        });
    }

    private void performSearch(String query) {
        hideKeyboard();
        saveSearchHistory(query);
        executeSearch(query);
    }

    private void performLiveSearch(String query) {
        // Không lưu history, không ẩn keyboard khi đang gõ
        executeSearch(query);
    }

    private void executeSearch(String query) {
        filteredSongs.clear();
        recommendedGenres.clear();
        Set<String> foundGenreNames = new HashSet<>();
        String lowerQuery = removeAccent(query.toLowerCase().trim());

        for (Song song : allSongs) {
            String title = song.getTitle() != null ? removeAccent(song.getTitle().toLowerCase()) : "";
            
            boolean matchArtist = false;
            if (song.getArtists() != null && !song.getArtists().isEmpty()) {
                for (com.example.musicappdemo.model.Artist artist : song.getArtists()) {
                    if (artist.getName() != null) {
                        String artistName = removeAccent(artist.getName().toLowerCase());
                        if (artistName.contains(lowerQuery)) {
                            matchArtist = true;
                            break;
                        }
                    }
                }
            }

            if (title.contains(lowerQuery) || matchArtist) {
                filteredSongs.add(song);
                if (song.getGenres() != null && !song.getGenres().isEmpty()) {
                    for (Genre g : song.getGenres()) {
                        if (g.getName() != null) {
                            String[] parts = g.getName().split(",");
                            for (String p : parts) foundGenreNames.add(p.trim());
                        }
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
                    recommendedGenres.add(new Genre(genreName));
                }
            }

            if (!recommendedGenres.isEmpty()) {
                binding.defaultContent.setVisibility(View.VISIBLE);
                binding.tvGenreTitle.setText("Thể loại đề xuất");
                genreAdapter.notifyDataSetChanged();
            } else {
                binding.defaultContent.setVisibility(View.GONE);
            }
        }
        binding.rvSearchHistory.setVisibility(View.GONE);
        songAdapter.notifyDataSetChanged();
    }

    private void hideKeyboard() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void resetUI() {
        binding.rvSearchResults.setVisibility(View.GONE);
        binding.tvNoResults.setVisibility(View.GONE);
        binding.defaultContent.setVisibility(View.VISIBLE);
        filteredSongs.clear();
        recommendedGenres.clear();
        recommendedGenres.addAll(allGenres);
        binding.tvGenreTitle.setText("Tất cả thể loại");
        genreAdapter.notifyDataSetChanged();
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
        if (query.isEmpty())
            return;
        searchHistory.remove(query);
        searchHistory.add(0, query);
        if (searchHistory.size() > 5) {
            searchHistory.remove(5);
        }
        historyAdapter.notifyDataSetChanged();
        saveHistoryToPrefs();
    }

    private void saveHistoryToPrefs() {
        if (getActivity() == null)
            return;
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < searchHistory.size(); i++) {
            sb.append(searchHistory.get(i));
            if (i < searchHistory.size() - 1)
                sb.append("|");
        }
        prefs.edit().putString(KEY_HISTORY, sb.toString()).apply();
    }

    private String removeAccent(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserAvatar();
    }

    private void loadUserAvatar() {
        if (getContext() == null || binding == null) return;
        String avatarUri = com.example.musicappdemo.data.SessionManager.get(getContext()).getAvatarUri();
        if (avatarUri != null && !avatarUri.isEmpty()) {
            com.bumptech.glide.Glide.with(this)
                    .load(com.example.musicappdemo.data.RetrofitClient.getFullUrl(avatarUri))
                    .placeholder(com.example.musicappdemo.R.drawable.ic_user)
                    .into(binding.btnProfile);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}