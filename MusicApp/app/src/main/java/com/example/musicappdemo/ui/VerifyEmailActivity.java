package com.example.musicappdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicappdemo.MainActivity;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.data.SessionManager;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.databinding.ActivityVerifyEmailActivityBinding;
import com.example.musicappdemo.model.auth.AuthRequest;
import com.example.musicappdemo.model.auth.AuthResponse;
import com.example.musicappdemo.model.auth.RegisterOtpRequest;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailActivity extends AppCompatActivity {

    private ActivityVerifyEmailActivityBinding binding;
    private String email, password, gender;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyEmailActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Hứng dữ liệu từ SignUpActivity chuyển sang
        String type = getIntent().getStringExtra("type");
        if ("signup".equals(type)) {
            email = getIntent().getStringExtra("email");
            gender = getIntent().getStringExtra("gender");
            password = getIntent().getStringExtra("password");
        } else {
            email = getIntent().getStringExtra("email");
        }

        // Kích hoạt bộ đếm ngược 60s ngay khi vừa vào màn hình này
        startResendTimer();

        // 2. Xử lý nút Xác nhận mã OTP
        binding.btnVerify.setOnClickListener(v -> {
            String otpCode = binding.edtVerifyCode.getText().toString().trim();

            if (otpCode.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã OTP xác thực!", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("signup".equals(type)) {
                performRegisterVerify(otpCode);
            } else {
                performForgotPasswordVerify(otpCode);
            }
        });

        setupResendCode(type);
    }

    private void performRegisterVerify(String otpCode) {
        RegisterOtpRequest request = new RegisterOtpRequest(email, otpCode, password, gender);
        RetrofitClient.getApiService().verifyAndRegister(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                System.out.println("👉 Register Verify HTTP Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();

                    // Kiểm tra xem token trả về có bị null không
                    if (authResponse.accessToken == null || authResponse.accessToken.isEmpty()) {
                        Toast.makeText(VerifyEmailActivity.this, "Lỗi: Server không trả về token đăng nhập!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (countDownTimer != null) countDownTimer.cancel(); // Tắt đếm ngược

                    // Lưu session đăng nhập vào máy
                    SessionManager.get(VerifyEmailActivity.this).save(authResponse);

                    Toast.makeText(VerifyEmailActivity.this, "Xác thực đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    // Bế vào MainActivity
                    Intent intent = new Intent(VerifyEmailActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Mã OTP đăng ký không chính xác hoặc đã hết hạn!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(VerifyEmailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performForgotPasswordVerify(String otpCode) {
        RegisterOtpRequest request = new RegisterOtpRequest(email, otpCode, null, null);

        // Đổi kiểu Callback từ SimpleResponse thành AuthResponse
        RetrofitClient.getApiService().verifyOtpForgotPassword(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (countDownTimer != null) countDownTimer.cancel();
                    Toast.makeText(VerifyEmailActivity.this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();

                    // Lấy Access Token tạm thời do Node.js trả về
                    AuthResponse authResponse = response.body();
                    String tokenTamThoi = authResponse.accessToken; // Đảm bảo class AuthResponse của bạn có hàm này (hoặc biến public)

                    // Chuyển sang màn hình ResetPassword và đính kèm Token + Email
                    Intent intent = new Intent(VerifyEmailActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("access_token", tokenTamThoi);
                    Toast.makeText(VerifyEmailActivity.this,""+ tokenTamThoi,Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Mã OTP không chính xác hoặc đã hết hạn!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(VerifyEmailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

        // 3. Xử lý sự kiện bấm "Gửi lại mã" (Resend OTP)
    private void setupResendCode(String type) {
        binding.txtResendCode.setOnClickListener(v -> {
            if (isTimerRunning) return;

            if ("signup".equals(type)) {
                AuthRequest authRequest = new AuthRequest(email, password, gender);
                RetrofitClient.getApiService().sendOtp(authRequest).enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(VerifyEmailActivity.this, "Mã OTP mới đã được gửi!", Toast.LENGTH_SHORT).show();
                            startResendTimer();
                        } else {
                            Toast.makeText(VerifyEmailActivity.this, "Gửi lại mã thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        Toast.makeText(VerifyEmailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                AuthRequest authRequest = new AuthRequest(email, null, null);
                RetrofitClient.getApiService().sendOtpForgotPassword(authRequest).enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(VerifyEmailActivity.this, "Mã OTP khôi phục đã được gửi!", Toast.LENGTH_SHORT).show();
                            startResendTimer();
                        } else {
                            Toast.makeText(VerifyEmailActivity.this, "Gửi lại mã thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        Toast.makeText(VerifyEmailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // 🕒 Hàm đếm ngược 60 giây khóa nút gửi lại
    private void startResendTimer() {
        isTimerRunning = true;
        binding.txtResendCode.setEnabled(false); // Vô hiệu hóa khả năng bấm click

        // 60000ms = 60s, mỗi bước giảm (tick) là 1000ms = 1s
        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                // Hiển thị text dạng: "Gửi lại mã (59s)"
                binding.txtResendCode.setText(String.format(Locale.getDefault(), "Gửi lại mã (%ds)", seconds));
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                binding.txtResendCode.setEnabled(true); // Cho phép bấm lại thoải mái
                binding.txtResendCode.setText("Gửi lại mã"); // Trả lại text ban đầu
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy timer nếu user thoát màn hình giữa chừng để tránh rò rỉ bộ nhớ (Memory Leak)
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}