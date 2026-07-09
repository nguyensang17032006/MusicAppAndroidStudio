package com.example.musicappdemo.data;

import com.example.musicappdemo.model.Artist;
import com.example.musicappdemo.model.Genre;
import com.example.musicappdemo.model.Song;
import com.example.musicappdemo.model.auth.AuthRequest;
import com.example.musicappdemo.model.auth.AuthResponse;
import com.example.musicappdemo.model.auth.RegisterOtpRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("api/songs")
    Call<SimpleResponse<List<Song>>> getSongs();

    @GET("api/genres")
    Call<SimpleResponse<List<Genre>>> getGenres();

    @GET("api/artists")
    Call<SimpleResponse<List<Artist>>> getArtists();


    // 1. API gửi mã OTP về email (Dùng chung cho đăng ký/đăng nhập OTP)
    // Đường dẫn đầy đủ sẽ là: BASE_URL + api/auth/send-otp
    @POST("api/auth/send-otp")
    Call<SimpleResponse<Void>> sendOtp(@Body AuthRequest request);

    // 2. API xác thực mã OTP + đặt mật khẩu + tạo user trong MySQL
    @POST("api/auth/verify-and-register")
    Call<AuthResponse> verifyAndRegister(@Body RegisterOtpRequest request);

    // 3. API đăng nhập truyền thống bằng Email & Mật khẩu
    @POST("api/auth/login")
    Call<AuthResponse> loginUser(@Body AuthRequest request);
}
