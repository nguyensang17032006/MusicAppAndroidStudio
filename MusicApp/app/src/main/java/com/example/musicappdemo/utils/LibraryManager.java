package com.example.musicappdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.data.SessionManager;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.model.Artist;
import com.example.musicappdemo.model.Playlist;
import com.example.musicappdemo.model.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryManager {
    private static final String PREFS_NAME = "LibraryPrefs";
    private static final String KEY_LIKED_SONGS = "LikedSongs_";
    private static final String KEY_PLAYLISTS = "UserPlaylists_";
    private static final String KEY_FOLLOWED_ARTISTS = "FollowedArtists_";

    private static LibraryManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;
    private final Context context;
    private OnSyncListener syncListener;

    public interface OnSyncListener {
        void onSyncComplete();
    }

    private LibraryManager(Context context) {
        this.context = context.getApplicationContext();
        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public static synchronized LibraryManager getInstance(Context context) {
        if (instance == null) {
            instance = new LibraryManager(context);
        }
        return instance;
    }

    public void setSyncListener(OnSyncListener listener) {
        this.syncListener = listener;
    }

    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(() -> 
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }

    public void syncAll() {
        syncLikedSongs();
        syncPlaylists();
        syncFollowedArtists();
    }

    private String getUserId() {
        return SessionManager.get(context).getUserId();
    }

    // --- LIKED SONGS ---
    public List<Song> getLikedSongs() {
        String userId = getUserId();
        if (userId == null) return new ArrayList<>();
        String json = prefs.getString(KEY_LIKED_SONGS + userId, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Song>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void syncLikedSongs() {
        String userId = getUserId();
        if (userId == null) return;
        RetrofitClient.getApiService().getLikedSongs(userId).enqueue(new Callback<SimpleResponse<List<Song>>>() {
            @Override
            public void onResponse(Call<SimpleResponse<List<Song>>> call, Response<SimpleResponse<List<Song>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Song> songs = response.body().getData();
                    prefs.edit().putString(KEY_LIKED_SONGS + userId, gson.toJson(songs)).apply();
                    if (syncListener != null) syncListener.onSyncComplete();
                }
            }
            @Override
            public void onFailure(Call<SimpleResponse<List<Song>>> call, Throwable t) {}
        });
    }

    public void addLikedSong(Song song) {
        String userId = getUserId();
        if (userId == null) return;

        List<Song> songs = getLikedSongs();
        for (Song s : songs) {
            if (s.getId().equals(song.getId())) return;
        }
        songs.add(song);
        prefs.edit().putString(KEY_LIKED_SONGS + userId, gson.toJson(songs)).apply();

        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        body.put("songId", song.getId());
        RetrofitClient.getApiService().toggleLikeSong(body).enqueue(new Callback<SimpleResponse<Void>>() {
            @Override
            public void onResponse(Call<SimpleResponse<Void>> call, Response<SimpleResponse<Void>> response) {
                if (!response.isSuccessful()) showToast("Lỗi đồng bộ bài hát");
            }
            @Override
            public void onFailure(Call<SimpleResponse<Void>> call, Throwable t) {}
        });
    }

    public void removeLikedSong(String songId) {
        String userId = getUserId();
        if (userId == null) return;

        List<Song> songs = getLikedSongs();
        songs.removeIf(s -> s.getId().equals(songId));
        prefs.edit().putString(KEY_LIKED_SONGS + userId, gson.toJson(songs)).apply();

        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        body.put("songId", songId);
        RetrofitClient.getApiService().toggleLikeSong(body).enqueue(new Callback<SimpleResponse<Void>>() {
            @Override
            public void onResponse(Call<SimpleResponse<Void>> call, Response<SimpleResponse<Void>> response) {}
            @Override
            public void onFailure(Call<SimpleResponse<Void>> call, Throwable t) {}
        });
    }

    public boolean isLiked(String songId) {
        List<Song> songs = getLikedSongs();
        for (Song s : songs) {
            if (s.getId().equals(songId)) return true;
        }
        return false;
    }

    // --- PLAYLISTS ---
    public List<Playlist> getPlaylists() {
        String userId = getUserId();
        if (userId == null) return new ArrayList<>();
        String json = prefs.getString(KEY_PLAYLISTS + userId, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Playlist>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void syncPlaylists() {
        String userId = getUserId();
        if (userId == null) return;
        RetrofitClient.getApiService().getUserPlaylists(userId).enqueue(new Callback<SimpleResponse<List<Playlist>>>() {
            @Override
            public void onResponse(Call<SimpleResponse<List<Playlist>>> call, Response<SimpleResponse<List<Playlist>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Playlist> playlists = response.body().getData();
                    prefs.edit().putString(KEY_PLAYLISTS + userId, gson.toJson(playlists)).apply();
                    if (syncListener != null) syncListener.onSyncComplete();
                }
            }
            @Override
            public void onFailure(Call<SimpleResponse<List<Playlist>>> call, Throwable t) {}
        });
    }

    public void createPlaylist(String name) {
        createPlaylist(name, null);
    }

    public void createPlaylist(String name, Song songToAdd) {
        String userId = getUserId();
        if (userId == null) return;

        // Kiểm tra trùng tên local trước khi gọi API
        List<Playlist> currentPlaylists = getPlaylists();
        for (Playlist p : currentPlaylists) {
            if (p.getName().equalsIgnoreCase(name)) {
                showToast("Tên playlist đã tồn tại!");
                return;
            }
        }

        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        body.put("name", name);
        RetrofitClient.getApiService().createPlaylist(body).enqueue(new Callback<SimpleResponse<Playlist>>() {
            @Override
            public void onResponse(Call<SimpleResponse<Playlist>> call, Response<SimpleResponse<Playlist>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Playlist newPlaylist = response.body().getData();
                    if (songToAdd != null && newPlaylist != null) {
                        addSongToPlaylist(newPlaylist.getId(), songToAdd);
                    } else {
                        syncPlaylists();
                    }
                    showToast("Đã tạo playlist: " + name);
                } else {
                    String errorMsg = "Lỗi khi tạo playlist";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg += ": " + response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            errorMsg += " (Code " + response.code() + ")";
                        } catch (Exception e) {}
                    }
                    showToast(errorMsg);
                }
            }
            @Override
            public void onFailure(Call<SimpleResponse<Playlist>> call, Throwable t) {
                showToast("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void addSongToPlaylist(String playlistId, Song song) {
        Map<String, String> body = new HashMap<>();
        body.put("playlistId", playlistId);
        body.put("songId", song.getId());
        RetrofitClient.getApiService().addSongToPlaylist(body).enqueue(new Callback<SimpleResponse<Void>>() {
            @Override
            public void onResponse(Call<SimpleResponse<Void>> call, Response<SimpleResponse<Void>> response) {
                if (response.isSuccessful()) {
                    syncPlaylists();
                } else {
                    showToast("Lỗi khi thêm vào playlist");
                }
            }
            @Override
            public void onFailure(Call<SimpleResponse<Void>> call, Throwable t) {}
        });
    }

    public void removeSongFromPlaylist(String playlistId, String songId) {
        Map<String, String> body = new HashMap<>();
        body.put("playlistId", playlistId);
        body.put("songId", songId);
        RetrofitClient.getApiService().removeSongFromPlaylist(body).enqueue(new Callback<SimpleResponse<Void>>() {
            @Override
            public void onResponse(Call<SimpleResponse<Void>> call, Response<SimpleResponse<Void>> response) {
                if (response.isSuccessful()) {
                    syncPlaylists();
                    showToast("Đã xóa khỏi playlist");
                } else {
                    showToast("Lỗi khi xóa khỏi playlist");
                }
            }
            @Override
            public void onFailure(Call<SimpleResponse<Void>> call, Throwable t) {
                showToast("Lỗi kết nối");
            }
        });
    }

    public void deletePlaylist(String playlistId) {
        String userId = getUserId();
        if (userId == null) return;

        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        body.put("playlistId", playlistId);
        RetrofitClient.getApiService().deletePlaylist(body).enqueue(new Callback<SimpleResponse<Void>>() {
            @Override
            public void onResponse(Call<SimpleResponse<Void>> call, Response<SimpleResponse<Void>> response) {
                if (response.isSuccessful()) {
                    syncPlaylists();
                    showToast("Đã xóa playlist");
                } else {
                    showToast("Lỗi khi xóa playlist");
                }
            }
            @Override
            public void onFailure(Call<SimpleResponse<Void>> call, Throwable t) {
                showToast("Lỗi kết nối");
            }
        });
    }

    // --- ARTISTS ---
    public List<Artist> getFollowedArtists() {
        String userId = getUserId();
        if (userId == null) return new ArrayList<>();
        String json = prefs.getString(KEY_FOLLOWED_ARTISTS + userId, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Artist>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void syncFollowedArtists() {
        String userId = getUserId();
        if (userId == null) return;
        RetrofitClient.getApiService().getFollowedArtists(userId).enqueue(new Callback<SimpleResponse<List<Artist>>>() {
            @Override
            public void onResponse(Call<SimpleResponse<List<Artist>>> call, Response<SimpleResponse<List<Artist>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Artist> artists = response.body().getData();
                    prefs.edit().putString(KEY_FOLLOWED_ARTISTS + userId, gson.toJson(artists)).apply();
                    if (syncListener != null) syncListener.onSyncComplete();
                }
            }
            @Override
            public void onFailure(Call<SimpleResponse<List<Artist>>> call, Throwable t) {}
        });
    }

    public void followArtist(Artist artist) {
        followArtist(artist.getId(), artist.getName());
    }

    public void followArtist(String artistId, String artistName) {
        String userId = getUserId();
        if (userId == null) return;

        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        if (artistId != null) body.put("artistId", artistId);
        if (artistName != null) body.put("artistName", artistName);

        RetrofitClient.getApiService().toggleFollowArtist(body).enqueue(new Callback<SimpleResponse<Void>>() {
            @Override
            public void onResponse(Call<SimpleResponse<Void>> call, Response<SimpleResponse<Void>> response) {
                if (response.isSuccessful()) {
                    syncFollowedArtists();
                    // Nếu đã là follower thì hành động là unfollow, nếu chưa thì là follow
                    // API toggle nên chúng ta chỉ refresh
                } else {
                    showToast("Lỗi khi thực hiện thao tác");
                }
            }
            @Override
            public void onFailure(Call<SimpleResponse<Void>> call, Throwable t) {
                showToast("Lỗi kết nối");
            }
        });
    }

    public void unfollowArtist(String artistId) {
        String userId = getUserId();
        if (userId == null) return;

        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        body.put("artistId", artistId);
        RetrofitClient.getApiService().toggleFollowArtist(body).enqueue(new Callback<SimpleResponse<Void>>() {
            @Override
            public void onResponse(Call<SimpleResponse<Void>> call, Response<SimpleResponse<Void>> response) {
                if (response.isSuccessful()) {
                    syncFollowedArtists();
                    showToast("Đã bỏ theo dõi nghệ sĩ");
                } else {
                    showToast("Lỗi khi bỏ theo dõi");
                }
            }
            @Override
            public void onFailure(Call<SimpleResponse<Void>> call, Throwable t) {
                showToast("Lỗi kết nối");
            }
        });
    }

    public void clearCache() {
        prefs.edit().clear().commit();
    }
}
