package com.example.musicappdemo.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappdemo.R;

public class ChatActivity extends AppCompatActivity {

    private String friendId;
    private String friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        friendId = getIntent().getStringExtra("FRIEND_ID");
        friendName = getIntent().getStringExtra("FRIEND_NAME");

        TextView tvChatName = findViewById(R.id.tv_chat_name);
        if (friendName != null) {
            tvChatName.setText(friendName);
        }

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_send).setOnClickListener(v -> {
            EditText etMessage = findViewById(R.id.et_message);
            String message = etMessage.getText().toString();
            if (!message.isEmpty()) {
                Toast.makeText(this, "Tính năng gửi tin nhắn đang phát triển!", Toast.LENGTH_SHORT).show();
                etMessage.setText("");
            }
        });
    }
}
