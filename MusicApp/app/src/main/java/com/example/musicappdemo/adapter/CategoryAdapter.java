package com.example.musicappdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.musicappdemo.R;
import com.example.musicappdemo.model.Genre;

import java.util.List;
import java.util.Random;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private List<Genre> genres;
    private String[] colors = {"#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#00BCD4", "#009688", "#4CAF50", "#FFC107", "#FF9800", "#FF5722"};

    public CategoryAdapter(Context context, List<Genre> genres) {
        this.context = context;
        this.genres = genres;
    }

    @Override
    public int getCount() { return genres.size(); }

    @Override
    public Object getItem(int position) { return genres.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        }

        Genre genre = genres.get(position);
        CardView card = convertView.findViewById(R.id.cardCategory);
        TextView name = convertView.findViewById(R.id.tvCategoryName);

        name.setText(genre.getName());
        card.setCardBackgroundColor(Color.parseColor(colors[position % colors.length]));

        return convertView;
    }
}
