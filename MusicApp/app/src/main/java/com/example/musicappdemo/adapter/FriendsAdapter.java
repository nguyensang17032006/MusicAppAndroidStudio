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
        holder.tvStatus.setText("Bạn bè");

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

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_friend_avatar);
            tvName = itemView.findViewById(R.id.tv_friend_name);
            tvStatus = itemView.findViewById(R.id.tv_friend_status);
            tvStreak = itemView.findViewById(R.id.tv_friend_streak);
        }
    }
}
