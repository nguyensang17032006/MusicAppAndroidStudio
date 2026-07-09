package com.example.musicappdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.musicappdemo.R;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.data.SessionManager;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.databinding.ActivityProfileBinding;
import com.example.musicappdemo.model.auth.SupabaseUser;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadUserData();

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

        binding.btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        binding.btnLogout.setOnClickListener(v -> {
            // 1. Dừng nhạc ngay lập tức
            com.example.musicappdemo.utils.MusicManager.getInstance().stopMusic();
            
            // 2. Xóa phiên làm việc
            SessionManager.get(this).logout();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            
            // 3. Chuyển về màn hình Splash/Login
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserData() {
        String email = SessionManager.get(this).getEmail();
        String gender = SessionManager.get(this).getGender();
        
        if (email != null) {
            binding.etProfileEmail.setText(email);
        }
        if (gender != null) {
            binding.actvGender.setText(gender, false);
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
                        
                        // Cập nhật lại vào SessionManager để đồng bộ
                        // Bạn có thể tạo thêm hàm updateGender trong SessionManager nếu cần
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

        Map<String, String> body = new HashMap<>();
        body.put("userId", userId);
        body.put("gender", gender);

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
}
