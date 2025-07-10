package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.BookingApiEndpoint;
import com.prm.flightbooking.dto.booking.UserBookingHistoryDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView rvBookingHistory;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private TextView tvNoBookings;
    private ImageButton btnBack, btnFilter;
    private TextInputEditText etSearch;
    private CardView chipAll, chipUpcoming, chipCompleted, chipCancelled;
    private BookingApiEndpoint bookingApi;
    private BookingHistoryAdapter adapter;
    private List<UserBookingHistoryDto> bookingList, filteredList;
    private int userId;
    private int currentPage = 1;
    private final int PAGE_SIZE = 10;
    private String currentFilter = "All";

    private LinearLayout searchFilterContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        // Initialize API service
        bookingApi = ApiServiceProvider.getBookingApi();

        // Retrieve userId from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        Log.d("BookingHistory", "Retrieved userId: " + userId);
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        bindingView();
        setupRecyclerView();
        setupListeners();
        fetchBookingHistory();
    }

    private void bindingView() {
        rvBookingHistory = findViewById(R.id.rv_booking_history);
        progressBar = findViewById(R.id.progress_bar);
        emptyState = findViewById(R.id.empty_state);
        tvNoBookings = findViewById(R.id.tv_no_bookings);
        btnBack = findViewById(R.id.btn_back);
        btnFilter = findViewById(R.id.btn_filter);
        etSearch = findViewById(R.id.et_search);
        chipAll = findViewById(R.id.chip_all);
        chipUpcoming = findViewById(R.id.chip_upcoming);
        chipCompleted = findViewById(R.id.chip_completed);
        chipCancelled = findViewById(R.id.chip_cancelled);
        searchFilterContainer = findViewById(R.id.search_filter_container);
    }

    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new BookingHistoryAdapter(filteredList);
        rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));
        rvBookingHistory.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnFilter.setOnClickListener(v -> {
            if (searchFilterContainer.getVisibility() == View.VISIBLE) {
                searchFilterContainer.setVisibility(View.GONE);
            } else {
                searchFilterContainer.setVisibility(View.VISIBLE);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterBookings(s.toString());
            }
        });

        chipAll.setOnClickListener(v -> setFilter("All"));
        chipUpcoming.setOnClickListener(v -> setFilter("Upcoming"));
        chipCompleted.setOnClickListener(v -> setFilter("Completed"));
        chipCancelled.setOnClickListener(v -> setFilter("Cancelled"));

        findViewById(R.id.btn_book_flight).setOnClickListener(v -> {
            // Navigate to booking activity (replace with actual activity)
            startActivity(new Intent(this, BookingHistoryActivity.class));
        });
    }

    private void setFilter(String filter) {
        currentFilter = filter;
        updateChipStyles();
        filterBookings(etSearch.getText().toString());
    }

    private void updateChipStyles() {
        int textColor = getResources().getColor(R.color.white);
        int inactiveTextColor = getResources().getColor(R.color.grey_600);

        TextView tvAll = chipAll.findViewById(R.id.tv_chip_all);
        TextView tvUpcoming = chipUpcoming.findViewById(R.id.tv_chip_upcoming);
        TextView tvCompleted = chipCompleted.findViewById(R.id.tv_chip_completed);
        TextView tvCancelled = chipCancelled.findViewById(R.id.tv_chip_cancelled);

        chipAll.setCardBackgroundColor(getResources().getColor(currentFilter.equals("All") ? R.color.blue_500 : R.color.grey_200));
        chipUpcoming.setCardBackgroundColor(getResources().getColor(currentFilter.equals("Upcoming") ? R.color.blue_500 : R.color.grey_200));
        chipCompleted.setCardBackgroundColor(getResources().getColor(currentFilter.equals("Completed") ? R.color.blue_500 : R.color.grey_200));
        chipCancelled.setCardBackgroundColor(getResources().getColor(currentFilter.equals("Cancelled") ? R.color.blue_500 : R.color.grey_200));

        if (tvAll != null) tvAll.setTextColor(currentFilter.equals("All") ? textColor : inactiveTextColor);
        if (tvUpcoming != null) tvUpcoming.setTextColor(currentFilter.equals("Upcoming") ? textColor : inactiveTextColor);
        if (tvCompleted != null) tvCompleted.setTextColor(currentFilter.equals("Completed") ? textColor : inactiveTextColor);
        if (tvCancelled != null) tvCancelled.setTextColor(currentFilter.equals("Cancelled") ? textColor : inactiveTextColor);
    }

    private void filterBookings(String query) {
        filteredList.clear();
        for (UserBookingHistoryDto booking : bookingList) {
            boolean matchesFilter = currentFilter.equals("All") ||
                    (currentFilter.equals("Upcoming") && booking.getBookingStatus().equalsIgnoreCase("Confirmed")) ||
                    (currentFilter.equals("Completed") && booking.getBookingStatus().equalsIgnoreCase("Completed")) ||
                    (currentFilter.equals("Cancelled") && booking.getBookingStatus().equalsIgnoreCase("Cancelled"));

            boolean matchesQuery = query.isEmpty() ||
                    booking.getRoute().toLowerCase().contains(query.toLowerCase()) ||
                    booking.getBookingReference().toLowerCase().contains(query.toLowerCase()) ||
                    booking.getBookingDate().toString().toLowerCase().contains(query.toLowerCase());

            if (matchesFilter && matchesQuery) {
                filteredList.add(booking);
            }
        }
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void fetchBookingHistory() {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        Call<List<UserBookingHistoryDto>> call = bookingApi.getBookingHistory(userId, currentPage, PAGE_SIZE);
        Log.d("BookingHistory", "Calling API for userId: " + userId + ", page: " + currentPage + ", pageSize: " + PAGE_SIZE);
        call.enqueue(new Callback<List<UserBookingHistoryDto>>() {
            @Override
            public void onResponse(Call<List<UserBookingHistoryDto>> call, Response<List<UserBookingHistoryDto>> response) {
                progressBar.setVisibility(View.GONE);
                Log.d("BookingHistory", "API Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    List<UserBookingHistoryDto> bookings = response.body();
                    Log.d("BookingHistory", "Bookings count: " + bookings.size());
                    bookingList.addAll(bookings);
                    filterBookings(etSearch.getText().toString());
                } else {
                    Log.d("BookingHistory", "Response failed: " + response.message());
                    Toast.makeText(BookingHistoryActivity.this, "Failed to load bookings: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserBookingHistoryDto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("BookingHistory", "API call failed: " + t.getMessage());
                Toast.makeText(BookingHistoryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        emptyState.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        rvBookingHistory.setVisibility(filteredList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // Adapter for RecyclerView
    private static class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

        private final List<UserBookingHistoryDto> bookings;
        private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MMM dd, yyyy, HH:mm", Locale.getDefault());
        private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        BookingHistoryAdapter(List<UserBookingHistoryDto> bookings) {
            this.bookings = bookings;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_booking_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            UserBookingHistoryDto booking = bookings.get(position);

            // Status Header
            String status = booking.getBookingStatus();
            holder.tvStatus.setText(getStatusText(status));
            holder.statusLayout.setBackgroundColor(getStatusColor(holder.itemView.getContext(), status));
            holder.tvBookingId.setText("Booking ID: " + booking.getBookingReference());

            // Airline Info
            holder.tvAirline.setText(booking.getAirlineName());
            holder.tvFlightDetails.setText("Flight " + booking.getFlightNumber());
            holder.tvPrice.setText(String.format(Locale.getDefault(), "$%.2f", booking.getTotalAmount()));

            // Route Info
            String[] routeParts = booking.getRoute().split(" to ");
            holder.tvFromCity.setText(routeParts.length > 0 ? routeParts[0] : "Unknown");
            holder.tvToCity.setText(routeParts.length > 1 ? routeParts[1] : "Unknown");
            holder.tvFromCode.setText(routeParts.length > 0 ? routeParts[0].substring(0, 3).toUpperCase() : "UNK");
            holder.tvToCode.setText(routeParts.length > 1 ? routeParts[1].substring(0, 3).toUpperCase() : "UNK");
            holder.tvFromTime.setText(timeFormat.format(booking.getDepartureTime()));
            holder.tvToTime.setText(timeFormat.format(booking.getArrivalTime()));
            holder.tvDate.setText(dateTimeFormat.format(booking.getBookingDate()));

            // Duration
            long durationMillis = booking.getArrivalTime().getTime() - booking.getDepartureTime().getTime();
            long hours = durationMillis / (1000 * 60 * 60);
            long minutes = (durationMillis / (1000 * 60)) % 60;
            holder.tvDuration.setText(String.format(Locale.getDefault(), "%dh %dm", hours, minutes));

            // Action Buttons
            holder.btnViewDetail.setText(status.equalsIgnoreCase("Cancelled") ? "View Details" : "View Details");
            holder.btnCheckin.setText(status.equalsIgnoreCase("Confirmed") ? "Check-in" :
                    status.equalsIgnoreCase("Completed") ? "Book Again" : "View Details");
            holder.btnCheckin.setVisibility(status.equalsIgnoreCase("Cancelled") ? View.GONE : View.VISIBLE);
            holder.btnViewDetail.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), BookingDetailActivity.class);
                intent.putExtra("bookingId", booking.getBookingId()); // đảm bảo UserBookingHistoryDto có phương thức getBookingId()
                holder.itemView.getContext().startActivity(intent);
            });
            // Adjust alpha for cancelled bookings
            holder.itemView.setAlpha(status.equalsIgnoreCase("Cancelled") ? 0.7f : 1.0f);
        }

        private String getStatusText(String status) {
            if (status.equalsIgnoreCase("Confirmed")) return "✅ Confirmed";
            if (status.equalsIgnoreCase("Completed")) return "✈️ Completed";
            if (status.equalsIgnoreCase("Cancelled")) return "❌ Cancelled";
            return status;
        }

        private int getStatusColor(android.content.Context context, String status) {
            if (status.equalsIgnoreCase("Confirmed")) return context.getResources().getColor(R.color.green_100);
            if (status.equalsIgnoreCase("Completed")) return context.getResources().getColor(R.color.blue_100);
            if (status.equalsIgnoreCase("Cancelled")) return context.getResources().getColor(R.color.red_100);
            return context.getResources().getColor(R.color.grey_100);
        }

        @Override
        public int getItemCount() {
            return bookings.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout statusLayout;
            TextView tvStatus, tvBookingId, tvAirline, tvFlightDetails, tvPrice;
            TextView tvFromCity, tvFromCode, tvFromTime, tvToCity, tvToCode, tvToTime;
            TextView tvDate, tvDuration;
            Button btnViewDetail, btnCheckin;

            ViewHolder(View itemView) {
                super(itemView);
                statusLayout = itemView.findViewById(R.id.status_layout);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvBookingId = itemView.findViewById(R.id.tv_booking_id);
                tvAirline = itemView.findViewById(R.id.tv_airline);
                tvFlightDetails = itemView.findViewById(R.id.tv_flight_details);
                tvPrice = itemView.findViewById(R.id.tv_price);
                tvFromCity = itemView.findViewById(R.id.tv_from_city);
                tvFromCode = itemView.findViewById(R.id.tv_from_code);
                tvFromTime = itemView.findViewById(R.id.tv_from_time);
                tvToCity = itemView.findViewById(R.id.tv_to_city);
                tvToCode = itemView.findViewById(R.id.tv_to_code);
                tvToTime = itemView.findViewById(R.id.tv_to_time);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvDuration = itemView.findViewById(R.id.tv_duration);
                btnViewDetail = itemView.findViewById(R.id.btn_view_detail);
                btnCheckin = itemView.findViewById(R.id.btn_check_in);
            }
        }
    }
}