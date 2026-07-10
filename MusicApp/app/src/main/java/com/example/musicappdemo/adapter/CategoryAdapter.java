package com.example.musicappdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappdemo.R;
import com.example.musicappdemo.model.Genre;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context context;
    private List<Genre> genres;
    private OnItemClickListener listener;
    private String[] colors = {"#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#00BCD4", "#009688", "#4CAF50", "#FFC107", "#FF9800", "#FF5722"};

    public interface OnItemClickListener {
        void onItemClick(Genre genre, int position);
    }

    public CategoryAdapter(Context context, List<Genre> genres, OnItemClickListener listener) {
        this.context = context;
        this.genres = genres;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.name.setText(genre.getName());
        holder.card.setCardBackgroundColor(Color.parseColor(colors[position % colors.length]));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(genre, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardCategory);
            name = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
