package com.example.musicappdemo.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappdemo.R;
import com.example.musicappdemo.adapter.ArtistAdapter;
import com.example.musicappdemo.adapter.PlaylistLibraryAdapter;
import com.example.musicappdemo.adapter.SearchResultAdapter;
import com.example.musicappdemo.databinding.FragmentLibraryBinding;
import com.example.musicappdemo.model.Artist;
import com.example.musicappdemo.model.Playlist;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.utils.LibraryManager;
import com.example.musicappdemo.utils.MusicManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryFragment extends Fragment {

    private FragmentLibraryBinding binding;
    private PlaylistLibraryAdapter playlistAdapter;
    private ArtistAdapter artistAdapter;
    private List<Playlist> playlists;
    private List<Artist> followedArtists = new ArrayList<>();
    private List<Artist> displayArtists = new ArrayList<>();
    private List<Artist> allArtists = new ArrayList<>();
    private List<Song> allSongs = new ArrayList<>();

    private enum LibraryMode { PLAYLISTS, ARTISTS }
    private LibraryMode currentMode = LibraryMode.PLAYLISTS;

    private String currentSortMode = "Recently Added"; // "A-Z" or "Recently Added"

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLibraryBinding.inflate(inflater, container, false);

        setupLibrary();
        setupEvents();
        updateLibraryUI();
        loadInitialData();

        LibraryManager libraryManager = LibraryManager.getInstance(getContext());
        libraryManager.setSyncListener(this::refreshLibrary);
        libraryManager.syncAll();

        return binding.getRoot();
    }

    private void loadInitialData() {
        fetchSongs();
        fetchArtists();
    }

    private void setupLibrary() {
        LibraryManager libraryManager = LibraryManager.getInstance(getContext());
        
        // Liked Songs count
        int likedCount = libraryManager.getLikedSongs().size();
        binding.tvLikedSongsCount.setText(likedCount + " bài hát");

        // Playlists
        playlists = libraryManager.getPlaylists();
        playlistAdapter = new PlaylistLibraryAdapter(getContext(), playlists, playlist -> {
            Intent intent = new Intent(getActivity(), PlaylistDetailActivity.class);
            intent.putExtra("playlist_title", playlist.getName());
            intent.putExtra("playlist_songs", new ArrayList<>(playlist.getSongs()));
            startActivity(intent);
        });
        binding.rvPlaylists.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPlaylists.setAdapter(playlistAdapter);
        binding.rvPlaylists.setNestedScrollingEnabled(false);

        // Artists
        artistAdapter = new ArtistAdapter(getContext(), displayArtists, artist -> {
            Intent intent = new Intent(getActivity(), ArtistDetailActivity.class);
            intent.putExtra("artist_name", artist.getName());
            startActivity(intent);
        });
        binding.rvArtists.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvArtists.setAdapter(artistAdapter);
        binding.rvArtists.setNestedScrollingEnabled(false);
    }

    private void setupEvents() {
        binding.btnLikedSongs.setOnClickListener(v -> {
            List<Song> likedSongs = LibraryManager.getInstance(getContext()).getLikedSongs();
            Intent intent = new Intent(getActivity(), PlaylistDetailActivity.class);
            intent.putExtra("playlist_title", "Liked Songs");
            intent.putExtra("playlist_songs", new ArrayList<>(likedSongs));
            startActivity(intent);
        });

        binding.btnAddPlaylist.setOnClickListener(v -> showCreatePlaylistDialog());
        binding.btnAddArtist.setOnClickListener(v -> showAddArtistDialog());
        binding.btnLibraryAdd.setOnClickListener(v -> {
            if (currentMode == LibraryMode.PLAYLISTS) {
                showCreatePlaylistDialog();
            } else {
                showAddArtistDialog();
            }
        });

        binding.chipPlaylists.setOnClickListener(v -> {
            currentMode = LibraryMode.PLAYLISTS;
            updateLibraryUI();
        });

        binding.chipArtists.setOnClickListener(v -> {
            currentMode = LibraryMode.ARTISTS;
            updateLibraryUI();
        });

        binding.layoutSort.setOnClickListener(v -> showSortMenu());
    }

    private void updateLibraryUI() {
        if (currentMode == LibraryMode.PLAYLISTS) {
            binding.rvPlaylists.setVisibility(View.VISIBLE);
            binding.rvArtists.setVisibility(View.GONE);
            binding.btnLikedSongs.setVisibility(View.VISIBLE);
            
            binding.btnAddPlaylist.setVisibility(View.VISIBLE);
            binding.btnAddArtist.setVisibility(View.GONE);
            
            binding.chipPlaylists.setAlpha(1.0f);
            binding.chipArtists.setAlpha(0.5f);
        } else {
            binding.rvPlaylists.setVisibility(View.GONE);
            binding.rvArtists.setVisibility(View.VISIBLE);
            binding.btnLikedSongs.setVisibility(View.GONE);

            binding.btnAddPlaylist.setVisibility(View.GONE);
            binding.btnAddArtist.setVisibility(View.VISIBLE);

            binding.chipPlaylists.setAlpha(0.5f);
            binding.chipArtists.setAlpha(1.0f);
        }
    }

    private void showAddArtistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_add_artist, null);
        builder.setView(view);

        EditText etSearch = view.findViewById(R.id.etSearchArtist);
        RecyclerView rvResults = view.findViewById(R.id.rvArtistResults);
        TextView tvEmpty = view.findViewById(R.id.tvEmptyArtists);

        List<Artist> searchResults = new ArrayList<>();
        AlertDialog dialog = builder.create();

        ArtistAdapter searchAdapter = new ArtistAdapter(getContext(), searchResults, artist -> {
            LibraryManager libraryManager = LibraryManager.getInstance(getContext());
            libraryManager.followArtist(artist);
            
            // Tự động add các bài nhạc của artist vào Liked Songs
            for (Song song : allSongs) {
                if (song.getArtists().get(0).getName() != null && song.getArtists().get(0).getName().contains(artist.getName())) {
                    libraryManager.addLikedSong(song);
                }
            }
            
            Toast.makeText(getContext(), "Đã thêm " + artist.getName() + " và các bài hát của họ", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            refreshLibrary();
        });

        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvResults.setAdapter(searchAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                searchResults.clear();
                if (!query.isEmpty()) {
                    for (Artist artist : allArtists) {
                        if (artist.getName().toLowerCase().contains(query)) {
                            searchResults.add(artist);
                        }
                    }
                }
                
                if (searchResults.isEmpty() && !query.isEmpty()) tvEmpty.setVisibility(View.VISIBLE);
                else tvEmpty.setVisibility(View.GONE);
                
                searchAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        dialog.show();
    }

    private void fetchSongs() {
        RetrofitClient.getApiService().getSongs().enqueue(new Callback<SimpleResponse<List<Song>>>() {
            @Override
            public void onResponse(Call<SimpleResponse<List<Song>>> call, Response<SimpleResponse<List<Song>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allSongs.clear();
                    allSongs.addAll(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse<List<Song>>> call, Throwable t) {}
        });
    }

    private void fetchArtists() {
        RetrofitClient.getApiService().getArtists().enqueue(new Callback<SimpleResponse<List<Artist>>>() {
            @Override
            public void onResponse(Call<SimpleResponse<List<Artist>>> call, Response<SimpleResponse<List<Artist>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allArtists.clear();
                    allArtists.addAll(response.body().getData());
                    refreshLibrary();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse<List<Artist>>> call, Throwable t) {}
        });
    }

    private void showSortMenu() {
        PopupMenu popupMenu = new PopupMenu(getContext(), binding.layoutSort);
        popupMenu.getMenu().add("A-Z");
        popupMenu.getMenu().add("Gần đây");
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getTitle() != null && item.getTitle().toString().equals("A-Z")) {
                currentSortMode = "A-Z";
                binding.tvSortMode.setText("A-Z");
            } else {
                currentSortMode = "Recently Added";
                binding.tvSortMode.setText("Gần đây");
            }
            applySort();
            return true;
        });
        popupMenu.show();
    }

    private void applySort() {
        displayArtists.clear();
        displayArtists.addAll(followedArtists);
        if (currentSortMode.equals("A-Z")) {
            Collections.sort(displayArtists, (a1, a2) -> a1.getName().compareToIgnoreCase(a2.getName()));
        } else {
            Collections.sort(displayArtists, (a1, a2) -> Long.compare(a2.getAddedTime(), a1.getAddedTime()));
        }
        if (artistAdapter != null) {
            artistAdapter.notifyDataSetChanged();
        }
    }

    private void showCreatePlaylistDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tên playlist mới");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Tạo", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                LibraryManager.getInstance(getContext()).createPlaylist(name);
                refreshLibrary();
            } else {
                Toast.makeText(getContext(), "Tên không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void refreshLibrary() {
        if (binding == null || getContext() == null) return;

        LibraryManager libraryManager = LibraryManager.getInstance(getContext());
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (binding == null) return;

                int likedCount = libraryManager.getLikedSongs().size();
                binding.tvLikedSongsCount.setText(likedCount + " bài hát");

                List<Playlist> remotePlaylists = libraryManager.getPlaylists();
                if (playlists != null) {
                    playlists.clear();
                    playlists.addAll(remotePlaylists);
                    if (playlistAdapter != null) {
                        playlistAdapter.notifyDataSetChanged();
                    }
                }

                List<Artist> remoteArtists = libraryManager.getFollowedArtists();
                if (followedArtists != null) {
                    followedArtists.clear();
                    followedArtists.addAll(remoteArtists);
                    applySort();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLibrary();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}