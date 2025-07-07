package com.prm.flightbooking.api;

import com.prm.flightbooking.dto.user.AdminUserResponseDto;
import com.prm.flightbooking.dto.user.UpdateUserStatusDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface UserApiEndpoint {
    @GET("admin/Users")
    Call<List<AdminUserResponseDto>> getAllUsers(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("admin/Users/{userId}")
    Call<AdminUserResponseDto> getUserById(@Path("userId") int userId);

    @PUT("admin/Users/{userId}/status")
    Call<AdminUserResponseDto> updateUserStatus(@Path("userId") int userId, @Body UpdateUserStatusDto statusDto);
}
