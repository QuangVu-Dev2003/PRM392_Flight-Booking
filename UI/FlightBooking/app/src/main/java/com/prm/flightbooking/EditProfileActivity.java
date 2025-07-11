package com.prm.flightbooking;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.AuthApiEndpoint;
import com.prm.flightbooking.dto.user.DeleteAccountDto;
import com.prm.flightbooking.dto.user.UpdateProfileDto;
import com.prm.flightbooking.dto.user.UserProfileDto;

import java.util.Calendar;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextInputEditText etFirstName, etLastName, etEmail, etPhone;
    private TextView tvDateOfBirth;
    private Button btnSave;
    private LinearLayout btnDatePicker, btnChangePassword, btnDeleteAccount;
    private Switch switchEmailNotifications, switchPushNotifications, switchSmsNotifications;
    private ImageView ivProfilePhoto;
    private Spinner spinnerGender;
    private AuthApiEndpoint authApi;
    private SharedPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize API service and SharedPreferences
        authApi = ApiServiceProvider.getAuthApi();
        userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Check login status
        if (!isLoggedIn()) {
            redirectToLogin();
            return;
        }

        // Bind views and actions
        bindingView();
        bindingAction();

        // Load current profile data
        loadProfileData();
    }

    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        tvDateOfBirth = findViewById(R.id.tv_date_of_birth);
        btnSave = findViewById(R.id.btn_save);
        btnDatePicker = findViewById(R.id.btn_date_picker);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnDeleteAccount = findViewById(R.id.btn_delete_account);
        switchEmailNotifications = findViewById(R.id.switch_email_notifications);
        switchPushNotifications = findViewById(R.id.switch_push_notifications);
        switchSmsNotifications = findViewById(R.id.switch_sms_notifications);
        ivProfilePhoto = findViewById(R.id.iv_profile_photo);
        spinnerGender = findViewById(R.id.spinner_gender);

        // Make email field read-only
        etEmail.setEnabled(false);

        // Setup gender spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Chọn giới tính", "Nam", "Nữ"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(this::onBackClick);
        btnSave.setOnClickListener(this::onSaveClick);
        btnDatePicker.setOnClickListener(this::onDatePickerClick);
        btnChangePassword.setOnClickListener(this::onChangePasswordClick);
        btnDeleteAccount.setOnClickListener(this::onDeleteAccountClick);

        // Notification switches
        switchEmailNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Email notifications: Coming soon", Toast.LENGTH_SHORT).show());
        switchPushNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "Push notifications: Coming soon", Toast.LENGTH_SHORT).show());
        switchSmsNotifications.setOnCheckedChangeListener((buttonView, isChecked) ->
                Toast.makeText(this, "SMS notifications: Coming soon", Toast.LENGTH_SHORT).show());
    }

    private void onBackClick(View view) {
        finish();
    }

    private void onSaveClick(View view) {
        performUpdateProfile();
    }

    private void onDatePickerClick(View view) {
        showDatePickerDialog();
    }

    private void onChangePasswordClick(View view) {
        navigateToActivity(ChangePasswordActivity.class);
    }

    private void onDeleteAccountClick(View view) {
        showDeleteAccountDialog();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    tvDateOfBirth.setText(selectedDate);
                },
                year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa tài khoản")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản? Hành động này sẽ vô hiệu hóa tài khoản của bạn.")
                .setPositiveButton("Xóa", (dialog, which) -> showPasswordInputDialog())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showPasswordInputDialog() {
        // Create a custom dialog for password input
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận mật khẩu");

        // Create a TextInputLayout to hold the password input
        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setPadding(16, 16, 16, 16);
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        textInputLayout.setBoxBackgroundColor(getResources().getColor(android.R.color.transparent));
        textInputLayout.setHint("Nhập mật khẩu");

        TextInputEditText passwordInput = new TextInputEditText(this);
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        textInputLayout.addView(passwordInput);

        builder.setView(textInputLayout);
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            String password = passwordInput.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                textInputLayout.setError("Vui lòng nhập mật khẩu");
            } else {
                performDeleteAccount(password);
            }
        });
        builder.setNegativeButton("Hủy", null);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Enable positive button only when password is not empty
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        passwordInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                textInputLayout.setError(null);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() > 0);
            }
        });
    }

    private void performDeleteAccount(String password) {
        int userId = userPrefs.getInt("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "Lỗi: ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        DeleteAccountDto deleteDto = new DeleteAccountDto(password);
        Call<Map<String, String>> call = authApi.deleteAccount(userId, deleteDto);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> result = response.body();
                    Toast.makeText(EditProfileActivity.this, result.get("message"), Toast.LENGTH_LONG).show();
                    if (result.get("message").equals("Account deleted successfully")) {
                        userPrefs.edit().clear().apply();
                        redirectToLogin();
                    }
                } else {
                    String errorMessage = "Lỗi khi xóa tài khoản";
                    try {
                        if (response.code() == 401) {
                            errorMessage = "Mật khẩu không đúng";
                        } else if (response.code() == 404) {
                            errorMessage = "Không tìm thấy người dùng";
                            redirectToLogin();
                        } else if (response.code() == 400) {
                            errorMessage = response.errorBody().string();
                            if (errorMessage.contains("active bookings")) {
                                errorMessage = "Không thể xóa tài khoản do có vé đang hoạt động";
                            }
                        } else if (response.code() >= 500) {
                            errorMessage = "Lỗi máy chủ, vui lòng thử lại sau";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadProfileData() {
        int userId = userPrefs.getInt("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "Lỗi: ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        Call<UserProfileDto> call = authApi.getProfile(userId);
        call.enqueue(new Callback<UserProfileDto>() {
            @Override
            public void onResponse(Call<UserProfileDto> call, Response<UserProfileDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateProfileUI(response.body());
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<UserProfileDto> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfileUI(UserProfileDto user) {
        String fullName = user.getFullName() != null ? user.getFullName() : "";
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        etFirstName.setText(firstName);
        etLastName.setText(lastName);
        etEmail.setText(user.getEmail() != null ? user.getEmail() : "");
        etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
        tvDateOfBirth.setText(user.getDateOfBirth() != null && !user.getDateOfBirth().isEmpty() ? user.getDateOfBirth() : "Chọn ngày sinh");

        String gender = translateGender(user.getGender());
        int genderPosition = 0;
        if (gender.equals("Nam")) {
            genderPosition = 1;
        } else if (gender.equals("Nữ")) {
            genderPosition = 2;
        }
        spinnerGender.setSelection(genderPosition);

        // Load notification preferences (set to false since not implemented)
        switchEmailNotifications.setChecked(false);
        switchPushNotifications.setChecked(false);
        switchSmsNotifications.setChecked(false);
    }

    private String translateGender(String gender) {
        if (gender == null || gender.isEmpty()) return "Chọn giới tính";
        switch (gender.toLowerCase()) {
            case "male":
            case "nam":
                return "Nam";
            case "female":
            case "nữ":
                return "Nữ";
            default:
                return "Chọn giới tính";
        }
    }

    private void performUpdateProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String fullName = firstName + " " + lastName;
        String phone = etPhone.getText().toString().trim();
        String dateOfBirth = tvDateOfBirth.getText().toString();
        String gender = spinnerGender.getSelectedItem().toString();
        int userId = userPrefs.getInt("user_id", -1);

        // Input validation
        if (userId <= 0) {
            Toast.makeText(this, "Lỗi: ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("Vui lòng nhập họ");
            etFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Vui lòng nhập tên");
            etLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Vui lòng nhập số điện thoại");
            etPhone.requestFocus();
            return;
        }

        if (dateOfBirth.equals("Chọn ngày sinh") || !dateOfBirth.matches("\\d{4}-\\d{2}-\\d{2}")) {
            Toast.makeText(this, "Vui lòng chọn ngày sinh hợp lệ (yyyy-MM-dd)", Toast.LENGTH_SHORT).show();
            btnDatePicker.requestFocus();
            return;
        }

        if (gender.equals("Chọn giới tính")) {
            Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
            spinnerGender.requestFocus();
            return;
        }

        // Disable save button to prevent multiple clicks
        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        UpdateProfileDto updateProfileDto = new UpdateProfileDto(fullName, phone, dateOfBirth, gender);
        Call<UserProfileDto> call = authApi.updateProfile(userId, updateProfileDto);
        call.enqueue(new Callback<UserProfileDto>() {
            @Override
            public void onResponse(Call<UserProfileDto> call, Response<UserProfileDto> response) {
                btnSave.setEnabled(true);
                btnSave.setText("LƯU");

                if (response.isSuccessful() && response.body() != null) {
                    UserProfileDto updatedUser = response.body();
                    saveUserInfo(updatedUser);
                    Toast.makeText(EditProfileActivity.this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<UserProfileDto> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("LƯU");
                Toast.makeText(EditProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserInfo(UserProfileDto user) {
        int userIdToSave = user.getUserId();
        if (userIdToSave <= 0) {
            Toast.makeText(this, "Không thể lưu thông tin: ID người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        userPrefs.edit()
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

    private void handleErrorResponse(Response<?> response) {
        String errorMessage = "Không thể cập nhật hồ sơ";
        if (response.code() == 401) {
            errorMessage = "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.";
            performLogout();
        } else if (response.code() == 400) {
            errorMessage = "Thông tin hồ sơ không hợp lệ";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi máy chủ, vui lòng thử lại sau";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void performLogout() {
        userPrefs.edit().clear().apply();
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }

    private boolean isLoggedIn() {
        return userPrefs.getBoolean("is_logged_in", false);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isLoggedIn()) {
            redirectToLogin();
        } else {
            loadProfileData();
        }
    }
}