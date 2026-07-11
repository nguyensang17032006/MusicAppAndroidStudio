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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

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
    private String currentSearchQuery = "";

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
            intent.putExtra("playlist_id", playlist.getId());
            intent.putExtra("playlist_title", playlist.getName());
            intent.putExtra("playlist_cover", playlist.getCover_url());
            intent.putExtra("playlist_songs", new ArrayList<>(playlist.getSongs()));
            startActivity(intent);
        });
        binding.rvPlaylists.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPlaylists.setAdapter(playlistAdapter);
        binding.rvPlaylists.setNestedScrollingEnabled(false);

        setupSwipeToDelete();

        // Artists
        artistAdapter = new ArtistAdapter(getContext(), displayArtists, artist -> {
            Intent intent = new Intent(getActivity(), ArtistDetailActivity.class);
            intent.putExtra("artist_name", artist.getName());
            intent.putExtra("artist_avatar", artist.getAvatar_url());
            startActivity(intent);
        });
        binding.rvArtists.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvArtists.setAdapter(artistAdapter);
        binding.rvArtists.setNestedScrollingEnabled(false);
    }

    private void setupEvents() {
        binding.btnLibrarySearch.setOnClickListener(v -> {
            if (binding.etLibrarySearch.getVisibility() == View.VISIBLE) {
                binding.etLibrarySearch.setVisibility(View.GONE);
                binding.tvLibraryTitle.setVisibility(View.VISIBLE);
                binding.etLibrarySearch.setText("");
                currentSearchQuery = "";
                filterLibrary();
            } else {
                binding.etLibrarySearch.setVisibility(View.VISIBLE);
                binding.tvLibraryTitle.setVisibility(View.GONE);
                binding.etLibrarySearch.requestFocus();
            }
        });

        binding.etLibrarySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                filterLibrary();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

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
            filterLibrary();
        } else {
            binding.rvPlaylists.setVisibility(View.GONE);
            binding.rvArtists.setVisibility(View.VISIBLE);
            binding.btnLikedSongs.setVisibility(View.GONE);

            binding.btnAddPlaylist.setVisibility(View.GONE);
            binding.btnAddArtist.setVisibility(View.VISIBLE);

            binding.chipPlaylists.setAlpha(0.5f);
            binding.chipArtists.setAlpha(1.0f);
            filterLibrary();
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
            
            // Kiểm tra trùng lặp
            boolean isAlreadyAdded = false;
            for (Artist followed : followedArtists) {
                if (followed.getId().equals(artist.getId())) {
                    isAlreadyAdded = true;
                    break;
                }
            }

            if (isAlreadyAdded) {
                Toast.makeText(getContext(), "Nghệ sĩ này đã được thêm rồi!", Toast.LENGTH_SHORT).show();
                return;
            }

            libraryManager.followArtist(artist);
            
            Toast.makeText(getContext(), "Đã thêm " + artist.getName() + " vào thư viện", Toast.LENGTH_SHORT).show();
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

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback swipeHandler = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private final Paint paint = new Paint();
            private final Rect textBounds = new Rect();

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                boolean isPlaylist = viewHolder.getBindingAdapter() == playlistAdapter;

                if (isPlaylist) {
                    Playlist playlist = playlists.get(position);
                    new AlertDialog.Builder(getContext())
                            .setTitle("Xóa playlist")
                            .setMessage("Bạn có chắc chắn muốn xóa playlist \"" + playlist.getName() + "\"?")
                            .setPositiveButton("Xóa", (dialog, which) -> LibraryManager.getInstance(getContext()).deletePlaylist(playlist.getId()))
                            .setNegativeButton("Hủy", (dialog, which) -> playlistAdapter.notifyItemChanged(position))
                            .setCancelable(false)
                            .show();
                } else {
                    Artist artist = displayArtists.get(position);
                    new AlertDialog.Builder(getContext())
                            .setTitle("Bỏ theo dõi")
                            .setMessage("Bạn có chắc chắn muốn bỏ theo dõi nghệ sĩ \"" + artist.getName() + "\"?")
                            .setPositiveButton("Bỏ theo dõi", (dialog, which) -> LibraryManager.getInstance(getContext()).unfollowArtist(artist.getId()))
                            .setNegativeButton("Hủy", (dialog, which) -> artistAdapter.notifyItemChanged(position))
                            .setCancelable(false)
                            .show();
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0) {
                    View itemView = viewHolder.itemView;
                    
                    // 1. Draw Red Background (Giới hạn chỉ rộng tối đa 50% itemView)
                    paint.setColor(Color.parseColor("#B0273F"));
                    float backgroundLeft = Math.max((float) itemView.getRight() + dX, (float) itemView.getRight() - (itemView.getWidth() / 2f));
                    c.drawRect(backgroundLeft, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);

                    // 2. Draw Trash Icon (Căn giữa trong vùng 50%)
                    Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_delete);
                    if (icon != null) {
                        int itemHeight = itemView.getBottom() - itemView.getTop();
                        int intrinsicWidth = icon.getIntrinsicWidth();
                        int intrinsicHeight = icon.getIntrinsicHeight();

                        // Vùng 50% bên phải
                        float halfWidthAreaLeft = itemView.getRight() - (itemView.getWidth() / 2f);
                        float centerXInRed = halfWidthAreaLeft + (itemView.getWidth() / 4f);

                        int iconLeft = (int) (centerXInRed - (intrinsicWidth / 2f));
                        int iconRight = iconLeft + intrinsicWidth;
                        int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2 - 20;
                        int iconBottom = iconTop + intrinsicHeight;

                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.setTint(Color.WHITE);
                        icon.draw(c);

                        // 3. Draw "Xóa" Text
                        paint.setColor(Color.WHITE);
                        paint.setTextSize(32);
                        paint.setAntiAlias(true);
                        paint.setTextAlign(Paint.Align.CENTER);
                        String text = "Xóa";
                        paint.getTextBounds(text, 0, text.length(), textBounds);
                        float textX = centerXInRed;
                        float textY = iconBottom + textBounds.height() + 10;
                        c.drawText(text, textX, textY, paint);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.5f; // Vuốt đúng 50% màn hình mới kích hoạt xóa
            }
        };

        new ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvPlaylists);
        new ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvArtists);
    }

    private void applySort() {
        filterLibrary();
    }

    private void filterLibrary() {
        if (currentMode == LibraryMode.PLAYLISTS) {
            List<Playlist> filteredPlaylists = new ArrayList<>();
            for (Playlist p : playlists) {
                if (p.getName().toLowerCase().contains(currentSearchQuery)) {
                    filteredPlaylists.add(p);
                }
            }
            // Sort playlists if needed (default is by name or recently added)
            if (currentSortMode.equals("A-Z")) {
                Collections.sort(filteredPlaylists, (p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
            }
            playlistAdapter.updateList(filteredPlaylists);
        } else {
            displayArtists.clear();
            for (Artist a : followedArtists) {
                if (a.getName().toLowerCase().contains(currentSearchQuery)) {
                    displayArtists.add(a);
                }
            }
            if (currentSortMode.equals("A-Z")) {
                Collections.sort(displayArtists, (a1, a2) -> a1.getName().compareToIgnoreCase(a2.getName()));
            } else {
                Collections.sort(displayArtists, (a1, a2) -> Long.compare(a2.getAddedTime(), a1.getAddedTime()));
            }
            if (artistAdapter != null) {
                artistAdapter.notifyDataSetChanged();
            }
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