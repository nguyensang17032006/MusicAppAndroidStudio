package com.example.musicappdemo.model;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {
    @SerializedName("id")
    private long id;

    @SerializedName("sender_id")
    private String senderId;

    @SerializedName("receiver_id")
    private String receiverId;

    @SerializedName("message_text")
    private String messageText;

    @SerializedName("created_at")
    private String createdAt;

    public ChatMessage() {}

    public ChatMessage(String senderId, String receiverId, String messageText) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
