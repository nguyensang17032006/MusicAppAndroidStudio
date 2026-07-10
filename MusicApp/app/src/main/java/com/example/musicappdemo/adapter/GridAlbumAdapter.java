package com.example.musicappdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.R;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.utils.LibraryManager;
import com.example.musicappdemo.utils.MusicManager;
import com.example.musicappdemo.utils.PlaylistDialogHelper;

import java.util.List;

public class GridAlbumAdapter extends BaseAdapter {
    private Context context;
    private List<Song> songList;

    public GridAlbumAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    @Override
    public int getCount() { return songList.size(); }

    @Override
    public Object getItem(int position) { return songList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_featured_program, parent, false);
        }

        Song song = songList.get(position);
        String artistName = (song.getArtists() != null && !song.getArtists().isEmpty()) ? song.getArtists().get(0).getName() : "Unknown Artist";

        ImageView img = convertView.findViewById(R.id.img_artwork);
        TextView title = convertView.findViewById(R.id.tv_title);
        TextView artist = convertView.findViewById(R.id.tv_subtitle);
        ImageView btnHeart = convertView.findViewById(R.id.btnHeart);
        ImageView btnMore = convertView.findViewById(R.id.btnMore);

        title.setText(song.getTitle());
        artist.setText(artistName);

        if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
            Glide.with(context).load(song.getCover_url()).into(img);
        } else {
            img.setImageResource(R.drawable.placeholder_img);
        }

        // Update Heart UI
        if (LibraryManager.getInstance(context).isLiked(song.getId())) {
            btnHeart.setImageResource(R.drawable.ic_heart);
            btnHeart.setColorFilter(ContextCompat.getColor(context, R.color.P60));
        } else {
            btnHeart.setImageResource(R.drawable.ic_heart);
            btnHeart.setColorFilter(ContextCompat.getColor(context, R.color.white));
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

        convertView.setOnClickListener(v -> {
            MusicManager.getInstance().playSong(context, song);
        });

        return convertView;
    }
}
