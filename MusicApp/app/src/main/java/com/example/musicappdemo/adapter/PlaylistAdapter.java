package com.example.musicappdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.R;
import com.example.musicappdemo.databinding.ItemLessonBinding;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.utils.MusicManager;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private Context context;
    private List<Song> songList;
    private int playingIndex = -1;

    public PlaylistAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    public void setPlayingIndex(int index) {
        this.playingIndex = index;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLessonBinding binding = ItemLessonBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.binding.tvLessonTitle.setText(song.getTitle());
        holder.binding.tvLessonInfo.setText(song.getArtist_names());

        if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
            Glide.with(context).load(song.getCover_url()).into(holder.binding.lessonImg);
        } else {
            holder.binding.lessonImg.setImageResource(R.drawable.placeholder_img);
        }

        if (position == playingIndex) {
            holder.itemView.setBackgroundColor(Color.parseColor("#33FFFFFF")); // Khung sáng hơn
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(v -> {
            MusicManager.getInstance().playPlaylist(context, songList, position);
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemLessonBinding binding;

        public ViewHolder(ItemLessonBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
