package com.prm.flightbooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.AuthApiEndpoint;
import com.prm.flightbooking.api.RetrofitClient;
import com.prm.flightbooking.dto.user.ResetPasswordDto;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etNewPassword, etConfirmPassword;
    private Button btnResetPassword;
    private ImageButton btnBack;

    private String email;
    private String otpCode;
    private AuthApiEndpoint authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password); // Sẽ tạo file này sau

        authApi = ApiServiceProvider.getAuthApi();

        // Nhận dữ liệu email và otpCode từ Intent
        if (getIntent() != null) {
            email = getIntent().getStringExtra("email");
            otpCode = getIntent().getStringExtra("otpCode");
        }

        bindingView();
        bindingAction();
    }

    private void bindingView() {
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        btnBack = findViewById(R.id.btn_back);
    }

    private void bindingAction() {
        btnResetPassword.setOnClickListener(this::onBtnResetPasswordClick);
        btnBack.setOnClickListener(this::onBtnBackClick);
    }

    private void onBtnBackClick(View view) {
        finish(); // Quay lại màn hình trước đó
    }

    private void onBtnResetPasswordClick(View view) {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            etNewPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) { // Kiểm tra độ dài mật khẩu (theo server validation)
            etNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etNewPassword.requestFocus();
            return;
        }

        btnResetPassword.setEnabled(false);
        btnResetPassword.setText("Đang đặt lại...");

        ResetPasswordDto resetPasswordDto = new ResetPasswordDto(email, otpCode, newPassword);

        authApi.resetPassword(resetPasswordDto).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("ĐẶT LẠI MẬT KHẨU");

                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();

                    // Chuyển về màn hình đăng nhập sau khi đổi mật khẩu thành công
                    Intent intent = new Intent(ResetPasswordActivity.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa các activity trên stack
                    startActivity(intent);
                    finish(); // Đóng ResetPasswordActivity
                } else {
                    String errorMessage = "Đặt lại mật khẩu thất bại. Vui lòng thử lại.";
                    try {
                        if (response.errorBody() != null) {
                            Map<String, String> errorMap = RetrofitClient.getGson().fromJson(response.errorBody().charStream(), Map.class);
                            if (errorMap != null && errorMap.containsKey("message")) {
                                errorMessage = errorMap.get("message");
                            }
                        }
                    } catch (Exception e) {
                        Log.e("ResetPassword", "Error parsing error body: " + e.getMessage());
                    }
                    Toast.makeText(ResetPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                btnResetPassword.setEnabled(true);
                btnResetPassword.setText("ĐẶT LẠI MẬT KHẨU");
                Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ResetPassword", "Network error: " + t.getMessage(), t);
            }
        });
    }
}
