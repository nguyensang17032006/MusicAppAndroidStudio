package com.example.musicappdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.R;
import com.example.musicappdemo.databinding.ItemMusicBinding;
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
        ItemMusicBinding binding = ItemMusicBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);
        String artistName = (song.getArtists() != null && !song.getArtists().isEmpty()) ? song.getArtists().get(0).getName() : "Unknown Artist";

        holder.binding.tvLessonTitle.setText(song.getTitle());
        holder.binding.tvInfo.setText(artistName);

        // Ẩn trái tim và dấu 3 chấm khi trong danh sách phát (Player)
        holder.binding.lessonActions.setVisibility(View.GONE);

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

        holder.binding.btnMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.binding.btnMore);
            popupMenu.getMenu().add("Thêm vào danh sách phát");
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Thêm vào danh sách phát")) {
                    MusicManager.getInstance().addSongToPlaylist(context, song);
                    Toast.makeText(context, "Đã thêm vào danh sách phát", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemMusicBinding binding;

        public ViewHolder(ItemMusicBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
