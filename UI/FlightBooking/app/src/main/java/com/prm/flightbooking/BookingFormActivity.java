package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.BookingApiEndpoint;
import com.prm.flightbooking.dto.booking.BookingResponseDto;
import com.prm.flightbooking.dto.booking.BookingSeatDto;
import com.prm.flightbooking.dto.booking.CreateBookingDto;
import com.prm.flightbooking.dto.seat.SelectedSeatInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingFormActivity extends AppCompatActivity {

    // Không cần các TextInputEditText nữa
    // private TextInputEditText etPassengerName, etPassengerIdNumber, etNotes;

    private TextView tvBookingSummary; // Để hiển thị tóm tắt các ghế đã chọn
    private Button btnBook;
    private ProgressBar progressBar;
    private BookingApiEndpoint bookingApi;
    // Thay đổi từ String selectedSeat sang List<SelectedSeatInfo>
    private List<SelectedSeatInfo> selectedSeatsList;
    private int flightId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_form);

        // Initialize API service
        bookingApi = ApiServiceProvider.getBookingApi();

        // Get data from Intent and SharedPreferences
        // Lấy danh sách ghế đã chọn
        selectedSeatsList = (List<SelectedSeatInfo>) getIntent().getSerializableExtra("selectedSeatsList");

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        flightId = prefs.getInt("flightId", -1);
        int userId = prefs.getInt("user_id", -1);

        if (flightId == -1 || userId <= 0 || selectedSeatsList == null || selectedSeatsList.isEmpty()) {
            Toast.makeText(this, "Invalid session data or no seats selected.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindingView();
        bindingAction();
        displayBookingSummary(); // Hiển thị tóm tắt thông tin các ghế
    }

    private void bindingView() {
        // etPassengerName = findViewById(R.id.et_passenger_name); // Không cần nữa
        // etPassengerIdNumber = findViewById(R.id.et_passenger_id_number); // Không cần nữa
        // etNotes = findViewById(R.id.et_notes); // Có thể giữ lại nếu muốn nhập notes cuối cùng
        tvBookingSummary = findViewById(R.id.tv_booking_summary); // Cần thêm vào layout
        btnBook = findViewById(R.id.btn_book);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void bindingAction() {
        btnBook.setOnClickListener(v -> performBooking());
    }

    private void displayBookingSummary() {
        StringBuilder summary = new StringBuilder("Booking Details:\n\n");
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (SelectedSeatInfo seat : selectedSeatsList) {
            summary.append("Seat: ").append(seat.getSeatNumber())
                    .append(" (").append(seat.getSeatClassName()).append(")\n")
                    .append("  Passenger: ").append(seat.getPassengerName()).append("\n")
                    .append("  ID: ").append(seat.getPassengerIdNumber()).append("\n");
            if (seat.getTotalPrice() != null) {
                totalPrice = totalPrice.add(seat.getTotalPrice());
            }
        }
        summary.append("\nTotal Seats: ").append(selectedSeatsList.size());
        summary.append("\nTotal Price: $").append(totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        tvBookingSummary.setText(summary.toString());
    }



    private void performBooking() {
        // Lấy ghi chú cuối cùng từ form nếu có
        // String notes = etNotes.getText().toString().trim(); // Giữ lại nếu muốn nhập notes

        // Get userId from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId <= 0) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Show progress and disable button
        progressBar.setVisibility(View.VISIBLE);
        btnBook.setEnabled(false);

        // Tạo danh sách BookingSeatDto từ selectedSeatsList
        List<BookingSeatDto> seats = new ArrayList<>();
        for (SelectedSeatInfo info : selectedSeatsList) {
            // Sửa đổi để truyền đúng seatId (là int) từ SelectedSeatInfo
            BookingSeatDto seatDto = new BookingSeatDto(info.getSeatId(), info.getPassengerName(), info.getPassengerIdNumber());
            seats.add(seatDto);
        }

        // Tạo CreateBookingDto
        // Lưu ý: Nếu không cần nhập notes trong BookingFormActivity, bạn có thể truyền null hoặc chuỗi rỗng
        CreateBookingDto bookingDto = new CreateBookingDto(userId, flightId, seats, "Notes from booking process"); // Thay bằng notes thực tế

        // Call API
        Call<BookingResponseDto> call = bookingApi.createBooking(bookingDto);
        call.enqueue(new Callback<BookingResponseDto>() {
            @Override
            public void onResponse(Call<BookingResponseDto> call, Response<BookingResponseDto> response) {
                progressBar.setVisibility(View.GONE);
                btnBook.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    BookingResponseDto bookingResponse = response.body();
                    String successMessage = "Booking successful! Reference: " + bookingResponse.getBookingReference();
                    Toast.makeText(BookingFormActivity.this, successMessage, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(BookingFormActivity.this, MainMenuActivity.class);
                    // Clear back stack để người dùng không quay lại trang booking form
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<BookingResponseDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnBook.setEnabled(true);
                Toast.makeText(BookingFormActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleErrorResponse(Response<BookingResponseDto> response) {
        String errorMessage = "Booking failed";
        if (response.errorBody() != null) {
            try {
                errorMessage = response.errorBody().string(); // Đọc thông báo lỗi từ body
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        switch (response.code()) {
            case 400:
                errorMessage = "Invalid booking details: " + errorMessage;
                break;
            case 404:
                errorMessage = "Flight or seat not found: " + errorMessage;
                break;
            case 409:
                errorMessage = "Seat already booked: " + errorMessage;
                break;
            case 500:
                errorMessage = "Server error, please try again later: " + errorMessage;
                break;
            default:
                errorMessage = "Unexpected error occurred: " + errorMessage;
                break;
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
