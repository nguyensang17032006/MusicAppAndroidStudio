package com.example.musicappdemo.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.R;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.data.SessionManager;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.databinding.ActivityProfileBinding;
import com.example.musicappdemo.model.auth.SupabaseUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private boolean isEditMode = false;
    private String selectedAvatarUri = null;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        updateAvatar(selectedImageUri.toString());
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), photo, "Avatar_" + System.currentTimeMillis(), null);
                    if (path != null) {
                        updateAvatar(path);
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadUserData();
        loadUserStreak();

        binding.btnBack.setOnClickListener(v -> finish());

        // Setup Gender Dropdown
        String[] genders = new String[]{"Male", "Female", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        binding.actvGender.setAdapter(adapter);

        // Edit/Save Toggle Logic
        binding.btnEditSave.setOnClickListener(v -> {
            if (!isEditMode) {
                enterEditMode();
            } else {
                saveProfileChanges();
            }
        });

        binding.btnChangeAvatar.setOnClickListener(v -> {
            if (isEditMode) {
                showImagePickerDialog();
            } else {
                Toast.makeText(this, "Please enter Edit mode to change photo", Toast.LENGTH_SHORT).show();
            }
        });

        binding.ivProfileAvatar.setOnClickListener(v -> {
            if (isEditMode) {
                showImagePickerDialog();
            }
        });

        binding.btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            com.example.musicappdemo.utils.MusicManager.getInstance().stopMusic();
            com.example.musicappdemo.utils.LibraryManager.getInstance(this).clearCache();
            SessionManager.get(this).logout();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Profile Picture");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkCameraPermissionAndOpen();
            } else if (which == 1) {
                openGallery();
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(takePictureIntent);
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickPhoto);
    }

    private void updateAvatar(String uriString) {
        this.selectedAvatarUri = uriString;
        SessionManager.get(this).saveAvatarUri(uriString);
        Glide.with(this)
                .load(uriString)
                .placeholder(R.drawable.ic_user)
                .into(binding.ivProfileAvatar);
        Toast.makeText(this, "Avatar updated locally", Toast.LENGTH_SHORT).show();
    }

    private void loadUserData() {
        String email = SessionManager.get(this).getEmail();
        String gender = SessionManager.get(this).getGender();
        String avatarUri = SessionManager.get(this).getAvatarUri();
        
        if (email != null) {
            binding.etProfileEmail.setText(email);
        }
        if (gender != null) {
            binding.actvGender.setText(gender, false);
        }
        if (avatarUri != null) {
            Glide.with(this)
                    .load(avatarUri)
                    .placeholder(R.drawable.ic_user)
                    .into(binding.ivProfileAvatar);
        }

        String userId = SessionManager.get(this).getUserId();
        if (userId != null) {
            RetrofitClient.getApiService().getUserProfile(userId).enqueue(new Callback<SimpleResponse<SupabaseUser>>() {
                @Override
                public void onResponse(Call<SimpleResponse<SupabaseUser>> call, Response<SimpleResponse<SupabaseUser>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        SupabaseUser user = response.body().getData();
                        binding.etProfileEmail.setText(user.getEmail());
                        binding.actvGender.setText(user.getGender(), false);
                        
                        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                            SessionManager.get(ProfileActivity.this).saveAvatarUri(user.getAvatarUrl());
                            Glide.with(ProfileActivity.this)
                                    .load(user.getAvatarUrl())
                                    .placeholder(R.drawable.ic_user)
                                    .into(binding.ivProfileAvatar);
                        }
                    }
                }
                @Override
                public void onFailure(Call<SimpleResponse<SupabaseUser>> call, Throwable t) {}
            });
        }
    }

    private void enterEditMode() {
        isEditMode = true;
        binding.btnEditSave.setText("Save");
        binding.btnEditSave.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        binding.actvGender.setEnabled(true);
        Toast.makeText(this, "You can edit now", Toast.LENGTH_SHORT).show();
    }

    private void saveProfileChanges() {
        String userId = SessionManager.get(this).getUserId();
        String gender = binding.actvGender.getText().toString();
        
        // Ưu tiên dùng uri mới chọn, nếu không có thì lấy từ SessionManager
        String avatarToSend = selectedAvatarUri;
        if (avatarToSend == null) {
            avatarToSend = SessionManager.get(this).getAvatarUri();
        }

        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        body.put("gender", gender);
        if (avatarToSend != null) {
            body.put("avatar_url", avatarToSend);
        }

        RetrofitClient.getApiService().updateProfile(body).enqueue(new Callback<SimpleResponse<Void>>() {
            @Override
            public void onResponse(Call<SimpleResponse<Void>> call, Response<SimpleResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();
                    exitEditMode();
                } else {
                    Toast.makeText(ProfileActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse<Void>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void exitEditMode() {
        isEditMode = false;
        binding.btnEditSave.setText("Edit");
        binding.btnEditSave.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_edit, 0);
        binding.actvGender.setEnabled(false);
    }

    private void loadUserStreak() {
        String userId = SessionManager.get(this).getUserId();
        if (userId == null) return;

        RetrofitClient.getApiService().getUserStreak(userId).enqueue(new Callback<SimpleResponse<com.example.musicappdemo.model.UserStreak>>() {
            @Override
            public void onResponse(Call<SimpleResponse<com.example.musicappdemo.model.UserStreak>> call, Response<SimpleResponse<com.example.musicappdemo.model.UserStreak>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    com.example.musicappdemo.model.UserStreak streak = response.body().getData();
                    
                    binding.tvStreakTitle.setText("Current Streak: " + streak.getCurrentStreak() + " day" + (streak.getCurrentStreak() == 1 ? "" : "s"));
                    
                    int mins = streak.getTodayListeningTime() / 60;
                    binding.tvStreakSubtitle.setText("Today: " + mins + "/30 mins listened");
                    binding.tvMaxStreakVal.setText(streak.getMaxStreak() + " 🔥");
                    
                    if (streak.getCurrentStreak() > 0) {
                        binding.tvStreakEmoji.setText("🔥");
                    } else {
                        binding.tvStreakEmoji.setText("❄️");
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse<com.example.musicappdemo.model.UserStreak>> call, Throwable t) {
                android.util.Log.e("ProfileActivity", "Error loading streak: " + t.getMessage());
            }
        });
    }
}
