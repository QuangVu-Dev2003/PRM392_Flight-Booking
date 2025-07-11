package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.NotificationApiEndpoint;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenuActivity extends AppCompatActivity {

    private TextView tvWelcome, tvUsername;
    private ImageView ivAvatar;
    private Button btnBannerBook;
    private LinearLayout menuBookFlight, menuMyTrips, menuNotifications, menuProfile;
    private BottomNavigationView bottomNavigation;
    private SharedPreferences userPrefs;
    private TextView tvNotificationsBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Khởi tạo SharedPreferences
        userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Kiểm tra trạng thái đăng nhập
        if (!isLoggedIn()) {
            redirectToLogin();
            return;
        }

        bindingView();
        bindingAction();
        loadUserInfo();
    }

    private void bindingView() {
        tvWelcome = findViewById(R.id.tv_welcome);
        tvUsername = findViewById(R.id.tv_username);
        ivAvatar = findViewById(R.id.iv_avatar);

        btnBannerBook = findViewById(R.id.btn_banner_book);

        menuBookFlight = findViewById(R.id.menu_book_flight);
        menuMyTrips = findViewById(R.id.menu_my_trips);
        menuNotifications = findViewById(R.id.menu_notifications);
        menuProfile = findViewById(R.id.menu_profile);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        tvNotificationsBadge = findViewById(R.id.tv_notifications_badge);
    }

    private void bindingAction() {
        btnBannerBook.setOnClickListener(this::onBannerBookClick);

        menuBookFlight.setOnClickListener(this::onBookFlightClick);
        menuMyTrips.setOnClickListener(this::onMyTripsClick);
        menuNotifications.setOnClickListener(this::onNotificationsClick);
        menuProfile.setOnClickListener(this::onProfileClick);

        ivAvatar.setOnClickListener(this::onAvatarClick);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            handleBottomNavigation(itemId);
            return true;
        });
    }

    private void onBannerBookClick(View view) {
        navigateToActivity(BookingActivity.class);
    }

    private void onBookFlightClick(View view) {
        navigateToActivity(BookingActivity.class);
    }

    private void onMyTripsClick(View view) {
        navigateToActivity(BookingHistoryActivity.class);
    }

    private void onNotificationsClick(View view) {
        navigateToActivity(NotificationActivity.class);
    }

    private void onProfileClick(View view) {
        navigateToActivity(Profile.class);
    }

    private void onAvatarClick(View view) {
        navigateToActivity(Profile.class);
    }

    private void handleBottomNavigation(int itemId) {
        if (itemId == R.id.nav_home) {
            // Home, do nothing
        } else if (itemId == R.id.nav_search) {
            navigateToActivity(SearchFlightActivity.class);
        } else if (itemId == R.id.nav_trips) {
            navigateToActivity(BookingHistoryActivity.class);
        } else if (itemId == R.id.nav_flights) {
            navigateToActivity(BookingActivity.class);
        } else if (itemId == R.id.nav_more) {
            showMoreOptionsDialog();
        }
    }

    private void navigateToActivity(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }

    private void loadUserInfo() {
        String fullName = userPrefs.getString("user_name", "");
        String username = userPrefs.getString("username", "User");

        // Hiển thị tên người dùng
        if (!fullName.isEmpty()) {
            tvWelcome.setText("Chào mừng trở lại,");
            tvUsername.setText(fullName);
        } else {
            tvWelcome.setText("Chào mừng,");
            tvUsername.setText(username);
        }

        // Hình đại diện
        String avatarPath = userPrefs.getString("avatar_path", "");
        if (!avatarPath.isEmpty()) {
        }
    }

    private void showMoreOptionsDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_more_options, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Các tùy chọn
        LinearLayout optionProfile = bottomSheetView.findViewById(R.id.option_profile);
        LinearLayout optionNotifications = bottomSheetView.findViewById(R.id.option_notifications);
        LinearLayout optionSupport = bottomSheetView.findViewById(R.id.option_support);
        LinearLayout optionLogout = bottomSheetView.findViewById(R.id.option_logout);

        // Set click
        optionProfile.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            navigateToActivity(Profile.class);
        });

        optionNotifications.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            navigateToActivity(NotificationActivity.class);
        });

        optionSupport.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Toast.makeText(this, "Support coming soon", Toast.LENGTH_SHORT).show();
        });

        optionLogout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showLogoutDialog();
        });

        bottomSheetDialog.show();
    }

    private void showLogoutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        // Xóa thông tin đăng nhập
        userPrefs.edit().clear().apply();

        Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();

        // Chuyển về màn hình đăng nhập
        redirectToLogin();
    }

    private boolean isLoggedIn() {
        return userPrefs.getBoolean("is_logged_in", false);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainMenuActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật thông tin user khi quay lại màn hình
        if (isLoggedIn()) {
            loadUserInfo();
            loadUnreadNotificationCountFromApi();
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        } else {
            redirectToLogin();
        }
    }

    private void loadUnreadNotificationCountFromApi() {
        NotificationApiEndpoint notificationApi = ApiServiceProvider.getNotificationApi();
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId <= 0) return;

        notificationApi.getUnreadCount(userId).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateNotificationsBadge(response.body());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                // Xử lý lỗi
            }
        });
    }

    private void updateNotificationsBadge(int unreadCount) {
        if (unreadCount > 0) {
            tvNotificationsBadge.setVisibility(View.VISIBLE);
            if (unreadCount > 99) {
                tvNotificationsBadge.setText("99+");
            } else {
                tvNotificationsBadge.setText(String.valueOf(unreadCount));
            }
        } else {
            tvNotificationsBadge.setVisibility(View.GONE);
        }
    }
}