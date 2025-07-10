package com.prm.flightbooking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.AuthApiEndpoint;
import com.prm.flightbooking.api.RetrofitClient;
import com.prm.flightbooking.dto.user.ForgotPasswordRequestDto;
import com.prm.flightbooking.fragments.OtpVerificationDialogFragment;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private Button btnSendResetLink;
    private ImageButton btnBack;
    private TextView tvBackToLogin;

    private AuthApiEndpoint authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authApi = ApiServiceProvider.getAuthApi();

        bindingView();
        bindingAction();
    }

    private void bindingView() {
        etEmail = findViewById(R.id.et_email);
        btnSendResetLink = findViewById(R.id.btn_send_reset_link);
        btnBack = findViewById(R.id.btn_back);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);

        // TODO: Xử lý các CardView khác (SMS Reset, Security Questions, Contact Support)
        // Hiện tại chỉ tập trung vào luồng email
    }

    private void bindingAction() {
        btnSendResetLink.setOnClickListener(this::onBtnSendResetLinkClick);
        btnBack.setOnClickListener(this::onBtnBackClick);
        tvBackToLogin.setOnClickListener(this::onTvBackToLoginClick);
    }

    private void onBtnBackClick(View view) {
        finish(); // Quay lại màn hình trước đó (Login)
    }

    private void onTvBackToLoginClick(View view) {
        Intent intent = new Intent(ForgotPasswordActivity.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa các activity trên stack
        startActivity(intent);
        finish(); // Đóng ForgotPasswordActivity
    }

    private void onBtnSendResetLinkClick(View view) {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email của bạn");
            etEmail.requestFocus();
            return;
        }

        // Disable nút và hiển thị trạng thái đang gửi
        btnSendResetLink.setEnabled(false);
        btnSendResetLink.setText("Đang gửi...");

        ForgotPasswordRequestDto requestDto = new ForgotPasswordRequestDto(email);

        authApi.forgotPassword(requestDto).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                // Re-enable nút và khôi phục text
                btnSendResetLink.setEnabled(true);
                btnSendResetLink.setText("GỬI MÃ XÁC THỰC"); // Hoặc "SEND RESET LINK"

                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().get("message");
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();

                    // Hiển thị dialog nhập OTP sau khi gửi thành công
                    showOtpVerificationDialog(email);

                } else {
                    String errorMessage = "Gửi yêu cầu thất bại. Vui lòng thử lại.";
                    try {
                        if (response.errorBody() != null) {
                            Map<String, String> errorMap = RetrofitClient.getGson().fromJson(response.errorBody().charStream(), Map.class);
                            if (errorMap != null && errorMap.containsKey("message")) {
                                errorMessage = errorMap.get("message");
                            }
                        }
                    } catch (Exception e) {
                        Log.e("ForgotPassword", "Error parsing error body: " + e.getMessage());
                    }
                    Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                // Re-enable nút và khôi phục text
                btnSendResetLink.setEnabled(true);
                btnSendResetLink.setText("GỬI MÃ XÁC THỰC"); // Hoặc "SEND RESET LINK"

                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ForgotPassword", "Network error: " + t.getMessage(), t);
            }
        });
    }

    // Phương thức hiển thị Dialog nhập OTP
    private void showOtpVerificationDialog(String email) {
        OtpVerificationDialogFragment dialogFragment = new OtpVerificationDialogFragment();
        Bundle args = new Bundle();
        args.putString("email", email);
        dialogFragment.setArguments(args);
        // Gán listener để nhận kết quả từ dialog
        dialogFragment.setOtpVerificationListener(new OtpVerificationDialogFragment.OtpVerificationListener() {
            @Override
            public void onOtpVerified(String email, String otpCode) {
                // OTP đã được xác thực thành công, chuyển sang màn hình đặt lại mật khẩu
                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("otpCode", otpCode); // Truyền OTP để tái sử dụng trong API reset password
                startActivity(intent);
                finish(); // Có thể đóng ForgotPasswordActivity hoặc giữ lại tùy luồng
            }

            @Override
            public void onOtpVerificationFailed() {
                // Người dùng nhập sai OTP hoặc OTP hết hạn
                Toast.makeText(ForgotPasswordActivity.this, "Xác thực OTP thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResendOtpRequested(String email) {
                // Xử lý gửi lại OTP nếu người dùng yêu cầu từ dialog
                onBtnSendResetLinkClick(null); // Gọi lại hàm gửi OTP
            }
        });
        dialogFragment.show(getSupportFragmentManager(), "otp_verification_dialog");
    }
}
