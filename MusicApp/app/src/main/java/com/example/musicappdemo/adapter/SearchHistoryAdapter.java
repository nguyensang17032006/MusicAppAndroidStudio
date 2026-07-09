package com.example.musicappdemo.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicappdemo.databinding.ItemSearchHistoryBinding;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

    private List<String> historyList;
    private OnHistoryClickListener listener;

    public interface OnHistoryClickListener {
        void onItemClick(String query);
        void onDeleteClick(int position);
    }

    public SearchHistoryAdapter(List<String> historyList, OnHistoryClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchHistoryBinding binding = ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String query = historyList.get(position);
        holder.binding.tvHistoryText.setText(query);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(query));
        holder.binding.btnDeleteHistory.setOnClickListener(v -> listener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemSearchHistoryBinding binding;

        public ViewHolder(ItemSearchHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
