package com.example.musicappdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicappdemo.R;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.databinding.ActivityResetPasswordBinding;
import com.example.musicappdemo.model.auth.NewPasswordRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String email = getIntent().getStringExtra("email");

        binding.btnResetPassword.setOnClickListener(v -> {
            String newPassword = binding.edtNewPassword.getText().toString().trim();
            String confirmPassword = binding.edtConfirmNewPassword.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ các trường!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Lấy accessToken tạm thời đã lưu từ Bước 2 (Xác thực OTP thành công)
            // Nếu bạn truyền token qua Intent từ Activity trước sang, hãy lấy như sau:
            String accessToken = getIntent().getStringExtra("access_token");

            // 2. Khởi tạo Object Request gửi kèm Email để Node.js in Log cho dễ nhìn (nếu muốn)
            NewPasswordRequest newPasswordRequest = new NewPasswordRequest(accessToken, newPassword);

            RetrofitClient.getApiService().updateNewPassword(newPasswordRequest).enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    // Log kiểm tra mã trạng thái HTTP trả về từ Node.js (Ví dụ: 200, 400, 500)
                    System.out.println("👉 Update Password HTTP Code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ResetPasswordActivity.this, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();

                        // 3. Đổi mật khẩu thành công hoàn toàn -> Bế User quay xe về màn hình Login ban đầu
                        Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Trường hợp Server trả về lỗi (Ví dụ: Mật khẩu quá ngắn, token hết hạn)
                        String errorMsg = "Cập nhật mật khẩu thất bại!";
                        try {
                            if (response.errorBody() != null) {
                                // Đọc chuỗi JSON lỗi từ server nếu có
                                errorMsg = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ResetPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    // In log lỗi chi tiết ra Logcat để dễ debug khi mất kết nối mạng
                    System.out.println("❌ LỖI UPDATE PASSWORD DO: " + t.getMessage());
                    Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}