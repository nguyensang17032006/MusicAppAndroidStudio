package com.example.musicappdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.R;
import com.example.musicappdemo.databinding.ItemFeaturedProgramBinding;
import com.example.musicappdemo.model.Song;

import java.util.List;

public class FeaturedSongAdapter extends RecyclerView.Adapter<FeaturedSongAdapter.ViewHolder> {

    private Context context;
    private List<Song> songList;

    public FeaturedSongAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFeaturedProgramBinding binding = ItemFeaturedProgramBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.binding.tvTitle.setText(song.getTitle());
        holder.binding.tvSubtitle.setText(song.getArtist_names());

        if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
            Glide.with(context).load(song.getCover_url()).into(holder.binding.imgArtwork);
        } else {
            holder.binding.imgArtwork.setImageResource(R.drawable.placeholder_img);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemFeaturedProgramBinding binding;

        public ViewHolder(ItemFeaturedProgramBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
