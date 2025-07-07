package com.prm.flightbooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.AuthApiEndpoint;
import com.prm.flightbooking.dto.user.LoginDto;
import com.prm.flightbooking.dto.user.UserProfileDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private TextInputEditText etUsernameOrEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    private AuthApiEndpoint authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo API service
        authApi = ApiServiceProvider.getAuthApi();

        // Ánh xạ views và thiết lập sự kiện
        bindingView();
        bindingAction();
    }

    private void bindingView() {
        etUsernameOrEmail = findViewById(R.id.et_username_or_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_sign_up);
    }

    private void bindingAction() {
        btnLogin.setOnClickListener(v -> performLogin());
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Signup.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String usernameOrEmail = etUsernameOrEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(usernameOrEmail)) {
            etUsernameOrEmail.setError("Vui lòng nhập email hoặc username");
            etUsernameOrEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // Disable button để tránh click nhiều lần
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        // Tạo LoginDto
        LoginDto loginDto = new LoginDto(usernameOrEmail, password);

        // Gọi API
        Call<UserProfileDto> call = authApi.login(loginDto);
        call.enqueue(new Callback<UserProfileDto>() {
            @Override
            public void onResponse(Call<UserProfileDto> call, Response<UserProfileDto> response) {
                // Enable lại button
                btnLogin.setEnabled(true);
                btnLogin.setText("START YOUR JOURNEY");

                if (response.isSuccessful() && response.body() != null) {
                    UserProfileDto user = response.body();

                    // Kiểm tra null cho fullName
                    String welcomeMessage = "Đăng nhập thành công!";
                    if (user.getFullName() != null && !user.getFullName().isEmpty()) {
                        welcomeMessage += " Chào mừng " + user.getFullName();
                    }

                    Toast.makeText(Login.this, welcomeMessage, Toast.LENGTH_SHORT).show();

                    saveUserInfo(user);

                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Xử lý lỗi từ server
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<UserProfileDto> call, Throwable t) {
                // Enable lại button
                btnLogin.setEnabled(true);
                btnLogin.setText("START YOUR JOURNEY");

                // Xử lý lỗi network
                Toast.makeText(Login.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleErrorResponse(Response<UserProfileDto> response) {
        try {
            String errorMessage = "Đăng nhập thất bại";

            if (response.code() == 401) {
                errorMessage = "Email/Username hoặc mật khẩu không đúng";
            } else if (response.code() == 400) {
                errorMessage = "Thông tin đăng nhập không hợp lệ";
            } else if (response.code() >= 500) {
                errorMessage = "Lỗi server, vui lòng thử lại sau";
            }

            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserInfo(UserProfileDto user) {
        // Lưu thông tin user vào SharedPreferences
        getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putInt("user_id", user.getUserId())
                .putString("username", user.getUsername())
                .putString("user_name", user.getFullName())
                .putString("user_email", user.getEmail())
                .putString("user_phone", user.getPhone())
                .putString("date_of_birth", user.getDateOfBirth())
                .putString("gender", user.getGender())
                .putInt("total_bookings", user.getTotalBookings())
                .putBoolean("is_logged_in", true)
                .apply();
    }
}