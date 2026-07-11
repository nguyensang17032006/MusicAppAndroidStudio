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
                ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Friend ID", userId);
                clipboard.setPrimaryClip(clip);
                
                Toast.makeText(requireContext(), "Đã sao chép ID của bạn!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Lỗi: Không tìm thấy ID người dùng.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnAddFriend.setOnClickListener(v -> {
            String friendId = binding.etFriendId.getText().toString().trim();
            if (friendId.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập ID bạn bè", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String myUserId = SessionManager.get(requireContext()).getUserId();
            if (myUserId == null || myUserId.isEmpty()) return;

            RetrofitClient.getApiService().getUserProfile(friendId).enqueue(new Callback<SimpleResponse<com.example.musicappdemo.model.auth.SupabaseUser>>() {
                @Override
                public void onResponse(Call<SimpleResponse<com.example.musicappdemo.model.auth.SupabaseUser>> call, Response<SimpleResponse<com.example.musicappdemo.model.auth.SupabaseUser>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        String email = response.body().getData().getEmail();
                        
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Tìm thấy bạn bè")
                            .setMessage("Bạn có muốn kết bạn với người dùng: " + email + "?")
                            .setPositiveButton("Kết bạn", (dialog, which) -> {
                                java.util.Map<String, String> body = new java.util.HashMap<>();
                                body.put("inviterId", friendId);
                                body.put("receiverId", myUserId);

                                RetrofitClient.getApiService().acceptFriendViaLink(body).enqueue(new Callback<SimpleResponse<Void>>() {
                                    @Override
                                    public void onResponse(Call<SimpleResponse<Void>> call, Response<SimpleResponse<Void>> response2) {
                                        if (response2.isSuccessful() && response2.body() != null && response2.body().isSuccess()) {
                                            Toast.makeText(requireContext(), "Kết bạn thành công!", Toast.LENGTH_SHORT).show();
                                            binding.etFriendId.setText("");
                                            loadFriends();
                                        } else {
                                            Toast.makeText(requireContext(), "Lỗi: ID không hợp lệ hoặc đã là bạn bè.", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<SimpleResponse<Void>> call, Throwable t) {
                                        Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
                            .setNegativeButton("Hủy", null)
                            .show();
                    } else {
                        Toast.makeText(requireContext(), "Không tìm thấy người dùng với ID này.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SimpleResponse<com.example.musicappdemo.model.auth.SupabaseUser>> call, Throwable t) {
                    Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
