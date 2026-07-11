package com.example.musicappdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappdemo.R;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.databinding.ItemPlaylistLibraryBinding;
import com.example.musicappdemo.model.Playlist;

import java.util.List;

public class PlaylistLibraryAdapter extends RecyclerView.Adapter<PlaylistLibraryAdapter.ViewHolder> {

    private Context context;
    private List<Playlist> playlists;
    private OnPlaylistClickListener listener;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
    }

    public PlaylistLibraryAdapter(Context context, List<Playlist> playlists, OnPlaylistClickListener listener) {
        this.context = context;
        this.playlists = playlists;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaylistLibraryBinding binding = ItemPlaylistLibraryBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.binding.tvPlaylistName.setText(playlist.getName());
        int count = playlist.getSongs() != null ? playlist.getSongs().size() : 0;
        holder.binding.tvPlaylistInfo.setText("Playlist • " + count + " bài hát");

        String coverUrl = RetrofitClient.getFullUrl(playlist.getCover_url());
        if (coverUrl != null) {
            com.bumptech.glide.Glide.with(context)
                    .load(coverUrl)
                    .placeholder(R.drawable.ic_music_note)
                    .into(holder.binding.ivPlaylistImg);
            holder.binding.ivPlaylistImg.setPadding(0, 0, 0, 0);
        } else {
            holder.binding.ivPlaylistImg.setImageResource(R.drawable.ic_music_note);
            holder.binding.ivPlaylistImg.setBackgroundResource(R.color.S20);
            holder.binding.ivPlaylistImg.setPadding(16, 16, 16, 16);
        }

        holder.itemView.setOnClickListener(v -> listener.onPlaylistClick(playlist));
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public void updateList(List<Playlist> newList) {
        this.playlists = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemPlaylistLibraryBinding binding;

        public ViewHolder(ItemPlaylistLibraryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
