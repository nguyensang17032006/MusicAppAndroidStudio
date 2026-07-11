package com.example.musicappdemo.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicappdemo.adapter.FriendsAdapter;
import com.example.musicappdemo.data.ApiService;
import com.example.musicappdemo.data.RetrofitClient;
import com.example.musicappdemo.data.SessionManager;
import com.example.musicappdemo.data.SimpleResponse;
import com.example.musicappdemo.databinding.FragmentFriendBinding;
import com.example.musicappdemo.model.Friend;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendFragment extends Fragment {

    private FragmentFriendBinding binding;
    private FriendsAdapter friendsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFriendBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        loadFriends();

        binding.btnCopyLink.setOnClickListener(v -> {
            String userId = SessionManager.get(requireContext()).getUserId();
            if (userId != null && !userId.isEmpty()) {
                String link = "https://musicappdemo.com/friend?inviterId=" + userId;
                
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Friend Link", link);
                clipboard.setPrimaryClip(clip);
                
                Toast.makeText(requireContext(), "Đã sao chép link kết bạn!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Lỗi: Không tìm thấy ID người dùng.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        friendsAdapter = new FriendsAdapter(requireContext(), new ArrayList<>());
        binding.rvFriends.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFriends.setAdapter(friendsAdapter);
    }

    private void loadFriends() {
        String userId = SessionManager.get(requireContext()).getUserId();
        if (userId == null || userId.isEmpty()) return;

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);
        binding.rvFriends.setVisibility(View.GONE);

        RetrofitClient.getApiService().getFriendsList(userId).enqueue(new Callback<SimpleResponse<List<Friend>>>() {
            @Override
            public void onResponse(Call<SimpleResponse<List<Friend>>> call, Response<SimpleResponse<List<Friend>>> response) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Friend> friends = response.body().getData();
                    if (friends != null && !friends.isEmpty()) {
                        friendsAdapter.updateData(friends);
                        binding.rvFriends.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi tải danh sách bạn bè", Toast.LENGTH_SHORT).show();
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse<List<Friend>>> call, Throwable t) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                binding.tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
