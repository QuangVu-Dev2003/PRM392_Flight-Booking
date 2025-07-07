package com.prm.flightbooking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.AuthApiEndpoint;
import com.prm.flightbooking.dto.user.RegisterUserDto;
import com.prm.flightbooking.dto.user.UserProfileDto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Signup extends AppCompatActivity {

    private TextInputEditText etUsername, etFirstName, etLastName, etEmail, etPhone, etPassword, etConfirmPassword, etDateOfBirth;
    private TextInputLayout tilDateOfBirth;
    private TextView tvGenderMale, tvGenderFemale, tvLogin;
    private Button btnSignup;
    private ImageButton btnBack;
    private CheckBox cbTerms;
    private AuthApiEndpoint authApi;
    private String selectedGender;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Khởi tạo API service
        authApi = ApiServiceProvider.getAuthApi();

        // Ánh xạ views và thiết lập sự kiện
        bindingView();
        bindingAction();
    }

    private void bindingView() {
        etUsername = findViewById(R.id.et_username);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etDateOfBirth = findViewById(R.id.et_date_of_birth);
        tilDateOfBirth = findViewById(R.id.til_date_of_birth);
        tvGenderMale = findViewById(R.id.tv_gender_male);
        tvGenderFemale = findViewById(R.id.tv_gender_female);
        btnSignup = findViewById(R.id.btn_signup);
        btnBack = findViewById(R.id.btn_back);
        cbTerms = findViewById(R.id.cb_terms);
        tvLogin = findViewById(R.id.tv_login);
    }

    private void bindingAction() {
        btnSignup.setOnClickListener(v -> performSignup());
        btnBack.setOnClickListener(v -> finish());
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Signup.this, Login.class);
            startActivity(intent);
            finish();
        });

        // Date picker for date of birth
        etDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        // Gender selection
        // Set default selection (optional)
        selectedGender = "Male";
        tvGenderMale.setSelected(true);
        tvGenderFemale.setSelected(false);

        tvGenderMale.setOnClickListener(v -> {
            selectedGender = "Male"; // Adjust to "M" if server requires
            tvGenderMale.setSelected(true);
            tvGenderFemale.setSelected(false);
        });

        tvGenderFemale.setOnClickListener(v -> {
            selectedGender = "Female"; // Adjust to "F" if server requires
            tvGenderFemale.setSelected(true);
            tvGenderMale.setSelected(false);
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etDateOfBirth.setText(sdf.format(selectedDate.getTime()));
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void performSignup() {
        String username = etUsername.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String fullName = firstName + " " + lastName;
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Vui lòng nhập tên người dùng");
            etUsername.requestFocus();
            return;
        }

        if (username.length() < 3 || !username.matches("[a-zA-Z0-9]+")) {
            etUsername.setError("Tên người dùng phải có ít nhất 3 ký tự và chỉ chứa chữ cái hoặc số");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("Vui lòng nhập tên");
            etFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Vui lòng nhập họ");
            etLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return;
        }

        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Số điện thoại không hợp lệ");
            etPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(dateOfBirth)) {
            etDateOfBirth.setError("Vui lòng chọn ngày sinh");
            etDateOfBirth.requestFocus();
            return;
        }

        if (selectedDate != null) {
            Calendar today = Calendar.getInstance();
            today.add(Calendar.YEAR, -18);
            if (selectedDate.after(today)) {
                etDateOfBirth.setError("Bạn phải ít nhất 18 tuổi");
                etDateOfBirth.requestFocus();
                return;
            }
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 8 || !password.matches(".*[A-Z].*") ||
                !password.matches(".*[a-z].*") || !password.matches(".*[0-9].*")) {
            etPassword.setError("Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(selectedGender)) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với Điều khoản & Điều kiện", Toast.LENGTH_SHORT).show();
            cbTerms.requestFocus();
            return;
        }

        // Normalize phone number (example for Vietnam)
        if (!phone.startsWith("+")) {
            phone = "+84" + phone.replaceFirst("^0", "");
        }

        // Disable button to prevent multiple clicks
        btnSignup.setEnabled(false);
        btnSignup.setText("Đang tạo tài khoản...");

        // Create RegisterUserDto
        RegisterUserDto registerDto = new RegisterUserDto(username, email, password, fullName, phone, dateOfBirth, selectedGender);

        // Call API
        Call<UserProfileDto> call = authApi.register(registerDto);
        call.enqueue(new Callback<UserProfileDto>() {
            @Override
            public void onResponse(Call<UserProfileDto> call, Response<UserProfileDto> response) {
                // Re-enable button
                btnSignup.setEnabled(true);
                btnSignup.setText("CREATE ACCOUNT");

                if (response.isSuccessful() && response.body() != null) {
                    UserProfileDto user = response.body();

                    // Check for null fullName
                    String welcomeMessage = "Đăng ký thành công!";
                    if (user.getFullName() != null && !user.getFullName().isEmpty()) {
                        welcomeMessage += " Chào mừng " + user.getFullName();
                    }

                    Toast.makeText(Signup.this, welcomeMessage, Toast.LENGTH_SHORT).show();

                    saveUserInfo(user);

                    Intent intent = new Intent(Signup.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle server error
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<UserProfileDto> call, Throwable t) {
                // Re-enable button
                btnSignup.setEnabled(true);
                btnSignup.setText("CREATE ACCOUNT");

                // Handle network error
                Toast.makeText(Signup.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleErrorResponse(Response<UserProfileDto> response) {
        try {
            String errorMessage = "Đăng ký thất bại";
            if (response.code() == 400) {
                errorMessage = "Thông tin đăng ký không hợp lệ";
                if (response.errorBody() != null) {
                    String errorBody = response.errorBody().string();
                    Log.e("SignupError", "Error Body: " + errorBody);
                    // Optionally parse errorBody for specific messages
                    // e.g., errorMessage += ": " + parseErrorBody(errorBody);
                }
            } else if (response.code() == 409) {
                errorMessage = "Tên người dùng, email hoặc số điện thoại đã được sử dụng";
            } else if (response.code() >= 500) {
                errorMessage = "Lỗi server, vui lòng thử lại sau";
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Đã có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserInfo(UserProfileDto user) {
        // Save user info to SharedPreferences
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
