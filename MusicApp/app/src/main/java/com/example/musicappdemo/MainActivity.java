package com.example.musicappdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.databinding.ActivityMainBinding;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.ui.HomeFragment;
import com.example.musicappdemo.ui.LibraryFragment;
import com.example.musicappdemo.ui.SearchFragment;
import com.example.musicappdemo.utils.MusicManager;
import com.example.musicappdemo.data.SessionManager;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.data.SimpleResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MusicManager.OnMusicStatusListener {

    private ActivityMainBinding binding;
    private Handler progressHandler = new Handler();
    private Runnable progressRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        com.example.musicappdemo.utils.LibraryManager.getInstance(this).syncAll();

        MusicManager.getInstance().setListener(this);

        updateMiniPlayerVisibility();

        String currentUserId = SessionManager.get(this).getUserId();
        if (currentUserId != null && !currentUserId.isEmpty()) {
            com.example.musicappdemo.data.SocketManager.getInstance().connect(currentUserId);
        }

        // Hiển thị Trang Chủ đầu tiên
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.fragmentContainer.getId(), new HomeFragment())
                    .commit();
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment selectedFragment;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (id == R.id.nav_library) {
                selectedFragment = new LibraryFragment();
            } else if (id == R.id.nav_friends) {
                selectedFragment = new com.example.musicappdemo.ui.FriendFragment();
            } else {
                return false;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(binding.fragmentContainer.getId(), selectedFragment)
                    .commit();
            return true;
        });

        // Xử lý khi click vào Mini Player
        binding.miniPlayer.getRoot().setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, PlayerActivity.class));
        });

        binding.miniPlayer.miniPlayerPlay.setOnClickListener(v -> MusicManager.getInstance().togglePause());

        binding.miniPlayer.miniPlayerClose.setOnClickListener(v -> {
            MusicManager.getInstance().stopMusic();
            updateMiniPlayerVisibility();
        });

        setupProgressUpdate();
        
        handleDeepLinkIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleDeepLinkIntent(intent);
    }

    private void handleDeepLinkIntent(Intent intent) {
        if (intent != null && intent.hasExtra("inviterId")) {
            String inviterId = intent.getStringExtra("inviterId");
            if (inviterId != null && !inviterId.isEmpty()) {
                showAcceptFriendDialog(inviterId);
            }
        }
    }

    private void showAcceptFriendDialog(String inviterId) {
        new AlertDialog.Builder(this)
                .setTitle("Kết bạn mới")
                .setMessage("Bạn có muốn kết bạn qua liên kết này không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> acceptFriend(inviterId))
                .setNegativeButton("Hủy bỏ", null)
                .show();
    }

    private void acceptFriend(String inviterId) {
        String currentUserId = SessionManager.get(this).getUserId();
        if (currentUserId == null || currentUserId.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập để kết bạn", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("inviterId", inviterId);
        body.put("receiverId", currentUserId);

        RetrofitClient.getApiService().acceptFriendViaLink(body).enqueue(new Callback<SimpleResponse<Void>>() {
            @Override
            public void onResponse(Call<SimpleResponse<Void>> call, Response<SimpleResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Đã kết bạn thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi kết bạn!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse<Void>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        MusicManager.getInstance().setListener(this);
        Song currentSong = MusicManager.getInstance().getCurrentSong();
        if (currentSong != null) {
            onSongChanged(currentSong);
            onStatusChanged(MusicManager.getInstance().isPlaying());
        }
        updateMiniPlayerVisibility();
    }

    private void updateMiniPlayerVisibility() {
        if (MusicManager.getInstance().getCurrentSong() != null) {
            binding.miniPlayer.getRoot().setVisibility(android.view.View.VISIBLE);
        } else {
            binding.miniPlayer.getRoot().setVisibility(android.view.View.GONE);
        }
    }

    @Override
    public void onSongChanged(Song song) {
        updateMiniPlayerVisibility();
        if (song != null) {
            String currentUserId = SessionManager.get(this).getUserId();
            com.example.musicappdemo.data.SocketManager.getInstance().emitPlayingSong(currentUserId, song.getTitle());

            String artistName = (song.getArtists() != null && !song.getArtists().isEmpty()) ? song.getArtists().get(0).getName() : "Unknown Artist";

            binding.miniPlayer.miniPlayerTitle.setText(song.getTitle());
            binding.miniPlayer.miniPlayerArtist.setText(artistName);
            if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
                Glide.with(this).load(song.getCover_url()).into(binding.miniPlayer.miniPlayerImg);
            } else {
                binding.miniPlayer.miniPlayerImg.setImageResource(R.drawable.placeholder_img);
            }
            binding.miniPlayer.miniPlayerProgress.setProgress(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressHandler.removeCallbacks(progressRunnable);
        com.example.musicappdemo.data.SocketManager.getInstance().disconnect();
    }

    @Override
    public void onStatusChanged(boolean isPlaying) {
        binding.miniPlayer.miniPlayerPlay.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
    }
}
