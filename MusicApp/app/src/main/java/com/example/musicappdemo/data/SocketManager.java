package com.example.musicappdemo.data;

import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    private static SocketManager instance;
    private Socket mSocket;
    private static final String SERVER_URL = "http://10.0.2.2:3000"; // Hoặc địa chỉ VPS của bạn

    private SocketManager() {
        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            Log.e("SocketManager", "Lỗi cú pháp URL", e);
        }
    }

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void connect(String userId) {
        if (!mSocket.connected()) {
            mSocket.connect();
        }
        // Emit luôn sau khi connect
        mSocket.on(Socket.EVENT_CONNECT, args -> {
            Log.d("SocketManager", "Connected to server!");
            if (userId != null && !userId.isEmpty()) {
                mSocket.emit("user_connected", userId);
            }
        });
    }

    public void emitPlayingSong(String userId, String songTitle) {
        if (mSocket.connected() && userId != null) {
            try {
                org.json.JSONObject data = new org.json.JSONObject();
                data.put("userId", userId);
                data.put("songTitle", songTitle);
                mSocket.emit("playing_song", data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (mSocket.connected()) {
            mSocket.disconnect();
        }
    }
}
