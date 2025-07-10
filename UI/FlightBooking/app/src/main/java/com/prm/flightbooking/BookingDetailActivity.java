package com.prm.flightbooking;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.BookingApiEndpoint;
import com.prm.flightbooking.dto.booking.BookingDetailDto;
import com.prm.flightbooking.dto.booking.FlightDetailDto;
import com.prm.flightbooking.dto.booking.PassengerSeatDto;

import java.math.BigDecimal;
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
    private ProgressBar progressBar;

    private BookingApiEndpoint bookingApi;
    private int userId;
    private int bookingId;

    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        bookingApi = ApiServiceProvider.getBookingApi();

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bookingId = getIntent().getIntExtra("bookingId", -1);
        if (bookingId == -1) {
            Toast.makeText(this, "Booking ID not found", Toast.LENGTH_SHORT).show();
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
        progressBar = findViewById(R.id.progress_bar);
    }

    private void fetchBookingDetail() {
        progressBar.setVisibility(View.VISIBLE);

        Call<BookingDetailDto> call = bookingApi.getBookingDetail(userId, bookingId);
        Log.d("BookingDetail", "Fetching detail for bookingId: " + bookingId);
        call.enqueue(new Callback<BookingDetailDto>() {
            @Override
            public void onResponse(Call<BookingDetailDto> call, Response<BookingDetailDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    populateBookingDetail(response.body());
                } else {
                    Toast.makeText(BookingDetailActivity.this, "Failed to load booking detail", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<BookingDetailDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BookingDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void populateBookingDetail(BookingDetailDto detail) {
        tvBookingReference.setText(detail.getBookingReference());
        tvStatus.setText(detail.getBookingStatus());
        tvPaymentStatus.setText(detail.getPaymentStatus());
        tvPrice.setText(detail.getTotalAmount() != null ? String.format(Locale.getDefault(), "$%.2f", detail.getTotalAmount()) : "N/A");
        tvBookingDate.setText(detail.getBookingDate() != null ? dateTimeFormat.format(detail.getBookingDate()) : "N/A");
        tvNotes.setText(detail.getNotes() != null ? detail.getNotes() : "No notes");

        FlightDetailDto flight = detail.getFlight();
        if (flight != null) {
            tvFlightNumber.setText(flight.getFlightNumber());
            tvAirline.setText(flight.getAirlineName());
            tvAircraftModel.setText(flight.getAircraftModel());
            tvDepartureAirport.setText(flight.getDepartureAirport());
            tvArrivalAirport.setText(flight.getArrivalAirport());
            tvDepartureTime.setText(flight.getDepartureTime() != null ? dateTimeFormat.format(flight.getDepartureTime()) : "N/A");
            tvArrivalTime.setText(flight.getArrivalTime() != null ? dateTimeFormat.format(flight.getArrivalTime()) : "N/A");
            tvGate.setText(flight.getGate() != null ? flight.getGate() : "N/A");
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
                tvSeatPrice.setText(seatPrice != null ? String.format(Locale.getDefault(), "$%.2f", seatPrice) : "N/A");

                // Hiển thị loại ghế: window, aisle hay middle
                String seatType = "Middle";
                if (passenger.isWindow()) seatType = "Window";
                else if (passenger.isAisle()) seatType = "Aisle";
                tvSeatType.setText(seatType);

                passengerContainer.addView(passengerView);
            }
        } else {
            TextView noPassenger = new TextView(this);
            noPassenger.setText("No passengers found");
            passengerContainer.addView(noPassenger);
        }
    }
}
