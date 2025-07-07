package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.user.ChangePasswordDto;
import com.prm.flightbooking.dto.user.LoginDto;
import com.prm.flightbooking.dto.user.RegisterUserDto;
import com.prm.flightbooking.dto.user.UpdateProfileDto;
import com.prm.flightbooking.dto.user.UserProfileDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AuthApiEndpoint {
    @POST("Auth/register")
    Call<UserProfileDto> register(@Body RegisterUserDto registerDto);

    @POST("Auth/login")
    Call<UserProfileDto> login(@Body LoginDto loginDto);

    @GET("Auth/profile/{userId}")
    Call<UserProfileDto> getProfile(@Path("userId") int userId);

    @PUT("Auth/profile/{userId}")
    Call<UserProfileDto> updateProfile(@Path("userId") int userId, @Body UpdateProfileDto updateDto);

    @POST("Auth/change-password/{userId}")
    Call<Map<String, String>> changePassword(@Path("userId") int userId, @Body ChangePasswordDto passwordDto);
}
