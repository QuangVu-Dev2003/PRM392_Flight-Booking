package com.prm.flightbooking;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.BookingApiEndpoint;
import com.prm.flightbooking.dto.booking.BookingResponseDto;
import com.prm.flightbooking.dto.booking.BookingSeatDto;
import com.prm.flightbooking.dto.booking.CreateBookingDto;
import com.prm.flightbooking.dto.seat.SelectedSeatInfo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingFormActivity extends AppCompatActivity {

    private TextInputEditText etNotes;
    private TextView tvBookingSummary, tvTotalPrice;
    private Button btnBook;
    private CheckBox cbTerms;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private BookingApiEndpoint bookingApi;
    private SharedPreferences sharedPreferences;
    private List<SelectedSeatInfo> selectedSeatsList;
    private int flightId, userId;
    private int notificationId = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        // Khởi tạo API và SharedPreferences
        bookingApi = ApiServiceProvider.getBookingApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Kiểm tra dữ liệu hợp lệ
        if (!validateSessionData()) {
            return;
        }

        bindingView();
        bindingAction();
        displayBookingSummary();
    }

    // Liên kết các view trong layout
    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        etNotes = findViewById(R.id.et_notes);
        tvBookingSummary = findViewById(R.id.tv_booking_summary);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnBook = findViewById(R.id.btn_book);
        cbTerms = findViewById(R.id.cb_terms);
        progressBar = findViewById(R.id.progress_bar);
    }

    // Gán sự kiện cho các view
    private void bindingAction() {
        btnBack.setOnClickListener(v -> finish());
        btnBook.setOnClickListener(this::onBtnBookClick);
    }

    // Xử lý khi nhấn nút đặt vé
    private void onBtnBookClick(View view) {
        performBooking();
    }

    // Kiểm tra dữ liệu phiên làm việc
    private boolean validateSessionData() {
        selectedSeatsList = (List<SelectedSeatInfo>) getIntent().getSerializableExtra("selectedSeatsList");
        flightId = sharedPreferences.getInt("flightId", -1);
        userId = sharedPreferences.getInt("user_id", -1);

        if (flightId == -1 || userId <= 0 || selectedSeatsList == null || selectedSeatsList.isEmpty()) {
            Toast.makeText(this, "Dữ liệu không hợp lệ hoặc chưa chọn ghế", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
    }

    // Hiển thị tóm tắt thông tin đặt vé
    private void displayBookingSummary() {
        StringBuilder summary = new StringBuilder();
        BigDecimal overallTotalPrice = BigDecimal.ZERO;
        summary.append("Thông tin đặt vé:\n\n");

        // Hiển thị chi tiết từng ghế
        for (int i = 0; i < selectedSeatsList.size(); i++) {
            SelectedSeatInfo seat = selectedSeatsList.get(i);
            summary.append("🔵 Ghế số: ")
                    .append(seat.getSeatNumber())
                    .append(" (").append(seat.getSeatClassName()).append(")\n");
            summary.append("   - Hành khách: ").append(seat.getPassengerName()).append("\n");
            summary.append("   - CMND/CCCD: ").append(seat.getPassengerIdNumber()).append("\n");

            if (seat.getTotalPrice() != null) {
                String seatFormattedPrice = formatCurrency(seat.getTotalPrice());
                summary.append("   - Giá ghế: ").append(seatFormattedPrice).append("\n"); // Hiển thị giá từng ghế
                overallTotalPrice = overallTotalPrice.add(seat.getTotalPrice()); // Vẫn cộng vào tổng để hiển thị ở tvTotalPrice
            } else {
                summary.append("   - Giá ghế: N/A\n"); // Xử lý trường hợp giá không có
            }

            // Thêm dấu phân cách giữa các ghế nếu không phải ghế cuối cùng
            if (i < selectedSeatsList.size() - 1) {
                summary.append("-----------------------------------------------------\n");
            }
        }

        summary.append("\n📊 Tổng số ghế đã chọn: ")
                .append(selectedSeatsList.size()).append(" ghế");

        String overallFormattedPrice = formatCurrency(overallTotalPrice);

        tvBookingSummary.setText(summary.toString());
        tvTotalPrice.setText(overallFormattedPrice);
    }

    // Định dạng tiền tệ Việt Nam
    private String formatCurrency(BigDecimal amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        return decimalFormat.format(amount) + " VNĐ";
    }

    // Thực hiện đặt vé
    private void performBooking() {
        if (!validateBookingInput()) {
            return;
        }

        // Hiển thị trạng thái đang xử lý
        setBookingInProgress(true);

        // Tạo dữ liệu đặt vé
        CreateBookingDto bookingDto = createBookingData();

        // Gọi API đặt vé
        Call<BookingResponseDto> call = bookingApi.createBooking(bookingDto);
        call.enqueue(new Callback<BookingResponseDto>() {
            @Override
            public void onResponse(Call<BookingResponseDto> call, Response<BookingResponseDto> response) {
                setBookingInProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    handleBookingSuccess(response.body());
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<BookingResponseDto> call, Throwable t) {
                setBookingInProgress(false);
                Toast.makeText(BookingFormActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Kiểm tra dữ liệu đầu vào
    private boolean validateBookingInput() {
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với điều khoản và điều kiện", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Tạo dữ liệu đặt vé
    private CreateBookingDto createBookingData() {
        String notes = etNotes.getText().toString().trim();
        if (notes.isEmpty()) {
            notes = "Không có yêu cầu đặc biệt";
        }

        List<BookingSeatDto> seats = new ArrayList<>();
        for (SelectedSeatInfo info : selectedSeatsList) {
            BookingSeatDto seatDto = new BookingSeatDto(
                    info.getSeatId(),
                    info.getPassengerName(),
                    info.getPassengerIdNumber()
            );
            seats.add(seatDto);
        }

        return new CreateBookingDto(userId, flightId, seats, notes);
    }

    // Thiết lập trạng thái đang xử lý
    private void setBookingInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        btnBook.setEnabled(!inProgress);
        btnBook.setText(inProgress ? "Đang xử lý..." : "XÁC NHẬN ĐẶT VÉ");
    }

    // Xử lý khi đặt vé thành công
    private void handleBookingSuccess(BookingResponseDto bookingResponse) {
        String bookingReference = bookingResponse.getBookingReference();
        int bookingId = bookingResponse.getBookingId();
        String successMessage = "Đặt vé thành công! Mã tham chiếu: " + bookingReference;

        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();

        // Gửi thông báo
        sendBookingSuccessNotification(bookingReference, bookingId);

        // Chuyển về màn hình chính
        navigateToMainMenu();
    }

    // Xử lý lỗi từ server
    private void handleErrorResponse(Response<BookingResponseDto> response) {
        String errorMessage = "Đặt vé thất bại";

        if (response.code() == 400) {
            errorMessage = "Thông tin đặt vé không hợp lệ";
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy chuyến bay hoặc ghế";
        } else if (response.code() == 409) {
            errorMessage = "Ghế đã được đặt bởi người khác";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Gửi thông báo đặt vé thành công
    private void sendBookingSuccessNotification(String bookingReference, int bookingId) {
        String channelId = "BookingChannelId";
        String channelName = "Thông báo đặt vé";

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Tạo kênh thông báo cho Android 8.0 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo intent khi nhấn vào thông báo
        Intent intent = new Intent(this, BookingDetailActivity.class);
        intent.putExtra("bookingId", bookingId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                bookingId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        // Xây dựng thông báo
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Đặt vé thành công")
                .setContentText("Mã đặt chỗ: " + bookingReference)
                .setSmallIcon(R.drawable.ic_notifications)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Mã đặt chỗ: " + bookingReference))
                .build();

        notificationManager.notify(notificationId++, notification);
    }

    // Chuyển về màn hình chính
    private void navigateToMainMenu() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}