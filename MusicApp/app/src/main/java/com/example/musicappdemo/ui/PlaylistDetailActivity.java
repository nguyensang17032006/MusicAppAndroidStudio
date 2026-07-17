package com.example.musicappdemo.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.R;
import com.example.musicappdemo.adapter.SearchResultAdapter;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.databinding.ActivityPlaylistDetailBinding;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.model.UploadResponse;
import com.example.musicappdemo.utils.LibraryManager;
import com.example.musicappdemo.utils.MusicManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Handler;

public class PlaylistDetailActivity extends AppCompatActivity implements MusicManager.OnMusicStatusListener {

    private ActivityPlaylistDetailBinding binding;
    private List<Song> songList = new ArrayList<>();
    private String playlistId;

    private Handler progressHandler = new Handler();
    private Runnable progressRunnable;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        uploadPlaylistCover(selectedImageUri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        playlistId = getIntent().getStringExtra("playlist_id");
        String title = getIntent().getStringExtra("playlist_title");
        String coverUrl = getIntent().getStringExtra("playlist_cover");
        ArrayList<Song> songs = (ArrayList<Song>) getIntent().getSerializableExtra("playlist_songs");

        if (title != null) binding.tvPlaylistTitle.setText(title);
        if (songs != null) songList.addAll(songs);

        boolean isGenre = getIntent().getBooleanExtra("is_genre", false);

        // Xử lý riêng cho Liked Songs hoặc Thể loại: Không cho đổi ảnh, dùng icon mặc định
        if (title != null && title.equalsIgnoreCase("Liked Songs")) {
            binding.btnChangeCover.setVisibility(android.view.View.GONE);
            binding.ivPlaylistCover.setImageResource(R.drawable.ic_heart);
            binding.ivPlaylistCover.setBackgroundResource(R.color.S20);
            binding.ivPlaylistCover.setPadding(60, 60, 60, 60); // Padding lớn cho icon nốt nhạc
        } else if (isGenre) {
            binding.btnChangeCover.setVisibility(android.view.View.GONE);
            binding.ivPlaylistCover.setBackgroundResource(R.color.S20);
            binding.ivPlaylistCover.setPadding(60, 60, 60, 60);
        } else if (coverUrl != null && !coverUrl.isEmpty()) {
            Glide.with(this)
                    .load(RetrofitClient.getFullUrl(coverUrl))
                    .placeholder(R.drawable.ic_music_note)
                    .into(binding.ivPlaylistCover);
            binding.ivPlaylistCover.setPadding(0, 0, 0, 0);
        }

        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnChangeCover.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        binding.fabPlayAll.setOnClickListener(v -> {
            if (!songList.isEmpty()) {
                MusicManager.getInstance().playPlaylist(this, songList, 0);
            } else {
                Toast.makeText(this, "Playlist trống", Toast.LENGTH_SHORT).show();
            }
        });

        // Mini player events
        binding.miniPlayer.getRoot().setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.musicappdemo.PlayerActivity.class));
        });

        binding.miniPlayer.miniPlayerPlay.setOnClickListener(v -> MusicManager.getInstance().togglePause());

        binding.miniPlayer.miniPlayerClose.setOnClickListener(v -> {
            MusicManager.getInstance().stopMusic();
            updateMiniPlayerVisibility();
        });

        setupProgressUpdate();
        setupRecyclerView();
        setupMiniPlayer();

        LibraryManager.getInstance(this).setSyncListener(() -> {
            runOnUiThread(() -> {
                // Refresh list from manager
                List<com.example.musicappdemo.model.Playlist> playlists = LibraryManager.getInstance(this).getPlaylists();
                for (com.example.musicappdemo.model.Playlist p : playlists) {
                    if (p.getId().equals(playlistId)) {
                        songList.clear();
                        songList.addAll(p.getSongs());
                        if (binding.rvPlaylistSongs.getAdapter() != null) {
                            binding.rvPlaylistSongs.getAdapter().notifyDataSetChanged();
                        }
                        break;
                    }
                }
            });
        });
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

    private void setupProgressUpdate() {
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


    private void updateMiniPlayerVisibility() {
        if (MusicManager.getInstance().getCurrentSong() != null) {
            binding.miniPlayer.getRoot().setVisibility(android.view.View.VISIBLE);
        } else {
            binding.miniPlayer.getRoot().setVisibility(android.view.View.GONE);
        }
    }


    private void uploadPlaylistCover(Uri uri) {
        try {
            File file = getFileFromUri(uri);
            if (file == null) return;

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            RetrofitClient.getApiService().uploadFile(body).enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("PlaylistDetail", "Upload Success: " + response.body().getFileUrl());
                        String uploadedUrl = response.body().getFileUrl();
                        updatePlaylistCoverOnServer(uploadedUrl);
                    } else {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            Log.e("PlaylistDetail", "Upload Failed. Code: " + response.code() + " Error: " + errorBody);
                            Toast.makeText(PlaylistDetailActivity.this, "Upload thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("PlaylistDetail", "Error reading error body", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<com.example.musicappdemo.model.UploadResponse> call, Throwable t) {
                    Log.e("PlaylistDetail", "Upload Network Error", t);
                    Toast.makeText(PlaylistDetailActivity.this, "Lỗi kết nối upload: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e("PlaylistDetail", "Error: " + e.getMessage());
        }
    }

    private void updatePlaylistCoverOnServer(String coverUrl) {
        // Nếu URL là tương đối (bắt đầu bằng /uploads), lưu nó vào database
        // Khi hiển thị, Glide sẽ dùng BASE_URL + coverUrl
        Map<String, String> body = new HashMap<>();
        body.put("playlistId", playlistId);
        body.put("coverUrl", coverUrl);

        RetrofitClient.getApiService().updatePlaylistCover(body).enqueue(new Callback<SimpleResponse<Void>>() {
            @Override
            public void onResponse(Call<SimpleResponse<Void>> call, Response<SimpleResponse<Void>> response) {
                if (response.isSuccessful()) {
                    // Cập nhật giao diện ngay lập tức
                    String fullUrl = RetrofitClient.getFullUrl(coverUrl);
                    Glide.with(PlaylistDetailActivity.this).load(fullUrl).into(binding.ivPlaylistCover);
                    binding.ivPlaylistCover.setPadding(0, 0, 0, 0);
                    Toast.makeText(PlaylistDetailActivity.this, "Ảnh bìa đã được cập nhật", Toast.LENGTH_SHORT).show();
                    
                    // Đồng bộ lại danh sách playlist ở màn hình ngoài
                    com.example.musicappdemo.utils.LibraryManager.getInstance(PlaylistDetailActivity.this).syncPlaylists();
                } else {
                    Toast.makeText(PlaylistDetailActivity.this, "Cập nhật server thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse<Void>> call, Throwable t) {
                Toast.makeText(PlaylistDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = new File(getCacheDir(), "temp_image_" + System.currentTimeMillis());
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            return null;
        }
    }

    private void setupRecyclerView() {
        SearchResultAdapter adapter = new SearchResultAdapter(this, songList, playlistId);
        binding.rvPlaylistSongs.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPlaylistSongs.setAdapter(adapter);
    }
}
