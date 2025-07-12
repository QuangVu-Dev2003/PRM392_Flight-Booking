package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.FlightApiEndpoint;
import com.prm.flightbooking.dto.seat.SeatDto;
import com.prm.flightbooking.dto.seat.SeatMapDto;
import com.prm.flightbooking.dto.seat.SelectedSeatInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChooseSeatsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnConfirmSeats;
    private LinearLayout llBusinessSeats, llEconomySeats;
    private TextView tvSelectedSeats, tvTotalPrice, tvNumSelectedSeats;
    private ProgressBar progressBar;

    private FlightApiEndpoint flightApi;
    private SharedPreferences sharedPreferences;
    private Map<Integer, SelectedSeatInfo> selectedSeatsInfoMap;
    private int flightId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_seats);

        // Khởi tạo API và SharedPreferences
        flightApi = ApiServiceProvider.getFlightApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Khởi tạo Map lưu thông tin ghế đã chọn
        selectedSeatsInfoMap = new HashMap<>();

        // Kiểm tra thông tin đăng nhập và flight ID
        if (!validateUserAndFlight()) {
            return;
        }

        bindingView();
        bindingAction();
        setupBackPressHandler();

        // Tải bản đồ ghế từ server
        fetchSeatMap();
    }

    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        btnConfirmSeats = findViewById(R.id.btn_confirm_seats);
        llBusinessSeats = findViewById(R.id.ll_business_seats);
        llEconomySeats = findViewById(R.id.ll_economy_seats);
        tvSelectedSeats = findViewById(R.id.tv_selected_seats);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvNumSelectedSeats = findViewById(R.id.tv_num_selected_seats);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(this::onBtnBackClick);
        btnConfirmSeats.setOnClickListener(this::onBtnConfirmSeatsClick);
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });
    }

    // Kiểm tra thông tin người dùng và chuyến bay
    private boolean validateUserAndFlight() {
        // Lấy flight ID từ Intent
        flightId = getIntent().getIntExtra("flightId", -1);
        if (flightId == -1) {
            Toast.makeText(this, "Mã chuyến bay không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }

        // Lưu flight ID vào SharedPreferences
        sharedPreferences.edit().putInt("flightId", flightId).apply();

        // Lấy user ID từ SharedPreferences
        userId = sharedPreferences.getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return false;
        }

        return true;
    }

    // Quay lại màn hình trước
    private void onBtnBackClick(View view) {
        showExitDialog();
    }

    // Xác nhận chọn ghế và chuyển sang màn hình đặt vé
    private void onBtnConfirmSeatsClick(View view) {
        proceedToBooking();
    }

    // Tải bản đồ ghế từ server
    private void fetchSeatMap() {
        progressBar.setVisibility(View.VISIBLE);

        Call<SeatMapDto> call = flightApi.getSeatMap(flightId, userId);
        call.enqueue(new Callback<SeatMapDto>() {
            @Override
            public void onResponse(Call<SeatMapDto> call, Response<SeatMapDto> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    displaySeatMap(response.body());
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<SeatMapDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChooseSeatsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hiển thị bản đồ ghế trên giao diện
    private void displaySeatMap(SeatMapDto seatMap) {
        // Xóa các view cũ
        llBusinessSeats.removeAllViews();
        llEconomySeats.removeAllViews();

        // Nhóm ghế theo hàng
        Map<Integer, List<SeatDto>> seatsByRow = groupSeatsByRow(seatMap.getSeats());

        // Tạo giao diện cho từng hàng ghế
        createSeatRows(seatsByRow);

        // Cập nhật thông tin ghế đã chọn
        updateSelectionInfo();
    }

    // Nhóm ghế theo hàng
    private Map<Integer, List<SeatDto>> groupSeatsByRow(List<SeatDto> seats) {
        Map<Integer, List<SeatDto>> seatsByRow = new HashMap<>();
        for (SeatDto seat : seats) {
            seatsByRow.computeIfAbsent(seat.getSeatRow(), k -> new ArrayList<>()).add(seat);
        }
        return seatsByRow;
    }

    // Tạo giao diện cho các hàng ghế
    private void createSeatRows(Map<Integer, List<SeatDto>> seatsByRow) {
        List<Integer> sortedRows = new ArrayList<>(seatsByRow.keySet());
        sortedRows.sort(null);

        for (int row : sortedRows) {
            List<SeatDto> rowSeats = seatsByRow.get(row);
            if (rowSeats == null) continue;

            // Sắp xếp ghế theo cột
            rowSeats.sort((s1, s2) -> s1.getSeatColumn().compareTo(s2.getSeatColumn()));

            // Tạo layout cho hàng ghế
            LinearLayout rowLayout = createRowLayout();

            // Thêm ghế vào hàng
            addSeatsToRow(rowLayout, rowSeats);

            // Thêm hàng vào layout phù hợp
            addRowToLayout(rowLayout, rowSeats.get(0));
        }
    }

    // Tạo layout cho một hàng ghế
    private LinearLayout createRowLayout() {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 8);
        rowLayout.setLayoutParams(params);

        return rowLayout;
    }

    // Thêm ghế vào hàng
    private void addSeatsToRow(LinearLayout rowLayout, List<SeatDto> rowSeats) {
        for (int i = 0; i < rowSeats.size(); i++) {
            SeatDto seat = rowSeats.get(i);
            TextView seatView = createSeatView(seat, i > 0);
            rowLayout.addView(seatView);

            // Thêm khoảng trống giữa các nhóm ghế
            if (rowSeats.size() > 3 && i == 2) {
                Space space = new Space(this);
                space.setLayoutParams(new LinearLayout.LayoutParams(20, LinearLayout.LayoutParams.MATCH_PARENT));
                rowLayout.addView(space);
            }
        }
    }

    // Tạo view cho một ghế
    private TextView createSeatView(SeatDto seat, boolean addMargin) {
        TextView seatView = new TextView(this);
        seatView.setText(seat.getSeatNumber());

        // Thiết lập layout params
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(addMargin ? 8 : 0, 0, 0, 0);
        seatView.setLayoutParams(params);

        // Thiết lập giao diện ghế
        setupSeatAppearance(seatView, seat);

        // Thiết lập sự kiện click
        seatView.setOnClickListener(v -> onSeatClick(seat, seatView));

        return seatView;
    }

    // Thiết lập giao diện cho ghế
    private void setupSeatAppearance(TextView seatView, SeatDto seat) {
        // Thiết lập màu nền dựa trên trạng thái ghế
        if (seat.isBookedByCurrentUser()) {
            seatView.setBackgroundResource(R.drawable.seat_selected);
        } else if (!seat.isAvailable()) {
            seatView.setBackgroundResource(R.drawable.seat_unavailable);
        } else {
            seatView.setBackgroundResource(R.drawable.seat_available);
        }

        // Thiết lập style text
        seatView.setTextColor(getResources().getColor(android.R.color.white));
        seatView.setPadding(24, 16, 24, 16);
        seatView.setGravity(Gravity.CENTER);
        seatView.setTextSize(14);
    }

    // Thêm hàng ghế vào layout phù hợp
    private void addRowToLayout(LinearLayout rowLayout, SeatDto firstSeat) {
        boolean isBusinessClass = firstSeat.getSeatClassName().equalsIgnoreCase("Business") ||
                firstSeat.getSeatClassName().equalsIgnoreCase("FIRST_CLASS");
        LinearLayout targetLayout = isBusinessClass ? llBusinessSeats : llEconomySeats;
        targetLayout.addView(rowLayout);
    }

    // Xử lý khi click vào ghế
    private void onSeatClick(SeatDto seat, TextView seatView) {
        if (!seat.isAvailable()) {
            Toast.makeText(this, "Ghế " + seat.getSeatNumber() + " không khả dụng", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedSeatsInfoMap.containsKey(seat.getSeatId())) {
            // Hủy chọn ghế
            unselectSeat(seat.getSeatId(), seatView);
        } else {
            // Chọn ghế mới
            showPassengerInfoDialog(seat, seatView);
        }
    }

    // Hủy chọn ghế
    private void unselectSeat(int seatId, TextView seatView) {
        selectedSeatsInfoMap.remove(seatId);
        seatView.setBackgroundResource(R.drawable.seat_available);
        updateSelectionInfo();
    }

    // Hiển thị dialog nhập thông tin hành khách
    private void showPassengerInfoDialog(SeatDto seat, TextView seatView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông tin hành khách - Ghế " + seat.getSeatNumber());

        // Tạo layout cho dialog
        LinearLayout layout = createDialogLayout();

        // Tạo các trường nhập liệu
        final EditText inputName = createInputField("Tên hành khách");
        final EditText inputIdNumber = createIdNumberField();

        layout.addView(inputName);
        layout.addView(inputIdNumber);
        builder.setView(layout);

        // Thiết lập các nút
        builder.setPositiveButton("Xác nhận", (dialog, which) -> {
            processPassengerInfo(seat, seatView, inputName, inputIdNumber);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Tạo layout cho dialog
    private LinearLayout createDialogLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        return layout;
    }

    // Tạo trường nhập tên
    private EditText createInputField(String hint) {
        EditText editText = new EditText(this);
        editText.setHint(hint);
        return editText;
    }

    // Tạo trường nhập số CMND
    private EditText createIdNumberField() {
        EditText editText = new EditText(this);
        editText.setHint("Số CMND/CCCD (9-12 số)");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        return editText;
    }

    // Xử lý thông tin hành khách
    private void processPassengerInfo(SeatDto seat, TextView seatView, EditText inputName, EditText inputIdNumber) {
        String name = inputName.getText().toString().trim();
        String idNumber = inputIdNumber.getText().toString().trim();

        // Kiểm tra thông tin đầu vào
        if (!validatePassengerInfo(name, idNumber)) {
            showPassengerInfoDialog(seat, seatView);
            return;
        }

        // Lưu thông tin ghế đã chọn
        saveSelectedSeat(seat, name, idNumber);

        // Cập nhật giao diện
        seatView.setBackgroundResource(R.drawable.seat_selected);
        updateSelectionInfo();
    }

    // Kiểm tra thông tin hành khách
    private boolean validatePassengerInfo(String name, String idNumber) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Tên hành khách không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(idNumber) || !idNumber.matches("\\d{9,12}")) {
            Toast.makeText(this, "Số CMND/CCCD phải có 9-12 số", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Lưu thông tin ghế đã chọn
    private void saveSelectedSeat(SeatDto seat, String name, String idNumber) {
        SelectedSeatInfo info = new SelectedSeatInfo(
                seat.getSeatId(),
                seat.getSeatNumber(),
                seat.getSeatClassName(),
                seat.getTotalPrice() != null ? seat.getTotalPrice() : BigDecimal.ZERO
        );
        info.setPassengerName(name);
        info.setPassengerIdNumber(idNumber);
        selectedSeatsInfoMap.put(seat.getSeatId(), info);
    }

    // Cập nhật thông tin ghế đã chọn
    private void updateSelectionInfo() {
        List<String> seatNumbers = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (SelectedSeatInfo info : selectedSeatsInfoMap.values()) {
            seatNumbers.add(info.getSeatNumber());
            if (info.getTotalPrice() != null) {
                totalPrice = totalPrice.add(info.getTotalPrice());
            }
        }

        // Cập nhật UI
        updateSelectedSeatsDisplay(seatNumbers);
        updateTotalPriceDisplay(totalPrice);
        updateSelectedSeatsCount();
    }

    // Cập nhật hiển thị danh sách ghế
    private void updateSelectedSeatsDisplay(List<String> seatNumbers) {
        String selectedSeatsText = String.join(", ", seatNumbers);
        tvSelectedSeats.setText(selectedSeatsText.isEmpty() ? "Chưa chọn ghế nào" : selectedSeatsText);
    }

    // Cập nhật hiển thị tổng giá
    private void updateTotalPriceDisplay(BigDecimal totalPrice) {
        String formatted = String.format("%,d", totalPrice.setScale(0, BigDecimal.ROUND_HALF_UP).intValue());
        tvTotalPrice.setText(formatted + " VNĐ");
    }

    // Cập nhật số lượng ghế đã chọn
    private void updateSelectedSeatsCount() {
        if (tvNumSelectedSeats != null) {
            tvNumSelectedSeats.setText(selectedSeatsInfoMap.size() + " ghế đã chọn");
        }
    }

    // Chuyển sang màn hình đặt vé
    private void proceedToBooking() {
        if (selectedSeatsInfoMap.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một ghế", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển sang màn hình đặt vé
        ArrayList<SelectedSeatInfo> seatsToBook = new ArrayList<>(selectedSeatsInfoMap.values());
        Intent intent = new Intent(this, BookingFormActivity.class);
        intent.putExtra("flightId", flightId);
        intent.putExtra("selectedSeatsList", seatsToBook);
        startActivity(intent);
    }

    // Xử lý lỗi từ server
    private void handleErrorResponse(Response<SeatMapDto> response) {
        String errorMessage = "Không thể tải bản đồ ghế";

        if (response.code() == 400) {
            errorMessage = "Yêu cầu không hợp lệ";
        } else if (response.code() == 401) {
            errorMessage = "Phiên đăng nhập hết hạn";
            redirectToLogin();
            return;
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy chuyến bay";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Chuyển về màn hình đăng nhập
    private void redirectToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showExitDialog() {
        // Hiện dialog hỏi người dùng có muốn thoát không
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thoát")
                .setMessage("Bạn có muốn thoát không? Thông tin chưa lưu sẽ bị mất.")
                .setPositiveButton("Có", (dialog, which) -> finish())
                .setNegativeButton("Không", (dialog, which) -> {
                    // Nếu chọn không thì đóng dialog, không thoát
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Kiểm tra trạng thái đăng nhập khi quay lại màn hình
        if (!sharedPreferences.getBoolean("is_logged_in", false)) {
            redirectToLogin();
        }
    }
}