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
    private TextView tvSelectedSeats, tvTotalPrice;
    private ProgressBar progressBar;
    private FlightApiEndpoint flightApi;
    private Map<Integer, SelectedSeatInfo> selectedSeatsInfoMap;
    private int flightId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_seats);

        // Khởi tạo API service để gọi API
        flightApi = ApiServiceProvider.getFlightApi();

        // Lấy flightId từ Intent
        flightId = getIntent().getIntExtra("flightId", -1);
        if (flightId == -1) {
            Toast.makeText(this, "ID chuyến bay không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lưu flightId vào SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("flightId", flightId);
        editor.apply();

        // Khởi tạo Map để lưu thông tin ghế đã chọn
        selectedSeatsInfoMap = new HashMap<>();

        bindingView();
        bindingAction();

        // Lấy bản đồ ghế
        performFetchSeatMap();
    }

    private void bindingView() {
        btnBack = findViewById(R.id.btn_back);
        btnConfirmSeats = findViewById(R.id.btn_confirm_seats);
        llBusinessSeats = findViewById(R.id.ll_business_seats);
        llEconomySeats = findViewById(R.id.ll_economy_seats);
        tvSelectedSeats = findViewById(R.id.tv_selected_seats);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void bindingAction() {
        btnBack.setOnClickListener(this::onBtnBackClick);
        btnConfirmSeats.setOnClickListener(this::onBtnConfirmSeatsClick);
    }

    private void onBtnBackClick(View view) {
        // Quay lại màn hình trước đó
        finish();
    }

    private void onBtnConfirmSeatsClick(View view) {
        // Chuyển sang màn hình đặt vé
        proceedToBooking();
    }

    // Gọi API lấy bản đồ ghế
    private void performFetchSeatMap() {
        // Hiển thị ProgressBar khi đang tải dữ liệu
        progressBar.setVisibility(View.VISIBLE);

        // Gọi API
        Call<SeatMapDto> call = flightApi.getSeatMap(flightId);
        call.enqueue(new Callback<SeatMapDto>() {
            @Override
            public void onResponse(Call<SeatMapDto> call, Response<SeatMapDto> response) {
                // Ẩn ProgressBar sau khi nhận được phản hồi
                progressBar.setVisibility(View.GONE);

                // Kiểm tra xem API có trả về dữ liệu thành công không
                if (response.isSuccessful() && response.body() != null) {
                    // Hiển thị bản đồ ghế
                    displaySeatMap(response.body());
                } else {
                    // Xử lý lỗi nếu API trả về không thành công
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<SeatMapDto> call, Throwable t) {
                // Ẩn ProgressBar khi có lỗi mạng
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChooseSeatsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hiển thị bản đồ ghế trên giao diện
    private void displaySeatMap(SeatMapDto seatMap) {
        // Xóa các view cũ trong layout ghế
        llBusinessSeats.removeAllViews();
        llEconomySeats.removeAllViews();

        // Nhóm các ghế theo hàng
        Map<Integer, List<SeatDto>> seatsByRow = new HashMap<>();
        for (SeatDto seat : seatMap.getSeats()) {
            seatsByRow.computeIfAbsent(seat.getSeatRow(), k -> new ArrayList<>()).add(seat);
        }

        // Lấy danh sách các hàng đã sắp xếp
        List<Integer> sortedRows = new ArrayList<>(seatsByRow.keySet());
        sortedRows.sort(null); // Sắp xếp tăng dần theo số hàng

        for (int row : sortedRows) {
            List<SeatDto> rowSeats = seatsByRow.get(row);
            if (rowSeats == null) continue; // Đảm bảo không null
            rowSeats.sort((s1, s2) -> s1.getSeatColumn().compareTo(s2.getSeatColumn()));

            // Tạo layout cho mỗi hàng ghế
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 0, 0, 8); // Đặt margin cố định giữa các hàng
            rowLayout.setLayoutParams(rowParams);

            for (int i = 0; i < rowSeats.size(); i++) {
                SeatDto seat = rowSeats.get(i);
                TextView seatView = new TextView(this);
                seatView.setText(seat.getSeatNumber());
                LinearLayout.LayoutParams seatParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                // Thêm margin giữa các ghế
                seatParams.setMargins(i > 0 ? (seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ? 8 : 4) : 0, 0, 0, 0);
                seatView.setLayoutParams(seatParams);

                // Kiểm tra xem ghế đã được chọn trước đó chưa
                boolean isSelected = selectedSeatsInfoMap.containsKey(seat.getSeatId());

                // Thiết lập style cho ghế dựa trên trạng thái
                int styleResId = seat.isAvailable() ?
                        (isSelected ?
                                (seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ?
                                        R.style.SeatButtonBusiness_Selected : R.style.SeatButtonEconomy_Selected) :
                                (seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ?
                                        R.style.SeatButtonBusiness : R.style.SeatButtonEconomy)) :
                        (seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ?
                                R.style.SeatButtonBusiness_Unavailable : R.style.SeatButtonEconomy_Unavailable);
                seatView.setTextAppearance(styleResId);

                // Gán sự kiện click cho ghế
                seatView.setOnClickListener(v -> onSeatClick(seat, seatView));

                rowLayout.addView(seatView);

                // Thêm khoảng trống giữa các nhóm ghế
                if (rowSeats.size() > 3 && i == 2) {
                    Space space = new Space(this);
                    space.setLayoutParams(new LinearLayout.LayoutParams(
                            seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ? 32 : 20,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    rowLayout.addView(space);
                }
            }

            // Thêm hàng ghế vào layout tương ứng (Business hoặc Economy)
            SeatDto firstSeat = rowSeats.get(0);
            LinearLayout targetLayout = (firstSeat.getSeatClassName().equals("Business") || firstSeat.getSeatClassName().equals("FIRST_CLASS")) ?
                    llBusinessSeats : llEconomySeats;
            targetLayout.addView(rowLayout);
        }

        // Cập nhật thông tin ghế đã chọn
        updateSelectionInfo();
    }

    // Xử lý khi click vào một ghế
    private void onSeatClick(SeatDto seat, TextView seatView) {
        if (seat.isAvailable()) {
            if (selectedSeatsInfoMap.containsKey(seat.getSeatId())) {
                // Ghế đã chọn, hủy chọn và xóa thông tin
                selectedSeatsInfoMap.remove(seat.getSeatId());
                seatView.setTextAppearance(seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ?
                        R.style.SeatButtonBusiness : R.style.SeatButtonEconomy);
                updateSelectionInfo();
            } else {
                // Ghế chưa chọn, hiển thị dialog nhập thông tin hành khách
                showPassengerInfoDialog(seat, seatView);
            }
        } else {
            Toast.makeText(this, "Ghế " + seat.getSeatNumber() + " không khả dụng.", Toast.LENGTH_SHORT).show();
        }
    }

    // Hiển thị dialog để nhập thông tin hành khách
    private void showPassengerInfoDialog(SeatDto seat, TextView seatView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nhập thông tin hành khách cho ghế " + seat.getSeatNumber());

        // Tạo layout cho dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        // Tạo trường nhập tên hành khách
        final EditText inputName = new EditText(this);
        inputName.setHint("Tên hành khách");
        layout.addView(inputName);

        // Tạo trường nhập số CMND/CCCD
        final EditText inputIdNumber = new EditText(this);
        inputIdNumber.setHint("Số CMND/CCCD (9-12 số)");
        inputIdNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputIdNumber);

        builder.setView(layout);

        // Nút OK trong dialog
        builder.setPositiveButton("OK", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String idNumber = inputIdNumber.getText().toString().trim();

            // Kiểm tra xem tên có rỗng không
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Tên hành khách không được để trống.", Toast.LENGTH_SHORT).show();
                showPassengerInfoDialog(seat, seatView); // Hiển thị lại dialog
                return;
            }
            // Kiểm tra số CMND/CCCD
            if (TextUtils.isEmpty(idNumber) || !idNumber.matches("\\d{9,12}")) {
                Toast.makeText(this, "Số CMND/CCCD phải có 9-12 số.", Toast.LENGTH_SHORT).show();
                showPassengerInfoDialog(seat, seatView); // Hiển thị lại dialog
                return;
            }

            // Lưu thông tin ghế đã chọn
            SelectedSeatInfo info = new SelectedSeatInfo(
                    seat.getSeatId(),
                    seat.getSeatNumber(),
                    seat.getSeatClassName(),
                    seat.getTotalPrice() != null ? seat.getTotalPrice() : BigDecimal.ZERO
            );
            info.setPassengerName(name);
            info.setPassengerIdNumber(idNumber);
            selectedSeatsInfoMap.put(seat.getSeatId(), info);

            // Cập nhật giao diện ghế thành đã chọn
            seatView.setTextAppearance(seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ?
                    R.style.SeatButtonBusiness_Selected : R.style.SeatButtonEconomy_Selected);
            updateSelectionInfo();
        });

        // Nút Cancel trong dialog
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Cập nhật thông tin ghế đã chọn và tổng giá
    private void updateSelectionInfo() {
        List<String> seatNumbers = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (SelectedSeatInfo info : selectedSeatsInfoMap.values()) {
            seatNumbers.add(info.getSeatNumber());
            if (info.getTotalPrice() != null) {
                totalPrice = totalPrice.add(info.getTotalPrice());
            }
        }

        // Cập nhật danh sách ghế đã chọn
        String selectedSeatsText = String.join(", ", seatNumbers);
        tvSelectedSeats.setText(selectedSeatsText.isEmpty() ? "Chưa chọn ghế nào" : selectedSeatsText);
        // Cập nhật tổng giá
        tvTotalPrice.setText("$" + totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

        // Cập nhật số lượng ghế đã chọn
        TextView tvNumSelectedSeats = findViewById(R.id.tv_num_selected_seats);
        if (tvNumSelectedSeats != null) {
            tvNumSelectedSeats.setText(selectedSeatsInfoMap.size() + " Ghế đã chọn");
        }
    }

    // Chuyển sang màn hình đặt vé
    private void proceedToBooking() {
        if (selectedSeatsInfoMap.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ít nhất một ghế", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển danh sách ghế đã chọn sang List
        ArrayList<SelectedSeatInfo> seatsToBook = new ArrayList<>(selectedSeatsInfoMap.values());

        // Chuyển sang màn hình BookingFormActivity
        Intent intent = new Intent(this, BookingFormActivity.class);
        intent.putExtra("flightId", flightId);
        intent.putExtra("selectedSeatsList", seatsToBook);
        startActivity(intent);
    }

    // Xử lý lỗi từ phản hồi của server
    private void handleErrorResponse(Response<SeatMapDto> response) {
        String errorMessage = "Không thể tải bản đồ ghế";
        if (response.errorBody() != null) {
            try {
                // Thông báo lỗi từ server
                errorMessage = response.errorBody().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Kiểm tra mã lỗi từ server
        if (response.code() == 400) {
            errorMessage = "Yêu cầu không hợp lệ: " + errorMessage;
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy chuyến bay: " + errorMessage;
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi server, vui lòng thử lại sau: " + errorMessage;
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}