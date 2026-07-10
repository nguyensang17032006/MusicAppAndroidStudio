package com.example.musicappdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.R;
import com.example.musicappdemo.databinding.ItemFeaturedProgramBinding;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.utils.LibraryManager;
import com.example.musicappdemo.utils.MusicManager;
import com.example.musicappdemo.utils.PlaylistDialogHelper;

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
        String artistName = (song.getArtists() != null && !song.getArtists().isEmpty()) ? song.getArtists().get(0).getName() : "Unknown Artist";

        holder.binding.tvTitle.setText(song.getTitle());
        holder.binding.tvSubtitle.setText(artistName);

        if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
            Glide.with(context).load(song.getCover_url()).into(holder.binding.imgArtwork);
        } else {
            holder.binding.imgArtwork.setImageResource(R.drawable.placeholder_img);
        }

        holder.itemView.setOnClickListener(v -> {
            MusicManager.getInstance().playSong(context, song);
        });

        // Update Heart UI
        if (LibraryManager.getInstance(context).isLiked(song.getId())) {
            holder.binding.btnHeart.setImageResource(R.drawable.ic_heart);
            holder.binding.btnHeart.setColorFilter(context.getResources().getColor(R.color.P60));
        } else {
            holder.binding.btnHeart.setImageResource(R.drawable.ic_heart);
            holder.binding.btnHeart.setColorFilter(context.getResources().getColor(R.color.white));
        }

        holder.binding.btnHeart.setOnClickListener(v -> {
            if (LibraryManager.getInstance(context).isLiked(song.getId())) {
                LibraryManager.getInstance(context).removeLikedSong(song.getId());
                Toast.makeText(context, "Đã bỏ thích", Toast.LENGTH_SHORT).show();
            } else {
                LibraryManager.getInstance(context).addLikedSong(song);
                Toast.makeText(context, "Đã thêm vào bài hát yêu thích", Toast.LENGTH_SHORT).show();
            }
            notifyItemChanged(position);
        });

        holder.binding.btnMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.binding.btnMore);
            popupMenu.getMenu().add("Thêm vào danh sách phát");
            popupMenu.getMenu().add("Thêm vào playlist");
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Thêm vào danh sách phát")) {
                    MusicManager.getInstance().addSongToPlaylist(context, song);
                    Toast.makeText(context, "Đã thêm vào danh sách phát", Toast.LENGTH_SHORT).show();
                } else if (item.getTitle().equals("Thêm vào playlist")) {
                    PlaylistDialogHelper.showAddToPlaylistDialog(context, song);
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
        ItemFeaturedProgramBinding binding;

        public ViewHolder(ItemFeaturedProgramBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
