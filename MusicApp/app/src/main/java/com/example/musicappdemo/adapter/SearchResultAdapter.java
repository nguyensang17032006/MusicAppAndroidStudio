package com.example.musicappdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.R;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.databinding.ItemMusicBinding;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.utils.LibraryManager;
import com.example.musicappdemo.utils.MusicManager;
import com.example.musicappdemo.utils.PlaylistDialogHelper;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private Context context;
    private List<Song> songList;
    private String currentPlaylistId;

    public SearchResultAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    public SearchResultAdapter(Context context, List<Song> songList, String playlistId) {
        this.context = context;
        this.songList = songList;
        this.currentPlaylistId = playlistId;
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

        String coverUrl = RetrofitClient.getFullUrl(song.getCover_url());
        if (coverUrl != null) {
            Glide.with(context).load(coverUrl).into(holder.binding.lessonImg);
        } else {
            holder.binding.lessonImg.setImageResource(R.drawable.placeholder_img);
        }

        holder.itemView.setOnClickListener(v -> {
            MusicManager.getInstance().playSong(context, song);
            Intent intent = new Intent(context, com.example.musicappdemo.PlayerActivity.class);
            context.startActivity(intent);
        });

        holder.binding.lessonActions.setVisibility(View.VISIBLE);
        updateHeartIcon(holder, song);

        holder.binding.btnHeart.setOnClickListener(v -> {
            if (LibraryManager.getInstance(context).isLiked(song.getId())) {
                LibraryManager.getInstance(context).removeLikedSong(song.getId());
                Toast.makeText(context, "Đã bỏ thích", Toast.LENGTH_SHORT).show();
            } else {
                LibraryManager.getInstance(context).addLikedSong(song);
                Toast.makeText(context, "Đã thêm vào bài hát yêu thích", Toast.LENGTH_SHORT).show();
            }
            updateHeartIcon(holder, song);
        });

        holder.binding.btnMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.binding.btnMore);
            
            // 1. Logic cho "Danh sách phát" (Queue)
            boolean isInQueue = false;
            for (Song s : MusicManager.getInstance().getPlaylist()) {
                if (s.getId().equals(song.getId())) {
                    isInQueue = true;
                    break;
                }
            }
            
            if (isInQueue) {
                popupMenu.getMenu().add("Xóa khỏi danh sách phát");
            } else {
                popupMenu.getMenu().add("Thêm vào danh sách phát");
            }

            // 2. Logic cho Playlist hiện tại (nếu đang ở màn hình playlist)
            if (currentPlaylistId != null) {
                popupMenu.getMenu().add("Xóa khỏi playlist này");
            } else {
                popupMenu.getMenu().add("Thêm vào playlist");
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();
                if (title.equals("Thêm vào danh sách phát")) {
                    MusicManager.getInstance().addSongToPlaylist(context, song);
                    Toast.makeText(context, "Đã thêm vào danh sách phát", Toast.LENGTH_SHORT).show();
                } else if (title.equals("Xóa khỏi danh sách phát")) {
                    MusicManager.getInstance().removeSongFromQueue(song.getId());
                    Toast.makeText(context, "Đã xóa khỏi danh sách phát", Toast.LENGTH_SHORT).show();
                } else if (title.equals("Thêm vào playlist")) {
                    showAddToPlaylistDialog(song);
                } else if (title.equals("Xóa khỏi playlist này")) {
                    LibraryManager.getInstance(context).removeSongFromPlaylist(currentPlaylistId, song.getId());
                }
                return true;
            });
            popupMenu.show();
        });
    }

    private void updateHeartIcon(ViewHolder holder, Song song) {
        if (LibraryManager.getInstance(context).isLiked(song.getId())) {
            holder.binding.btnHeart.setImageResource(R.drawable.ic_heart);
            holder.binding.btnHeart.setColorFilter(context.getResources().getColor(R.color.P60));
        } else {
            holder.binding.btnHeart.setImageResource(R.drawable.ic_heart);
            holder.binding.btnHeart.setColorFilter(context.getResources().getColor(R.color.S60));
        }
    }

    private void showAddToPlaylistDialog(Song song) {
        PlaylistDialogHelper.showAddToPlaylistDialog(context, song);
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
