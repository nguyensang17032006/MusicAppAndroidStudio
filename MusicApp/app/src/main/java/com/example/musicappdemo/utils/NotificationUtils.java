package com.example.musicappdemo.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.musicappdemo.MainActivity;
import com.example.musicappdemo.R;
import com.example.musicappdemo.ui.ChatActivity;

public class NotificationUtils {
    private static final String CHANNEL_ID = "chat_notifications";

    public static void showChatNotification(Context context, String senderId, String messageText) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Tin nhắn",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo khi có tin nhắn mới");
            notificationManager.createNotificationChannel(channel);
        }

        // Bấm vào thông báo sẽ mở MainActivity (hoặc ChatActivity nếu bạn muốn)
        // Mở ChatActivity luôn
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("FRIEND_ID", senderId);
        // friendName sẽ được ChatActivity tự động fetch từ API nếu bị null
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                (int) System.currentTimeMillis(), 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat) // Đảm bảo có icon này, nếu chưa có thì dùng ic_music_note
                .setContentTitle("Bạn có tin nhắn mới")
                .setContentText(messageText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Dùng hashCode của senderId làm notificationId để gộp thông báo của cùng 1 người
        notificationManager.notify(senderId.hashCode(), builder.build());
    }
}
