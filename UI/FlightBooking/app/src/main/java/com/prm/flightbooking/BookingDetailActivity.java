package com.prm.flightbooking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.BookingApiEndpoint;
import com.prm.flightbooking.dto.booking.BookingDetailDto;
import com.prm.flightbooking.dto.booking.FlightDetailDto;
import com.prm.flightbooking.dto.booking.PassengerSeatDto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailActivity extends AppCompatActivity {

    private TextView tvBookingReference, tvStatus, tvPaymentStatus, tvPrice, tvBookingDate;
    private TextView tvFlightNumber, tvAirline, tvAircraftModel, tvDepartureAirport, tvArrivalAirport;
    private TextView tvDepartureTime, tvArrivalTime, tvGate, tvNotes;
    private LinearLayout passengerContainer;
    private LinearLayout seatSummaryContainer;
    private ProgressBar progressBar;
    private BookingApiEndpoint bookingApi;
    private int userId;
    private int bookingId;
    private Button btnCancelBooking;
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("vi", "VN"));
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        bookingApi = ApiServiceProvider.getBookingApi();

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookingId = getIntent().getIntExtra("bookingId", -1);
        if (bookingId == -1) {
            Toast.makeText(this, "Không tìm thấy mã đặt vé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();
        fetchBookingDetail();
    }

    private void bindViews() {
        tvBookingReference = findViewById(R.id.tv_booking_reference);
        tvStatus = findViewById(R.id.tv_status);
        tvPaymentStatus = findViewById(R.id.tv_payment_status);
        tvPrice = findViewById(R.id.tv_price);
        tvBookingDate = findViewById(R.id.tv_booking_date);
        tvFlightNumber = findViewById(R.id.tv_flight_number);
        tvAirline = findViewById(R.id.tv_airline);
        tvAircraftModel = findViewById(R.id.tv_aircraft_model);
        tvDepartureAirport = findViewById(R.id.tv_departure_airport);
        tvArrivalAirport = findViewById(R.id.tv_arrival_airport);
        tvDepartureTime = findViewById(R.id.tv_departure_time);
        tvArrivalTime = findViewById(R.id.tv_arrival_time);
        tvGate = findViewById(R.id.tv_gate);
        tvNotes = findViewById(R.id.tv_notes);
        passengerContainer = findViewById(R.id.passenger_container);
        seatSummaryContainer = findViewById(R.id.seat_summary_container);
        progressBar = findViewById(R.id.progress_bar);
        btnCancelBooking = findViewById(R.id.btn_cancel_booking);
    }

    private void fetchBookingDetail() {
        progressBar.setVisibility(View.VISIBLE);

        Call<BookingDetailDto> call = bookingApi.getBookingDetail(userId, bookingId);
        Log.d("BookingDetail", "Lấy chi tiết đặt vé với bookingId: " + bookingId);
        call.enqueue(new Callback<BookingDetailDto>() {
            @Override
            public void onResponse(Call<BookingDetailDto> call, Response<BookingDetailDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    populateBookingDetail(response.body());
                } else {
                    Toast.makeText(BookingDetailActivity.this, "Không tải được chi tiết đặt vé", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<BookingDetailDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BookingDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateBookingDetail(BookingDetailDto detail) {
        tvBookingReference.setText("Mã đặt vé: " + detail.getBookingReference());

        String status = detail.getBookingStatus();
        switch (status.toUpperCase()) {
            case "CONFIRMED":
                tvStatus.setText("✅ Đã xác nhận");
                break;
            case "CANCELLED":
                tvStatus.setText("❌ Đã hủy");
                break;
            case "PENDING":
                tvStatus.setText("⏳ Đang chờ");
                break;
            default:
                tvStatus.setText(status);
                break;
        }

        String paymentStatus = detail.getPaymentStatus();
        switch (paymentStatus.toUpperCase()) {
            case "PAID":
                tvPaymentStatus.setText("Đã thanh toán");
                break;
            case "PENDING":
                tvPaymentStatus.setText("Chưa thanh toán");
                break;
            default:
                tvPaymentStatus.setText(paymentStatus);
                break;
        }

        BigDecimal amount = detail.getTotalAmount();
        if (amount != null) {
            tvPrice.setText(currencyFormat.format(amount));
        } else {
            tvPrice.setText("N/A");
        }

        tvBookingDate.setText(detail.getBookingDate() != null ? dateTimeFormat.format(detail.getBookingDate()) : "N/A");
        tvNotes.setText(detail.getNotes() != null ? detail.getNotes() : "Không có ghi chú");

        FlightDetailDto flight = detail.getFlight();
        if (flight != null) {
            tvFlightNumber.setText(flight.getFlightNumber());
            tvAirline.setText("Hãng bay: " + flight.getAirlineName());
            tvAircraftModel.setText("Loại máy bay: " + flight.getAircraftModel());
            tvDepartureAirport.setText(flight.getDepartureAirport());
            tvArrivalAirport.setText(flight.getArrivalAirport());
            tvDepartureTime.setText(flight.getDepartureTime() != null ? dateTimeFormat.format(flight.getDepartureTime()) : "N/A");
            tvArrivalTime.setText(flight.getArrivalTime() != null ? dateTimeFormat.format(flight.getArrivalTime()) : "N/A");
            tvGate.setText(flight.getGate() != null ? "Cổng: " + flight.getGate() : "N/A");
        }

        // Hiển thị danh sách hành khách
        passengerContainer.removeAllViews();
        if (detail.getPassengers() != null && !detail.getPassengers().isEmpty()) {
            for (PassengerSeatDto passenger : detail.getPassengers()) {
                View passengerView = getLayoutInflater().inflate(R.layout.item_passenger_detail, passengerContainer, false);

                TextView tvPassengerName = passengerView.findViewById(R.id.tv_passenger_name);
                TextView tvSeatNumber = passengerView.findViewById(R.id.tv_seat_number);
                TextView tvSeatClass = passengerView.findViewById(R.id.tv_seat_class);
                TextView tvSeatPrice = passengerView.findViewById(R.id.tv_seat_price);
                TextView tvSeatType = passengerView.findViewById(R.id.tv_seat_type);

                tvPassengerName.setText(passenger.getPassengerName());
                tvSeatNumber.setText(passenger.getSeatNumber());
                tvSeatClass.setText(passenger.getSeatClass());
                BigDecimal seatPrice = passenger.getSeatPrice();
                tvSeatPrice.setText(seatPrice != null ? currencyFormat.format(seatPrice) : "N/A");

                String seatType = "Ghế giữa";
                if (passenger.isWindow()) seatType = "Ghế cửa sổ";
                else if (passenger.isAisle()) seatType = "Ghế lối đi";
                tvSeatType.setText(seatType);

                passengerContainer.addView(passengerView);

                ImageButton btnOptions = passengerView.findViewById(R.id.btn_passenger_options);
                LinearLayout detailLayout = passengerView.findViewById(R.id.layout_passenger_detail);

                // Mặc định mở phần chi tiết
                detailLayout.setVisibility(View.VISIBLE);
                btnOptions.setRotation(180);

                btnOptions.setOnClickListener(v -> {
                    if (detailLayout.getVisibility() == View.VISIBLE) {
                        detailLayout.setVisibility(View.GONE);
                        btnOptions.setRotation(0);
                    } else {
                        detailLayout.setVisibility(View.VISIBLE);
                        btnOptions.setRotation(180);
                    }
                });
            }
        } else {
            TextView noPassenger = new TextView(this);
            noPassenger.setText("Không có hành khách");
            passengerContainer.addView(noPassenger);
        }

        seatSummaryContainer.removeAllViews();
        if (detail.getPassengers() != null && !detail.getPassengers().isEmpty()) {
            for (PassengerSeatDto passenger : detail.getPassengers()) {
                // Tạo TextView cho mỗi hành khách
                TextView tvSummary = new TextView(this);
                tvSummary.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                tvSummary.setTextSize(14f);
                tvSummary.setTextColor(getResources().getColor(android.R.color.black));

                // Nội dung: Tên hành khách - Số ghế - Loại ghế
                String seatType = "Ghế giữa";
                if (passenger.isWindow()) seatType = "Ghế cửa sổ";
                else if (passenger.isAisle()) seatType = "Ghế lối đi";

                String text = String.format("%s - Ghế %s - %s",
                        passenger.getPassengerName(),
                        passenger.getSeatNumber(),
                        passenger.getSeatClass() + " / " + seatType);

                tvSummary.setText(text);

                seatSummaryContainer.addView(tvSummary);
            }
        } else {
            TextView noSeatSummary = new TextView(this);
            noSeatSummary.setText("Không có thông tin ghế");
            noSeatSummary.setTextColor(getResources().getColor(android.R.color.darker_gray));
            seatSummaryContainer.addView(noSeatSummary);
        }

        // Hiển thị nút Hủy chuyến bay nếu trạng thái là CONFIRMED
        if ("CONFIRMED".equalsIgnoreCase(detail.getBookingStatus())) {
            btnCancelBooking.setVisibility(View.VISIBLE);
            btnCancelBooking.setOnClickListener(v -> showCancelConfirmationDialog());
        } else {
            btnCancelBooking.setVisibility(View.GONE);
        }
    }

    private void cancelBooking() {
        progressBar.setVisibility(View.VISIBLE);
        bookingApi.cancelBookingUser(userId, bookingId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(BookingDetailActivity.this, "Hủy vé thành công", Toast.LENGTH_SHORT).show();
                    fetchBookingDetail(); // Cập nhật lại thông tin vé
                } else {
                    Toast.makeText(BookingDetailActivity.this, "Không thể hủy vé. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BookingDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy vé")
                .setMessage("Bạn có chắc chắn muốn hủy vé này không?")
                .setPositiveButton("Hủy vé", (dialog, which) -> cancelBooking())
                .setNegativeButton("Thoát", null)
                .show();
    }
}
