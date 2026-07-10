package com.example.musicappdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicappdemo.R;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.databinding.ActivityForgotPasswordBinding;
import com.example.musicappdemo.model.auth.AuthRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edtEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    binding.edtEmail.setError("Email is required");
                    return;
                }

                AuthRequest request = new AuthRequest(email,"","");

                RetrofitClient.getApiService().sendOtpForgotPassword(request).enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                        if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
                            Intent intent = new Intent(ForgotPasswordActivity.this, VerifyEmailActivity.class);
                            intent.putExtra("type","forgot");
                            intent.putExtra("email", email);
                            startActivity(intent);
                        }
                        else {
                            String errorMsg = (response.body() != null) ? response.body().getMessage() : "Registration failed";
                            Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        Toast.makeText(ForgotPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}