package com.prm.flightbooking.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String ANDROID_STUDIO_BASE_URL = "http://10.0.2.2:5091/api/";
    private static final String REAL_IP_BASE_URL = "http://192.168.1.23:5091/api/"; // IP thật của bạn

    private static Retrofit retrofitInstance;

    public static Retrofit getInstance() {
        if (retrofitInstance == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();

            String baseUrl = getBaseUrl();

            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofitInstance;
    }

    private static String getBaseUrl() {
        // Thử detect emulator type hoặc cho phép config
        String emulator = android.os.Build.PRODUCT;
        if (emulator.contains("sdk") || emulator.contains("Andy")) {
            return ANDROID_STUDIO_BASE_URL;
        }
        return REAL_IP_BASE_URL;
    }
}
