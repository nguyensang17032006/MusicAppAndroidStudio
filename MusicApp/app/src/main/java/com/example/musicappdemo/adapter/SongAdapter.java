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
        ImageView imgCover = convertView.findViewById(R.id.logo); // Tạm dùng ID cũ từ fragment_home

        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist_names());
        
        if (song.getCover_url() != null && !song.getCover_url().isEmpty()) {
            Glide.with(context).load(song.getCover_url()).into(imgCover);
        } else {
            imgCover.setImageResource(R.drawable.placeholder_img);
        }

        convertView.setOnClickListener(v -> {
            MusicManager.getInstance().playSong(context, song);
        });

        return convertView;
    }
}
