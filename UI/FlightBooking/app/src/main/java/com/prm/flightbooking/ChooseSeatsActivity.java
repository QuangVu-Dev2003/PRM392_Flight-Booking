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
    // Thay đổi từ List<String> sang Map<Integer, SelectedSeatInfo> để dễ quản lý thông tin hành khách
    private Map<Integer, SelectedSeatInfo> selectedSeatsInfoMap; // Key: Seat ID
    private int flightId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_seats);

        flightApi = ApiServiceProvider.getFlightApi();
        flightId = getIntent().getIntExtra("flightId", -1);
        if (flightId == -1) {
            Toast.makeText(this, "Invalid flight ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lưu flightId vào SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("flightId", flightId);
        editor.apply();

        bindingView();
        selectedSeatsInfoMap = new HashMap<>(); // Khởi tạo Map
        bindingAction();
        fetchSeatMap();
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
        btnBack.setOnClickListener(v -> finish());
        btnConfirmSeats.setOnClickListener(v -> proceedToBooking());
    }

    private void fetchSeatMap() {
        progressBar.setVisibility(View.VISIBLE);
        Call<SeatMapDto> call = flightApi.getSeatMap(flightId);
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
                Toast.makeText(ChooseSeatsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySeatMap(SeatMapDto seatMap) {
        llBusinessSeats.removeAllViews();
        llEconomySeats.removeAllViews();

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

            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            // rowParams.setMargins(0, 0, 0, row == seatsByRow.size() ? 0 : 8); // Logic này có thể bị sai nếu row không liên tục
            rowParams.setMargins(0, 0, 0, 8); // Đặt margin cố định giữa các hàng
            rowLayout.setLayoutParams(rowParams);

            for (int i = 0; i < rowSeats.size(); i++) {
                SeatDto seat = rowSeats.get(i);
                TextView seatView = new TextView(this);
                seatView.setText(seat.getSeatNumber());
                LinearLayout.LayoutParams seatParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                // Thêm margin giữa các ghế, đặc biệt là khoảng trống hành lang
                seatParams.setMargins(i > 0 ? (seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ? 8 : 4) : 0, 0, 0, 0);
                seatView.setLayoutParams(seatParams);

                // Kiểm tra xem ghế đã được chọn trước đó chưa để set style
                boolean isSelected = selectedSeatsInfoMap.containsKey(seat.getSeatId());

                int styleResId = seat.isAvailable() ?
                        (isSelected ?
                                (seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ?
                                        R.style.SeatButtonBusiness_Selected : R.style.SeatButtonEconomy_Selected) :
                                (seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ?
                                        R.style.SeatButtonBusiness : R.style.SeatButtonEconomy)) :
                        (seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ?
                                R.style.SeatButtonBusiness_Unavailable : R.style.SeatButtonEconomy_Unavailable);
                seatView.setTextAppearance(styleResId);

                seatView.setOnClickListener(v -> {
                    if (seat.isAvailable()) {
                        if (selectedSeatsInfoMap.containsKey(seat.getSeatId())) {
                            // Ghế đã chọn, hủy chọn và xóa thông tin hành khách
                            selectedSeatsInfoMap.remove(seat.getSeatId());
                            seatView.setTextAppearance(seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ?
                                    R.style.SeatButtonBusiness : R.style.SeatButtonEconomy);
                            updateSelectionInfo();
                        } else {
                            // Ghế chưa chọn, yêu cầu nhập thông tin hành khách
                            showPassengerInfoDialog(seat, seatView);
                        }
                    } else {
                        Toast.makeText(this, "Seat " + seat.getSeatNumber() + " is not available.", Toast.LENGTH_SHORT).show();
                    }
                });

                rowLayout.addView(seatView);

                // Thêm Space để tạo khoảng trống giữa các nhóm ghế (ví dụ: hành lang)
                // Giả sử có 6 ghế/hàng, khoảng trống sẽ ở vị trí thứ 3 và thứ 4
                // Hoặc bạn có thể tùy chỉnh logic này dựa trên cấu hình ghế của bạn
                if (rowSeats.size() > 3 && i == 2) { // Ví dụ: sau ghế thứ 3 trong hàng
                    Space space = new Space(this);
                    space.setLayoutParams(new LinearLayout.LayoutParams(
                            seat.getSeatClassName().equals("Business") || seat.getSeatClassName().equals("FIRST_CLASS") ? 32 : 20,
                            LinearLayout.LayoutParams.MATCH_PARENT)); // Chiều cao phù hợp
                    rowLayout.addView(space);
                }
            }

            SeatDto firstSeat = rowSeats.get(0);
            LinearLayout targetLayout = (firstSeat.getSeatClassName().equals("Business") || firstSeat.getSeatClassName().equals("FIRST_CLASS")) ?
                    llBusinessSeats : llEconomySeats;
            targetLayout.addView(rowLayout);
        }

        updateSelectionInfo(); // Cập nhật thông tin ban đầu (nếu có ghế nào đã được chọn trước đó)
    }

    private void showPassengerInfoDialog(SeatDto seat, TextView seatView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Passenger Info for Seat " + seat.getSeatNumber());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20); // Add padding

        final EditText inputName = new EditText(this);
        inputName.setHint("Passenger Name");
        layout.addView(inputName);

        final EditText inputIdNumber = new EditText(this);
        inputIdNumber.setHint("Passenger ID Number (9-12 digits)");
        inputIdNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputIdNumber);

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String name = inputName.getText().toString().trim();
            String idNumber = inputIdNumber.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Passenger Name cannot be empty.", Toast.LENGTH_SHORT).show();
                // Không đóng dialog, hoặc yêu cầu nhập lại
                showPassengerInfoDialog(seat, seatView); // Hiển thị lại dialog
                return;
            }
            if (TextUtils.isEmpty(idNumber) || !idNumber.matches("\\d{9,12}")) {
                Toast.makeText(this, "Passenger ID Number must be 9-12 digits.", Toast.LENGTH_SHORT).show();
                // Không đóng dialog, hoặc yêu cầu nhập lại
                showPassengerInfoDialog(seat, seatView); // Hiển thị lại dialog
                return;
            }

            // Tạo đối tượng SelectedSeatInfo và thêm vào map
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
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            // Nếu hủy, không làm gì cả, ghế vẫn chưa được chọn
        });

        builder.show();
    }


    private void updateSelectionInfo() {
        List<String> seatNumbers = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (SelectedSeatInfo info : selectedSeatsInfoMap.values()) {
            seatNumbers.add(info.getSeatNumber());
            if (info.getTotalPrice() != null) {
                totalPrice = totalPrice.add(info.getTotalPrice());
            }
        }

        String selectedSeatsText = String.join(", ", seatNumbers);
        tvSelectedSeats.setText(selectedSeatsText.isEmpty() ? "No seats selected" : selectedSeatsText);
        tvTotalPrice.setText("$" + totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

        TextView tvNumSelectedSeats = findViewById(R.id.tv_num_selected_seats);
        if (tvNumSelectedSeats != null) {
            tvNumSelectedSeats.setText(selectedSeatsInfoMap.size() + " Seats Selected");
        }
    }

    private void proceedToBooking() {
        if (selectedSeatsInfoMap.isEmpty()) {
            Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chuyển Map các ghế đã chọn sang List để truyền qua Intent
        ArrayList<SelectedSeatInfo> seatsToBook = new ArrayList<>(selectedSeatsInfoMap.values());

        Intent intent = new Intent(this, BookingFormActivity.class);
        intent.putExtra("flightId", flightId);
        intent.putExtra("selectedSeatsList", seatsToBook); // Truyền danh sách qua
        startActivity(intent);
        // Không finish() ở đây nếu bạn muốn người dùng có thể quay lại chọn thêm/thay đổi
        // Nhưng nếu luồng là "chọn xong là đi đặt luôn", thì có thể finish()
        // finish(); // Tùy vào luồng UI mong muốn
    }

    private void handleErrorResponse(Response<SeatMapDto> response) {
        String errorMessage = "Failed to load seat map";
        if (response.errorBody() != null) {
            try {
                // Thử đọc thông báo lỗi từ error body nếu có
                errorMessage = response.errorBody().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        switch (response.code()) {
            case 400:
                errorMessage = "Invalid request: " + errorMessage;
                break;
            case 404:
                errorMessage = "Flight not found: " + errorMessage;
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
