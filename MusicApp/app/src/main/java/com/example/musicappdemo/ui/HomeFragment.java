package com.example.musicappdemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicappdemo.adapter.FeaturedSongAdapter;
import com.example.musicappdemo.adapter.SongAdapter;
import com.example.musicappdemo.databinding.FragmentHomeBinding;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.data.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setupRecyclerViews();
        fetchSongs();
        updateGreeting();

        binding.btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
        });

        return binding.getRoot();
    }

    private void updateGreeting() {
        String email = com.example.musicappdemo.data.SessionManager.get(getContext()).getEmail();
        if (email != null && !email.isEmpty()) {
            String name = email.split("@")[0];
            // Viết hoa chữ cái đầu cho đẹp
            if (name.length() > 0) {
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
            }
            binding.tvGreeting.setText("Chào bạn, " + name + "!");
        }

        String avatarUri = com.example.musicappdemo.data.SessionManager.get(getContext()).getAvatarUri();
        if (avatarUri != null && !avatarUri.isEmpty()) {
            com.bumptech.glide.Glide.with(this)
                    .load(com.example.musicappdemo.data.RetrofitClient.getFullUrl(avatarUri))
                    .placeholder(com.example.musicappdemo.R.drawable.ic_user)
                    .into(binding.btnProfile);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateGreeting();
    }

    private void setupRecyclerViews() {
        binding.rvFeaturedSongs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) return;

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void fetchSongs() {
        RetrofitClient.getApiService().getSongs().enqueue(new Callback<SimpleResponse<List<Song>>>() {
            @Override
            public void onResponse(Call<SimpleResponse<List<Song>>> call, Response<SimpleResponse<List<Song>>> response) {
                if (binding == null || getContext() == null) return;
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Song> allSongs = response.body().getData();

                    if (allSongs != null) {
                        com.example.musicappdemo.utils.MusicManager.getInstance().setAllSongs(allSongs);
                        // 1. Bài nhạc mới nhất (Giới hạn 5 bài)
                        List<Song> latestSongs = new ArrayList<>(allSongs);
                        // Giả sử bài mới nhất nằm ở cuối list hoặc có thuộc tính ngày tháng, ở đây ta lấy 5 bài đầu/cuối
                        // Nếu list trả về là mới nhất trước thì lấy 5 bài đầu
                        if (latestSongs.size() > 5) {
                            latestSongs = latestSongs.subList(0, 5);
                        }
                        SongAdapter latestAdapter = new SongAdapter(getContext(), latestSongs);
                        binding.lvLatestLessons.setAdapter(latestAdapter);
                        setListViewHeightBasedOnChildren(binding.lvLatestLessons);

                        // 2. Bài nhạc nghe nhiều (Sắp xếp theo views và lấy 10 bài)
                        List<Song> featuredSongs = new ArrayList<>(allSongs);
                        Collections.sort(featuredSongs, (s1, s2) -> Integer.compare(s2.getViews(), s1.getViews()));
                        if (featuredSongs.size() > 10) {
                            featuredSongs = featuredSongs.subList(0, 10);
                        }
                        FeaturedSongAdapter featuredAdapter = new FeaturedSongAdapter(getContext(), featuredSongs);
                        binding.rvFeaturedSongs.setAdapter(featuredAdapter);
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse<List<Song>>> call, Throwable t) {
                if (binding == null || getContext() == null) return;
                Log.e("HomeFragment", "Lỗi API: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
