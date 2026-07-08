package com.example.musicappdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.musicappdemo.MainActivity;
import com.example.musicappdemo.R;
import com.example.musicappdemo.data.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::route, 800);
    }

    private void route() {
        boolean loggedIn = SessionManager.get(this).isLoggedIn();
        Intent intent = new Intent(this,
                loggedIn ? MainActivity.class : LoginActivity.class);
        startActivity(intent);
        finish();
    }
}