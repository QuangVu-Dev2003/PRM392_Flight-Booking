package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.AuthApiEndpoint;
import com.prm.flightbooking.dto.user.ChangePasswordDto;

import java.util.Map;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageButton btnBack, btnHelp;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private TextInputLayout tilCurrentPassword, tilNewPassword, tilConfirmPassword;
    private Button btnChangePassword;
    private TextView tvPasswordStrength;
    private View strengthBar1, strengthBar2, strengthBar3, strengthBar4;
    private ImageView checkLength, checkUppercase, checkLowercase, checkNumber, checkSpecial;
    private AuthApiEndpoint authApi;
    private SharedPreferences userPrefs;

    // Password requirements patterns
    private static final Pattern LENGTH_PATTERN = Pattern.compile(".{8,}");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_PATTERN = Pattern.compile("[!@#$%^*]");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

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
    }

    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        btnHelp = findViewById(R.id.btn_help);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        tilCurrentPassword = findViewById(R.id.til_current_password);
        tilNewPassword = findViewById(R.id.til_new_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);
        tvPasswordStrength = findViewById(R.id.tv_password_strength);
        strengthBar1 = findViewById(R.id.strength_bar_1);
        strengthBar2 = findViewById(R.id.strength_bar_2);
        strengthBar3 = findViewById(R.id.strength_bar_3);
        strengthBar4 = findViewById(R.id.strength_bar_4);
        checkLength = findViewById(R.id.check_length);
        checkUppercase = findViewById(R.id.check_uppercase);
        checkLowercase = findViewById(R.id.check_lowercase);
        checkNumber = findViewById(R.id.check_number);
        checkSpecial = findViewById(R.id.check_special);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(v -> finish());
        btnHelp.setOnClickListener(v -> Toast.makeText(this, "Help: Coming soon", Toast.LENGTH_SHORT).show());
        btnChangePassword.setOnClickListener(v -> performChangePassword());

        // Real-time password strength validation
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validatePassword(s.toString());
                updateChangeButtonState();
            }
        });

        // Real-time confirm password validation
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validateConfirmPassword();
                updateChangeButtonState();
            }
        });

        etCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateChangeButtonState();
            }
        });
    }

    private void validatePassword(String password) {
        int strength = 0;
        boolean hasLength = LENGTH_PATTERN.matcher(password).matches();
        boolean hasUppercase = UPPERCASE_PATTERN.matcher(password).matches();
        boolean hasLowercase = LOWERCASE_PATTERN.matcher(password).matches();
        boolean hasNumber = NUMBER_PATTERN.matcher(password).matches();
        boolean hasSpecial = SPECIAL_PATTERN.matcher(password).matches();

        // Update requirement icons
        checkLength.setImageResource(hasLength ? R.drawable.ic_check_circle_filled : R.drawable.ic_check_circle_outline);
        checkUppercase.setImageResource(hasUppercase ? R.drawable.ic_check_circle_filled : R.drawable.ic_check_circle_outline);
        checkLowercase.setImageResource(hasLowercase ? R.drawable.ic_check_circle_filled : R.drawable.ic_check_circle_outline);
        checkNumber.setImageResource(hasNumber ? R.drawable.ic_check_circle_filled : R.drawable.ic_check_circle_outline);
        checkSpecial.setImageResource(hasSpecial ? R.drawable.ic_check_circle_filled : R.drawable.ic_check_circle_outline);

        // Calculate strength
        if (hasLength) strength++;
        if (hasUppercase) strength++;
        if (hasLowercase) strength++;
        if (hasNumber) strength++;
        if (hasSpecial) strength++;

        // Update strength bars and text
        updateStrengthBars(strength);
        updateStrengthText(strength);
    }

    private void updateStrengthBars(int strength) {
        int colorWeak = getResources().getColor(R.color.weak_password);
        int colorMedium = getResources().getColor(R.color.medium_password);
        int colorStrong = getResources().getColor(R.color.strong_password);
        int colorDefault = getResources().getColor(R.color.grey);

        strengthBar1.setBackgroundColor(strength >= 1 ? colorWeak : colorDefault);
        strengthBar2.setBackgroundColor(strength >= 2 ? colorMedium : colorDefault);
        strengthBar3.setBackgroundColor(strength >= 3 ? colorMedium : colorDefault);
        strengthBar4.setBackgroundColor(strength >= 4 ? colorStrong : colorDefault);
    }

    private void updateStrengthText(int strength) {
        if (strength == 0) {
            tvPasswordStrength.setText("Enter password to check strength");
            tvPasswordStrength.setTextColor(getResources().getColor(R.color.grey));
        } else if (strength <= 2) {
            tvPasswordStrength.setText("Weak");
            tvPasswordStrength.setTextColor(getResources().getColor(R.color.weak_password));
        } else if (strength <= 3) {
            tvPasswordStrength.setText("Medium");
            tvPasswordStrength.setTextColor(getResources().getColor(R.color.medium_password));
        } else {
            tvPasswordStrength.setText("Strong");
            tvPasswordStrength.setTextColor(getResources().getColor(R.color.strong_password));
        }
    }

    private void validateConfirmPassword() {
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (!confirmPassword.isEmpty() && !confirmPassword.equals(newPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
        } else {
            tilConfirmPassword.setError(null);
        }
    }

    private void updateChangeButtonState() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        boolean isValidPassword = LENGTH_PATTERN.matcher(newPassword).matches() &&
                UPPERCASE_PATTERN.matcher(newPassword).matches() &&
                LOWERCASE_PATTERN.matcher(newPassword).matches() &&
                NUMBER_PATTERN.matcher(newPassword).matches() &&
                SPECIAL_PATTERN.matcher(newPassword).matches();

        boolean isEnabled = !currentPassword.isEmpty() &&
                !newPassword.isEmpty() &&
                !confirmPassword.isEmpty() &&
                newPassword.equals(confirmPassword) &&
                isValidPassword;

        btnChangePassword.setEnabled(isEnabled);
        btnChangePassword.setAlpha(isEnabled ? 1.0f : 0.5f);
    }

    private void performChangePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        int userId = userPrefs.getInt("user_id", -1);

        // Input validation
        if (userId <= 0) {
            Toast.makeText(this, "Error: Invalid user ID", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        if (currentPassword.isEmpty()) {
            tilCurrentPassword.setError("Please enter current password");
            etCurrentPassword.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            tilNewPassword.setError("Please enter new password");
            etNewPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Please confirm new password");
            etConfirmPassword.requestFocus(); // Fixed typo here
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus(); // Fixed typo here
            return;
        }

        // Disable button to prevent multiple clicks
        btnChangePassword.setEnabled(false);
        btnChangePassword.setText("Updating...");

        ChangePasswordDto changePasswordDto = new ChangePasswordDto(currentPassword, newPassword);
        Call<Map<String, String>> call = authApi.changePassword(userId, changePasswordDto);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                btnChangePassword.setEnabled(true);
                btnChangePassword.setText("UPDATE PASSWORD");

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, String> result = response.body();
                    Toast.makeText(ChangePasswordActivity.this, result.get("message"), Toast.LENGTH_LONG).show();
                    if (result.get("message").equals("Password changed successfully")) {
                        performLogout();
                    }
                } else {
                    String errorMessage = "Failed to change password";
                    try {
                        if (response.code() == 401) {
                            errorMessage = "Incorrect current password";
                            tilCurrentPassword.setError(errorMessage);
                            etCurrentPassword.requestFocus();
                        } else if (response.code() == 400) {
                            errorMessage = response.errorBody().string();
                            tilNewPassword.setError(errorMessage);
                            etNewPassword.requestFocus();
                        } else if (response.code() == 404) {
                            errorMessage = "User not found";
                            redirectToLogin();
                        } else if (response.code() >= 500) {
                            errorMessage = "Server error, please try again later";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                btnChangePassword.setEnabled(true);
                btnChangePassword.setText("UPDATE PASSWORD");
                Toast.makeText(ChangePasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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

    private void performLogout() {
        userPrefs.edit().clear().apply();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        redirectToLogin();
    }
}