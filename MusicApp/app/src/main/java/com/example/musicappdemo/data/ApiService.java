package com.example.musicappdemo.data;

import com.example.musicappdemo.model.Artist;
import com.example.musicappdemo.model.Genre;
import com.example.musicappdemo.model.Playlist;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.model.auth.AuthRequest;
import com.example.musicappdemo.model.auth.AuthResponse;
import com.example.musicappdemo.model.auth.NewPasswordRequest;
import com.example.musicappdemo.model.auth.RegisterOtpRequest;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/songs")
    Call<SimpleResponse<List<Song>>> getSongs();

    @POST("api/songs/{id}/view")
    Call<SimpleResponse<Void>> incrementView(@Path("id") String songId);

    @GET("api/genres")
    Call<SimpleResponse<List<Genre>>> getGenres();

    @GET("api/artists")
    Call<SimpleResponse<List<Artist>>> getArtists();


    // 1. API gửi mã OTP về email (Dùng chung cho đăng ký/đăng nhập OTP)
    // Đường dẫn đầy đủ sẽ là: BASE_URL + api/auth/send-otp
    @POST("api/auth/send-otp")
    Call<SimpleResponse> sendOtp(@Body AuthRequest request);

    // 2. API xác thực mã OTP + đặt mật khẩu + tạo user trong MySQL
    @POST("api/auth/verify-and-register")
    Call<AuthResponse> verifyAndRegister(@Body RegisterOtpRequest request);

    @POST("api/auth/forgot-password/send-otp")
    Call<SimpleResponse> sendOtpForgotPassword(@Body AuthRequest request);

    @POST("api/auth/forgot-password/verify")
    Call<AuthResponse> verifyOtpForgotPassword(@Body RegisterOtpRequest request);

    @POST("api/auth/forgot-password/newpassword")
    Call<SimpleResponse> updateNewPassword(@Body NewPasswordRequest request);


    // 3. API đăng nhập truyền thống bằng Email & Mật khẩu
    @POST("api/auth/login")
    Call<AuthResponse> loginUser(@Body AuthRequest request);

    // LIBRARY APIS
    @GET("api/library/liked-songs/{userId}")
    Call<SimpleResponse<List<Song>>> getLikedSongs(@Path("userId") String userId);

    @POST("api/library/liked-songs/toggle")
    Call<SimpleResponse<Void>> toggleLikeSong(@Body java.util.Map<String, String> body);

    @GET("api/library/playlists/{userId}")
    Call<SimpleResponse<List<Playlist>>> getUserPlaylists(@Path("userId") String userId);

    @POST("api/library/playlists/create")
    Call<SimpleResponse<Playlist>> createPlaylist(@Body java.util.Map<String, String> body);

    @POST("api/library/playlists/add-song")
    Call<SimpleResponse<Void>> addSongToPlaylist(@Body java.util.Map<String, String> body);

    @POST("api/library/playlists/remove-song")
    Call<SimpleResponse<Void>> removeSongFromPlaylist(@Body java.util.Map<String, String> body);

    @POST("api/library/playlists/delete")
    Call<SimpleResponse<Void>> deletePlaylist(@Body java.util.Map<String, String> body);

    @POST("api/library/playlists/update-cover")
    Call<SimpleResponse<Void>> updatePlaylistCover(@Body java.util.Map<String, String> body);

    @GET("api/library/followed-artists/{userId}")
    Call<SimpleResponse<List<Artist>>> getFollowedArtists(@Path("userId") String userId);

    @POST("api/library/followed-artists/toggle")
    Call<SimpleResponse<Void>> toggleFollowArtist(@Body java.util.Map<String, String> body);

    @GET("api/auth/me/{userId}")
    Call<SimpleResponse<com.example.musicappdemo.model.auth.SupabaseUser>> getUserProfile(@Path("userId") String userId);

    @POST("api/auth/update-profile")
    Call<SimpleResponse<Void>> updateProfile(@Body Map<String, String> body);

    @retrofit2.http.Multipart
    @POST("api/upload")
    Call<Map<String, Object>> uploadFile(@retrofit2.http.Part okhttp3.MultipartBody.Part file);
}
