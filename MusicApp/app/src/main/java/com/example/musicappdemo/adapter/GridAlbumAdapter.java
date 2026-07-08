package com.example.musicappdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicappdemo.R;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.utils.MusicManager;

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
        ImageView img = convertView.findViewById(R.id.img_artwork);
        TextView title = convertView.findViewById(R.id.tv_title);
        TextView artist = convertView.findViewById(R.id.tv_subtitle);

        title.setText(song.getTitle());
        artist.setText(song.getArtist_names());

        if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
            Glide.with(context).load(song.getCover_url()).into(img);
        } else {
            img.setImageResource(R.drawable.placeholder_img);
        }

        convertView.setOnClickListener(v -> {
            MusicManager.getInstance().playSong(context, song);
        });

        return convertView;
    }
}
