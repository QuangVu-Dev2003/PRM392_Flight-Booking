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

import java.math.BigDecimal;
import java.text.NumberFormat;
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
    private LinearLayout searchFilterContainer;
    private Button btnBookFlight;

    private BookingApiEndpoint bookingApi;
    private BookingHistoryAdapter adapter;
    private List<UserBookingHistoryDto> bookingList, filteredList;
    private int userId;
    private int currentPage = 1;
    private final int PAGE_SIZE = 10;
    private String currentFilter = "Tất cả";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        // Khởi tạo API service
        bookingApi = ApiServiceProvider.getBookingApi();

        // Lấy userId từ SharedPreferences
        if (!getUserIdFromPreferences()) {
            return;
        }

        bindingView();
        bindingAction();
        setupRecyclerView();
        fetchBookingHistory();
    }

    private boolean getUserIdFromPreferences() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        Log.d("BookingHistory", "Retrieved userId: " + userId);

        if (userId == -1) {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        return true;
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
        btnBookFlight = findViewById(R.id.btn_book_flight);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(this::onBtnBackClick);
        btnFilter.setOnClickListener(this::onBtnFilterClick);
        btnBookFlight.setOnClickListener(this::onBtnBookFlightClick);
        chipAll.setOnClickListener(this::onChipAllClick);
        chipUpcoming.setOnClickListener(this::onChipUpcomingClick);
        chipCompleted.setOnClickListener(this::onChipCompletedClick);
        chipCancelled.setOnClickListener(this::onChipCancelledClick);

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
    }

    private void onBtnBackClick(View view) {
        finish();
    }

    // Hiển/ẩn bộ lọc tìm kiếm
    private void onBtnFilterClick(View view) {
        toggleSearchFilter();
    }

    // Chuyển sang màn hình đặt vé
    private void onBtnBookFlightClick(View view) {
        Intent intent = new Intent(this, BookingActivity.class);
        startActivity(intent);
    }

    // Lọc tất cả
    private void onChipAllClick(View view) {
        setFilter("Tất cả");
    }

    // Lọc chuyến bay sắp tới
    private void onChipUpcomingClick(View view) {
        setFilter("Sắp tới");
    }

    // Lọc chuyến bay đã hoàn thành
    private void onChipCompletedClick(View view) {
        setFilter("Đã hoàn thành");
    }

    // Lọc chuyến bay đã hủy
    private void onChipCancelledClick(View view) {
        setFilter("Đã hủy");
    }

    // Hiển/ẩn container tìm kiếm và lọc
    private void toggleSearchFilter() {
        if (searchFilterContainer.getVisibility() == View.VISIBLE) {
            searchFilterContainer.setVisibility(View.GONE);
        } else {
            searchFilterContainer.setVisibility(View.VISIBLE);
        }
    }

    /*
     - Khởi tạo RecyclerView
     - Thiết lập adapter và layout manager
     */
    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new BookingHistoryAdapter(filteredList);
        rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));
        rvBookingHistory.setAdapter(adapter);
    }

    /*
     - Thiết lập filter theo trạng thái
     - Cập nhật UI cho các chip filter
     */
    private void setFilter(String filter) {
        currentFilter = filter;
        updateChipStyles();
        filterBookings(etSearch.getText().toString());
    }

    // Cập nhật màu sắc và trạng thái của các chip filter
    private void updateChipStyles() {
        int textColor = getResources().getColor(R.color.white);
        int inactiveTextColor = getResources().getColor(R.color.grey_600);

        TextView tvAll = chipAll.findViewById(R.id.tv_chip_all);
        TextView tvUpcoming = chipUpcoming.findViewById(R.id.tv_chip_upcoming);
        TextView tvCompleted = chipCompleted.findViewById(R.id.tv_chip_completed);
        TextView tvCancelled = chipCancelled.findViewById(R.id.tv_chip_cancelled);

        chipAll.setCardBackgroundColor(getResources().getColor(currentFilter.equals("Tất cả") ? R.color.blue_500 : R.color.grey_200));
        chipUpcoming.setCardBackgroundColor(getResources().getColor(currentFilter.equals("Sắp tới") ? R.color.blue_500 : R.color.grey_200));
        chipCompleted.setCardBackgroundColor(getResources().getColor(currentFilter.equals("Đã hoàn thành") ? R.color.blue_500 : R.color.grey_200));
        chipCancelled.setCardBackgroundColor(getResources().getColor(currentFilter.equals("Đã hủy") ? R.color.blue_500 : R.color.grey_200));

        if (tvAll != null) tvAll.setTextColor(currentFilter.equals("Tất cả") ? textColor : inactiveTextColor);
        if (tvUpcoming != null) tvUpcoming.setTextColor(currentFilter.equals("Sắp tới") ? textColor : inactiveTextColor);
        if (tvCompleted != null) tvCompleted.setTextColor(currentFilter.equals("Đã hoàn thành") ? textColor : inactiveTextColor);
        if (tvCancelled != null) tvCancelled.setTextColor(currentFilter.equals("Đã hủy") ? textColor : inactiveTextColor);
    }

    /*
     - Lọc danh sách booking theo filter và search query
     - Cập nhật adapter và empty state
     */
    private void filterBookings(String query) {
        filteredList.clear();
        for (UserBookingHistoryDto booking : bookingList) {
            boolean matchesFilter = currentFilter.equals("Tất cả") ||
                    (currentFilter.equals("Sắp tới") && booking.getBookingStatus().equalsIgnoreCase("Confirmed")) ||
                    (currentFilter.equals("Đã hoàn thành") && booking.getBookingStatus().equalsIgnoreCase("Completed")) ||
                    (currentFilter.equals("Đã hủy") && booking.getBookingStatus().equalsIgnoreCase("Cancelled"));

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

    /*
     - Gọi API để lấy lịch sử đặt vé
     - Xử lý response và cập nhật UI
     */
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
                    // Xử lý lỗi từ server
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<UserBookingHistoryDto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("BookingHistory", "API call failed: " + t.getMessage());
                Toast.makeText(BookingHistoryActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xử lý các lỗi response từ server
    private void handleErrorResponse(Response<List<UserBookingHistoryDto>> response) {
        try {
            String errorMessage = "Không thể tải lịch sử đặt vé";

            if (response.code() == 401) {
                errorMessage = "Phiên đăng nhập đã hết hạn";
            } else if (response.code() == 400) {
                errorMessage = "Yêu cầu không hợp lệ";
            } else if (response.code() >= 500) {
                errorMessage = "Lỗi server, vui lòng thử lại sau";
            }

            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            Log.d("BookingHistory", "Response failed: " + response.message());

        } catch (Exception e) {
            Toast.makeText(this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     - Cập nhật trạng thái empty state
     - Hiển thị/ẩn RecyclerView và empty state
     */
    private void updateEmptyState() {
        emptyState.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        rvBookingHistory.setVisibility(filteredList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // Adapter cho RecyclerView
    private static class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder> {

        private final List<UserBookingHistoryDto> bookings;
        private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy, HH:mm", Locale.getDefault());
        private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

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

            // Hiển thị trạng thái booking
            String status = booking.getBookingStatus();
            holder.tvStatus.setText(getStatusText(status));
            holder.statusLayout.setBackgroundColor(getStatusColor(holder.itemView.getContext(), status));
            holder.tvBookingId.setText("Mã đặt vé: " + booking.getBookingReference());

            // Thông tin hãng bay
            holder.tvAirline.setText(booking.getAirlineName());
            holder.tvFlightDetails.setText("Chuyến bay " + booking.getFlightNumber());

            // Xử lý giá tiền theo định dạng VND
            BigDecimal totalAmount = booking.getTotalAmount();
            String formattedPrice = currencyFormat.format(totalAmount);
            holder.tvPrice.setText(formattedPrice);

            // Thông tin tuyến đường
            String route = booking.getRoute().replace("→", "to").trim();
            String[] routeParts = route.split(" to ");
            holder.tvFromCity.setText(routeParts.length > 0 ? routeParts[0].trim() : "Không xác định");
            holder.tvToCity.setText(routeParts.length > 1 ? routeParts[1].trim() : "Không xác định");
            holder.tvFromCode.setText(routeParts.length > 0 ? routeParts[0].substring(0, 3).toUpperCase() : "Không xác định");
            holder.tvToCode.setText(routeParts.length > 1 ? routeParts[1].substring(0, 3).toUpperCase() : "Không xác định");
            holder.tvFromTime.setText(timeFormat.format(booking.getDepartureTime()));
            holder.tvToTime.setText(timeFormat.format(booking.getArrivalTime()));
            holder.tvDate.setText(dateTimeFormat.format(booking.getBookingDate()));

            // Thời gian bay
            long durationMillis = booking.getArrivalTime().getTime() - booking.getDepartureTime().getTime();
            long hours = durationMillis / (1000 * 60 * 60);
            long minutes = (durationMillis / (1000 * 60)) % 60;
            holder.tvDuration.setText(String.format(Locale.getDefault(), "%dg %dm", hours, minutes));

            // Các nút hành động
            holder.btnViewDetail.setText(status.equalsIgnoreCase("Cancelled") ? "Xem chi tiết" : "Xem chi tiết");
            holder.btnCheckin.setText(status.equalsIgnoreCase("Confirmed") ? "Check-in" :
                    status.equalsIgnoreCase("Completed") ? "Đặt lại" : "Xem chi tiết");
            holder.btnCheckin.setVisibility(status.equalsIgnoreCase("Cancelled") ? View.GONE : View.VISIBLE);

            holder.btnViewDetail.setOnClickListener(v -> {
                Intent intent = new Intent(holder.itemView.getContext(), BookingDetailActivity.class);
                intent.putExtra("bookingId", booking.getBookingId());
                holder.itemView.getContext().startActivity(intent);
            });

            // OnClickListener cho nút "Check-in"
            holder.btnCheckin.setOnClickListener(v -> {
                if (status.equalsIgnoreCase("Confirmed")) {
                    // Chuyển sang PayActivity khi check-in
                    Intent intent = new Intent(holder.itemView.getContext(), PayActivity.class);
                    intent.putExtra("bookingId", booking.getBookingId());
                    intent.putExtra("bookingReference", booking.getBookingReference());
                    intent.putExtra("totalAmount", booking.getTotalAmount().doubleValue());
                    intent.putExtra("flightNumber", booking.getFlightNumber());
                    intent.putExtra("route", booking.getRoute());
                    holder.itemView.getContext().startActivity(intent);
                } else if (status.equalsIgnoreCase("Completed")) {
                    // Chuyển sang BookingActivity để đặt lại
                    Intent intent = new Intent(holder.itemView.getContext(), BookingActivity.class);
                    holder.itemView.getContext().startActivity(intent);
                } else {
                    // Xem chi tiết cho các trạng thái khác
                    Intent intent = new Intent(holder.itemView.getContext(), BookingDetailActivity.class);
                    intent.putExtra("bookingId", booking.getBookingId());
                    holder.itemView.getContext().startActivity(intent);
                }
            });

            // Điều chỉnh độ mờ cho booking đã hủy
            holder.itemView.setAlpha(status.equalsIgnoreCase("Cancelled") ? 0.7f : 1.0f);
        }

        private String getStatusText(String status) {
            if (status.equalsIgnoreCase("Confirmed")) return "✅ Đã xác nhận";
            if (status.equalsIgnoreCase("Completed")) return "✈️ Đã hoàn thành";
            if (status.equalsIgnoreCase("Cancelled")) return "❌ Đã hủy";
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