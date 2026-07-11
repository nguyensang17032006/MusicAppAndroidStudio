package com.example.musicappdemo.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

public class PlaylistDetailActivity extends AppCompatActivity {

    private ActivityPlaylistDetailBinding binding;
    private List<Song> songList = new ArrayList<>();
    private String playlistId;

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

        if (coverUrl != null && !coverUrl.isEmpty()) {
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
                Toast.makeText(this, "Đang phát tất cả bài hát", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Playlist trống", Toast.LENGTH_SHORT).show();
            }
        });

        setupRecyclerView();
    }

    private void uploadPlaylistCover(Uri uri) {
        try {
            File file = getFileFromUri(uri);
            if (file == null) return;

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            RetrofitClient.getApiService().uploadFile(body).enqueue(new Callback<com.example.musicappdemo.model.UploadResponse>() {
                @Override
                public void onResponse(Call<com.example.musicappdemo.model.UploadResponse> call, Response<com.example.musicappdemo.model.UploadResponse> response) {
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
        SearchResultAdapter adapter = new SearchResultAdapter(this, songList);
        binding.rvPlaylistSongs.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPlaylistSongs.setAdapter(adapter);
    }
}
