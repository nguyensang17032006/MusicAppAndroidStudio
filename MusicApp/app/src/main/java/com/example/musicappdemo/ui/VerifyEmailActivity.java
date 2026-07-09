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
        email = getIntent().getStringExtra("email");
        gender = getIntent().getStringExtra("gender");
        password = getIntent().getStringExtra("password");

        // Kích hoạt bộ đếm ngược 60s ngay khi vừa vào màn hình này
        startResendTimer();

        // 2. Xử lý nút Xác nhận mã OTP
        binding.btnVerify.setOnClickListener(v -> {
            String otpCode = binding.edtVerifyCode.getText().toString().trim();

            if (otpCode.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã OTP xác thực!", Toast.LENGTH_SHORT).show();
                return;
            }

            RegisterOtpRequest request = new RegisterOtpRequest(email, otpCode, password, gender);

            RetrofitClient.getApiService().verifyAndRegister(request).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    try {
                        if (response.raw().body() != null) {
                            System.out.println("👉 Chuỗi JSON thô từ Node.js: " + response.raw().toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // 🚀 IN LOG KIỂM TRA BODY CÓ BỊ NULL KHÔNG
                    System.out.println("👉 response.isSuccessful(): " + response.isSuccessful());
                    System.out.println("👉 response.body() có bị null không?: " + (response.body() == null));
                    if (response.isSuccessful() && response.body() != null) {
                        if (countDownTimer != null) countDownTimer.cancel(); // Tắt đếm ngược nếu thành công

                        AuthResponse authResponse = response.body();
                        SessionManager.get(VerifyEmailActivity.this).save(authResponse);

                        Toast.makeText(VerifyEmailActivity.this, "Xác thực thành công! Chào mừng bạn.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(VerifyEmailActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        });

        // 3. Xử lý sự kiện bấm "Gửi lại mã" (Resend OTP)
        // ⚠️ Hãy đổi lại ID 'tvResendOtp' đúng với ID TextView/Button trong file XML của bạn nhé!
        binding.txtResendCode.setOnClickListener(v -> {
            if (isTimerRunning) {
                // Nếu đang đếm ngược thì không cho bấm gửi lại
                return;
            }

            // Gọi API Node.js để gửi lại OTP mới
            AuthRequest authRequest = new AuthRequest(email,password,gender);

            RetrofitClient.getApiService().sendOtp(authRequest).enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(VerifyEmailActivity.this, "Mã OTP mới đã được gửi!", Toast.LENGTH_SHORT).show();
                        // Kích hoạt lại bộ đếm ngược 60s
                        startResendTimer();
                    } else {
                        Toast.makeText(VerifyEmailActivity.this, "Gửi lại mã thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    Toast.makeText(VerifyEmailActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


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