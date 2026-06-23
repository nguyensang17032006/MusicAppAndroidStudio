package com.example.musicappdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicappdemo.MainActivity;
import com.example.musicappdemo.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class VerifyEmailActivity extends AppCompatActivity {

    private TextInputEditText edtVerifyCode;
    private MaterialButton btnVerify;
    private TextView txtResendCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify_email_activity);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
    }

    private void initViews() {
        edtVerifyCode = findViewById(R.id.edtVerifyCode);
        btnVerify = findViewById(R.id.btnVerify);
        txtResendCode = findViewById(R.id.txtResendCode);
    }

    private void setupListeners() {
        btnVerify.setOnClickListener(v -> {
            String code = edtVerifyCode.getText().toString().trim();
            if (code.length() == 6) {
                // Giả lập xác thực thành công
                Toast.makeText(this, "Xác thực thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(VerifyEmailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                edtVerifyCode.setError("Vui lòng nhập đủ 6 chữ số");
            }
        });

        txtResendCode.setOnClickListener(v -> {
            Toast.makeText(this, "Đã gửi lại mã xác thực", Toast.LENGTH_SHORT).show();
        });
    }
}