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
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.databinding.ActivitySignUpBinding;
import com.example.musicappdemo.model.auth.AuthRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = binding.edtEmail.getText().toString().trim();
                final String password = binding.edtPassword.getText().toString().trim();
                String confirmPassword = binding.edtConfirmPassword.getText().toString().trim();
                String gender = "";
                int checkedId = binding.rbgroup.getCheckedRadioButtonId();
                if (checkedId == R.id.rbMale) {
                    gender = "Male";
                } else if (checkedId == R.id.rbFemale) {
                    gender = "Female";
                } else if (checkedId == R.id.rbOther) {
                    gender = "Other";
                }



                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || gender.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String finalGender = gender;
                AuthRequest request = new AuthRequest(email, password, finalGender);

                RetrofitClient.getApiService().sendOtp(request).enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                        if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
                            Toast.makeText(SignUpActivity.this, "Mã OTP đã được gửi !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, VerifyEmailActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("gender",finalGender);
                            intent.putExtra("password",password);
                            startActivity(intent);
                        } else {
                            String errorMsg = (response.body() != null) ? response.body().getMessage() : "Registration failed";
                            Toast.makeText(SignUpActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        Toast.makeText(SignUpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

}