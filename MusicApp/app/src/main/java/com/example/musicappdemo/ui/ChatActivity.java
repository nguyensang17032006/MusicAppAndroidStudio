package com.example.musicappdemo.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappdemo.R;
import com.example.musicappdemo.adapter.ChatAdapter;
import com.example.musicappdemo.data.ApiService;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.data.SessionManager;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.data.SocketManager;
import com.example.musicappdemo.model.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private String friendId;
    private String friendName;
    private String myUserId;

    private RecyclerView rvChat;
    private ChatAdapter chatAdapter;
    private EditText etMessage;

    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        friendId = getIntent().getStringExtra("FRIEND_ID");
        friendName = getIntent().getStringExtra("FRIEND_NAME");
        myUserId = SessionManager.get(this).getUserId();

        TextView tvChatName = findViewById(R.id.tv_chat_name);
        if (friendName != null) {
            tvChatName.setText(friendName);
        }

        rvChat = findViewById(R.id.rv_chat);
        etMessage = findViewById(R.id.et_message);

        // Hide placeholder text
        TextView placeholder = findViewById(R.id.tv_chat_name); 
        // Actually, the placeholder text doesn't have an ID in XML, let's ignore it or the adapter will cover it.

        chatAdapter = new ChatAdapter(myUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(chatAdapter);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_send).setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                etMessage.setText("");
            }
        });

        mSocket = SocketManager.getInstance().getSocket();
        if (mSocket != null) {
            mSocket.on("receive_message", onNewMessage);
        }

        loadChatHistory();
    }

    private void loadChatHistory() {
        if (myUserId == null || friendId == null) return;

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getChatHistory(myUserId, friendId).enqueue(new Callback<SimpleResponse<List<ChatMessage>>>() {
            @Override
            public void onResponse(Call<SimpleResponse<List<ChatMessage>>> call, Response<SimpleResponse<List<ChatMessage>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<ChatMessage> messages = response.body().getData();
                    chatAdapter.setMessages(messages);
                    if (!messages.isEmpty()) {
                        rvChat.scrollToPosition(messages.size() - 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse<List<ChatMessage>>> call, Throwable t) {
                Log.e("ChatActivity", "Error loading chat history", t);
                Toast.makeText(ChatActivity.this, "Lỗi khi tải tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String messageText) {
        if (mSocket != null && mSocket.connected()) {
            JSONObject data = new JSONObject();
            try {
                data.put("senderId", myUserId);
                data.put("receiverId", friendId);
                data.put("message", messageText);
                mSocket.emit("send_message", data);
                
                // Add message to local view instantly for better UX
                ChatMessage newMsg = new ChatMessage(myUserId, friendId, messageText);
                chatAdapter.addMessage(newMsg);
                rvChat.scrollToPosition(chatAdapter.getItemCount() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String senderId = data.getString("sender_id");
                        String receiverId = data.getString("receiver_id");
                        String messageText = data.getString("message_text");

                        // If the message is from the friend we are currently chatting with
                        if (senderId.equals(friendId) && receiverId.equals(myUserId)) {
                            ChatMessage newMsg = new ChatMessage(senderId, receiverId, messageText);
                            chatAdapter.addMessage(newMsg);
                            rvChat.scrollToPosition(chatAdapter.getItemCount() - 1);
                        }
                    } catch (JSONException e) {
                        Log.e("ChatActivity", "JSON parsing error", e);
                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.off("receive_message", onNewMessage);
        }
    }
}
