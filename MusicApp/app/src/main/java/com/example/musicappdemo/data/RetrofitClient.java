package com.example.musicappdemo.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // LƯU Ý VỀ IP:
    // - Dùng máy ảo Android Studio mặc định: "http://10.0.2.2:3000/"
    // - Dùng máy ảo Genymotion: "http://10.0.3.2:3000/"
    // - Dùng điện thoại thật: Thay bằng IP local của máy tính (Ví dụ: "http://192.168.1.5:3000/")
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()) // Tự động parse JSON qua Gson
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}