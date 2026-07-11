package com.example.musicappdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappdemo.R;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.databinding.ItemPlaylistLibraryBinding;
import com.example.musicappdemo.model.Artist;
import com.example.musicappdemo.ui.ArtistDetailActivity;

import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private Context context;
    private List<Artist> artists;
    private OnArtistClickListener listener;

    public interface OnArtistClickListener {
        void onArtistClick(Artist artist);
    }

    public ArtistAdapter(Context context, List<Artist> artists) {
        this.context = context;
        this.artists = artists;
    }

    public ArtistAdapter(Context context, List<Artist> artists, OnArtistClickListener listener) {
        this.context = context;
        this.artists = artists;
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
        Artist artist = artists.get(position);
        holder.binding.tvPlaylistName.setText(artist.getName());
        holder.binding.tvPlaylistInfo.setText("Nghệ sĩ");

        String avatarUrl = RetrofitClient.getFullUrl(artist.getAvatar_url());
        if (avatarUrl != null) {
            com.bumptech.glide.Glide.with(context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_user)
                    .into(holder.binding.ivPlaylistImg);
            holder.binding.ivPlaylistImg.setPadding(0, 0, 0, 0);
        } else {
            holder.binding.ivPlaylistImg.setImageResource(R.drawable.ic_user);
            holder.binding.ivPlaylistImg.setBackgroundResource(R.color.S20);
            holder.binding.ivPlaylistImg.setPadding(16, 16, 16, 16);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onArtistClick(artist);
            } else {
                Intent intent = new Intent(context, ArtistDetailActivity.class);
                intent.putExtra("artist_id", artist.getId());
                intent.putExtra("artist_name", artist.getName());
                intent.putExtra("artist_avatar", artist.getAvatar_url());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemPlaylistLibraryBinding binding;

        public ViewHolder(ItemPlaylistLibraryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
