package com.example.musicappdemo.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // LƯU Ý VỀ IP:
    // - Dùng máy ảo Android Studio mặc định: "http://10.0.2.2:5000/"
    // - Dùng máy ảo Genymotion: "http://10.0.3.2:5000/"
    // - Dùng máy ảo Android Studio mặc định: "http://10.0.2.2:3000/"
    // - Dùng VPS hiện tại của bạn:
    private static final String BASE_URL = "http://45.32.105.30:3000/";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}