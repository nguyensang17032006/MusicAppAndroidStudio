package com.example.musicappdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.PlayerActivity;
import com.example.musicappdemo.R;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.utils.LibraryManager;
import com.example.musicappdemo.utils.MusicManager;
import com.example.musicappdemo.utils.PlaylistDialogHelper;

import java.util.List;

public class SongAdapter extends BaseAdapter {
    private Context context;
    private List<Song> songs;

    public SongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @Override
    public int getCount() { return songs.size(); }

    @Override
    public Object getItem(int position) { return songs.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lesson, parent, false);
        }

        Song song = songs.get(position);

        TextView tvTitle = convertView.findViewById(R.id.tvLessonTitle);
        TextView tvArtist = convertView.findViewById(R.id.tvLessonInfo);
        ImageView imgCover = convertView.findViewById(R.id.lessonImg);

        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist_names());

        ImageView btnHeart = convertView.findViewById(R.id.btnHeart);
        ImageView btnMore = convertView.findViewById(R.id.btnMore);

        // Update Heart UI
        if (LibraryManager.getInstance(context).isLiked(song.getId())) {
            btnHeart.setImageResource(R.drawable.ic_heart);
            btnHeart.setColorFilter(ContextCompat.getColor(context, R.color.P60));
        } else {
            btnHeart.setImageResource(R.drawable.ic_heart);
            btnHeart.setColorFilter(ContextCompat.getColor(context, R.color.S60));
        }

        btnHeart.setOnClickListener(v -> {
            if (LibraryManager.getInstance(context).isLiked(song.getId())) {
                LibraryManager.getInstance(context).removeLikedSong(song.getId());
                Toast.makeText(context, "Đã bỏ thích", Toast.LENGTH_SHORT).show();
            } else {
                LibraryManager.getInstance(context).addLikedSong(song);
                Toast.makeText(context, "Đã thêm vào bài hát yêu thích", Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged();
        });

        btnMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, btnMore);
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

        if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
            Glide.with(context).load(song.getCover_url()).into(imgCover);
        } else {
            imgCover.setImageResource(R.drawable.placeholder_img);
        }

        convertView.setOnClickListener(v -> {
            MusicManager.getInstance().playSong(context, song);
            Intent intent = new Intent(context, PlayerActivity.class);
            context.startActivity(intent);
        });

        return convertView;
    }
}
