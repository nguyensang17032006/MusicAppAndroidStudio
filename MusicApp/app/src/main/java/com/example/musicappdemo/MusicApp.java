package com.example.musicappdemo;

import android.app.Application;
import android.se.omapi.Session;

import com.example.musicappdemo.data.SessionManager;

public class MusicApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SessionManager.get(this);
        com.example.musicappdemo.data.SocketManager.getInstance().init(this);
    }
}
