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
    private TextView tvBookingSummary;
    private TextView tvTotalPrice;
    private Button btnBook;
    private CheckBox cbTerms;
    private ProgressBar progressBar;
    private BookingApiEndpoint bookingApi;
    private List<SelectedSeatInfo> selectedSeatsList;
    private int flightId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        // Khởi tạo API service để gọi API
        bookingApi = ApiServiceProvider.getBookingApi();

        // Lấy dữ liệu từ Intent và SharedPreferences
        selectedSeatsList = (List<SelectedSeatInfo>) getIntent().getSerializableExtra("selectedSeatsList");
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        flightId = prefs.getInt("flightId", -1);
        int userId = prefs.getInt("user_id", -1);

        // Kiểm tra dữ liệu hợp lệ
        if (flightId == -1 || userId <= 0 || selectedSeatsList == null || selectedSeatsList.isEmpty()) {
            Toast.makeText(this, "Dữ liệu phiên không hợp lệ hoặc chưa chọn ghế.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Gọi các hàm khởi tạo view, hành động và hiển thị tóm tắt
        bindingView();
        bindingAction();
        displayBookingSummary();
    }

    // Hàm này để liên kết các view trong layout với code
    private void bindingView() {
        // Tìm và gán các view từ layout
        etNotes = findViewById(R.id.et_notes);
        tvBookingSummary = findViewById(R.id.tv_booking_summary);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        btnBook = findViewById(R.id.btn_book);
        cbTerms = findViewById(R.id.cb_terms);
        progressBar = findViewById(R.id.progress_bar);
    }

    // Hàm này để gán các hành động cho view (như click)
    private void bindingAction() {
        // Gán sự kiện click cho nút đặt vé
        btnBook.setOnClickListener(this::onBtnBookClick);
    }

    // Hàm xử lý khi click nút đặt vé
    private void onBtnBookClick(View view) {
        // Thực hiện đặt vé
        performBooking();
    }

    // Hàm hiển thị tóm tắt thông tin các ghế đã chọn
    private void displayBookingSummary() {
        StringBuilder summary = new StringBuilder("Chi tiết đặt vé:\n\n");
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (SelectedSeatInfo seat : selectedSeatsList) {
            summary.append("Ghế: ").append(seat.getSeatNumber())
                    .append(" (").append(seat.getSeatClassName()).append(")\n")
                    .append("  Hành khách: ").append(seat.getPassengerName()).append("\n")
                    .append("  CMND/CCCD: ").append(seat.getPassengerIdNumber()).append("\n");
            if (seat.getTotalPrice() != null) {
                totalPrice = totalPrice.add(seat.getTotalPrice());
            }
        }
        summary.append("\nTổng số ghế: ").append(selectedSeatsList.size());

        // Định dạng tiền Việt Nam
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
        String formattedPrice = decimalFormat.format(totalPrice) + " VNĐ";

        summary.append("\nTổng giá: ").append(formattedPrice);
        tvBookingSummary.setText(summary.toString());
        tvTotalPrice.setText(formattedPrice);
    }

    // Hàm thực hiện gọi API để đặt vé
    private void performBooking() {
        // Kiểm tra xem người dùng đã đồng ý với điều khoản chưa
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với Điều khoản và Điều kiện.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy userId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị ProgressBar và vô hiệu hóa nút đặt vé
        progressBar.setVisibility(View.VISIBLE);
        btnBook.setEnabled(false);

        // Lấy ghi chú từ người dùng, nếu rỗng thì dùng giá trị mặc định
        String notes = etNotes.getText().toString().trim();
        if (notes.isEmpty()) {
            notes = "Người dùng không yêu cầu gì thêm";
        }

        // Tạo danh sách BookingSeatDto từ selectedSeatsList
        List<BookingSeatDto> seats = new ArrayList<>();
        for (SelectedSeatInfo info : selectedSeatsList) {
            BookingSeatDto seatDto = new BookingSeatDto(info.getSeatId(), info.getPassengerName(), info.getPassengerIdNumber());
            seats.add(seatDto);
        }

        // Tạo CreateBookingDto với thông tin đặt vé
        CreateBookingDto bookingDto = new CreateBookingDto(userId, flightId, seats, notes);

        // Gọi API để đặt vé
        Call<BookingResponseDto> call = bookingApi.createBooking(bookingDto);
        call.enqueue(new Callback<BookingResponseDto>() {
            @Override
            public void onResponse(Call<BookingResponseDto> call, Response<BookingResponseDto> response) {
                // Ẩn ProgressBar và kích hoạt lại nút
                progressBar.setVisibility(View.GONE);
                btnBook.setEnabled(true);

                // Kiểm tra xem API có trả về thành công không
                if (response.isSuccessful() && response.body() != null) {
                    BookingResponseDto bookingResponse = response.body();
                    String successMessage = "Đặt vé thành công! Mã tham chiếu: " + bookingResponse.getBookingReference();
                    Toast.makeText(BookingFormActivity.this, successMessage, Toast.LENGTH_LONG).show();

                    // Gửi notification hiển thị trên thanh thông báo
                    sendBookingSuccessNotification(bookingResponse.getBookingReference());

                    // Chuyển về MainMenuActivity và xóa back stack
                    Intent intent = new Intent(BookingFormActivity.this, MainMenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Xử lý lỗi nếu API thất bại
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<BookingResponseDto> call, Throwable t) {
                // Ẩn ProgressBar và kích hoạt lại nút khi có lỗi mạng
                progressBar.setVisibility(View.GONE);
                btnBook.setEnabled(true);
                Toast.makeText(BookingFormActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm xử lý lỗi từ phản hồi của server
    private void handleErrorResponse(Response<BookingResponseDto> response) {
        String errorMessage = "Đặt vé thất bại";
        if (response.errorBody() != null) {
            try {
                errorMessage = response.errorBody().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Kiểm tra mã lỗi từ server
        if (response.code() == 400) {
            errorMessage = "Thông tin đặt vé không hợp lệ: " + errorMessage;
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy chuyến bay hoặc ghế: " + errorMessage;
        } else if (response.code() == 409) {
            errorMessage = "Ghế đã được đặt bởi người khác: " + errorMessage;
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau: " + errorMessage;
        }
        // Hiển thị thông báo lỗi
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private int notificationId = 1000; // id cho notification, có thể tăng dần nếu muốn nhiều notification

    private void sendBookingSuccessNotification(String bookingReference) {
        String channelId = "BookingChannelId";
        String channelName = "Booking Notifications";

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Tạo NotificationChannel cho Android 8.0 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo intent khi người dùng nhấn vào notification sẽ mở app (hoặc trang chi tiết booking nếu có)
        Intent intent = new Intent(this, MainMenuActivity.class); // hoặc BookingDetailActivity nếu có
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        // Xây dựng notification
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Đặt vé thành công")
                .setContentText("Mã đặt chỗ: " + bookingReference)
                .setSmallIcon(R.drawable.ic_notifications) // icon bạn dùng trong project
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Mã đặt chỗ: " + bookingReference))
                .build();

        // Hiển thị notification
        notificationManager.notify(notificationId++, notification);
    }
}