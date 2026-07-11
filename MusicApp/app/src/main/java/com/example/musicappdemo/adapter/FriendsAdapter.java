package com.example.musicappdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.R;
import com.example.musicappdemo.model.Friend;
import com.example.musicappdemo.ui.ChatActivity;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    private Context context;
    private List<Friend> friends;

    public FriendsAdapter(Context context, List<Friend> friends) {
        this.context = context;
        this.friends = friends;
    }

    public void updateData(List<Friend> newFriends) {
        this.friends = newFriends;
        notifyDataSetChanged();
    }

    public void updateFriendStatus(String userId, boolean isOnline, String currentSong) {
        if (friends == null) return;
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).getId().equals(userId)) {
                friends.get(i).setOnline(isOnline);
                friends.get(i).setCurrentSong(currentSong);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friends.get(position);
        
        holder.tvName.setText(friend.getEmail());
        holder.tvStreak.setText(friend.getStreak() + " \uD83D\uDD25"); // 🔥 emoji

        if (friend.isOnline()) {
            if (friend.getCurrentSong() != null && !friend.getCurrentSong().isEmpty()) {
                holder.tvStatus.setText("Đang nghe: " + friend.getCurrentSong());
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#1DB954")); // Xanh lá
            } else {
                holder.tvStatus.setText("Online");
                holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#1DB954"));
            }
        } else {
            holder.tvStatus.setText("Offline");
            holder.tvStatus.setTextColor(android.graphics.Color.GRAY);
        }

        if (friend.getAvatarUrl() != null && !friend.getAvatarUrl().isEmpty()) {
            Glide.with(context).load(friend.getAvatarUrl()).placeholder(R.drawable.ic_user).into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_user);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("FRIEND_ID", friend.getId());
            intent.putExtra("FRIEND_NAME", friend.getEmail());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Xóa bạn bè")
                .setMessage("Bạn có chắc chắn muốn xóa " + friend.getEmail() + " khỏi danh sách bạn bè?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    String myUserId = com.example.musicappdemo.data.SessionManager.get(context).getUserId();
                    if (myUserId == null) return;
                    
                    java.util.Map<String, String> body = new java.util.HashMap<>();
                    body.put("userId1", myUserId);
                    body.put("userId2", friend.getId());
                    
                    com.example.musicappdemo.data.RetrofitClient.getApiService().removeFriend(body).enqueue(new retrofit2.Callback<com.example.musicappdemo.data.SimpleResponse<Void>>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.example.musicappdemo.data.SimpleResponse<Void>> call, retrofit2.Response<com.example.musicappdemo.data.SimpleResponse<Void>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                android.widget.Toast.makeText(context, "Đã xóa bạn bè", android.widget.Toast.LENGTH_SHORT).show();
                                friends.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                            } else {
                                android.widget.Toast.makeText(context, "Lỗi khi xóa bạn bè", android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }
                        
                        @Override
                        public void onFailure(retrofit2.Call<com.example.musicappdemo.data.SimpleResponse<Void>> call, Throwable t) {
                            android.widget.Toast.makeText(context, "Lỗi mạng: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
        });
    }

    @Override
    public int getItemCount() {
        return friends != null ? friends.size() : 0;
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvStatus;
        TextView tvStreak;
        ImageView btnDelete;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_friend_avatar);
            tvName = itemView.findViewById(R.id.tv_friend_name);
            tvStatus = itemView.findViewById(R.id.tv_friend_status);
            tvStreak = itemView.findViewById(R.id.tv_friend_streak);
            btnDelete = itemView.findViewById(R.id.btn_delete_friend);
        }
    }
}
