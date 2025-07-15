package com.prm.flightbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.prm.flightbooking.api.ApiServiceProvider;
import com.prm.flightbooking.api.BookingApiEndpoint;
import com.prm.flightbooking.dto.booking.BookingDetailDto;
import com.prm.flightbooking.dto.booking.FlightDetailDto;
import com.prm.flightbooking.dto.booking.PassengerSeatDto;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayActivity extends AppCompatActivity {

    // View components from activity_pay.xml
    private TextView tvTicketDetailTitle, tvJfkCode, tvLosAngelesCity, tvLaxCode, tvFlightTime;
    private TextView tvOriginCity, tvFlightDate, tvFlightTimeValue;
    private TextView tvFlightClass, tvSeatsNumber, tvAirlineName, tvTicketPrice;
    private TextView tvBarcodeNumber;
    private ImageView ivDeltaLogo, ivPlaneIcon, ivQrCode, ivBarcode;
    private Button btnDownloadTicket, btnPayLater, btnPayNow;
    private ProgressBar progressBar;

    // API service and data
    private BookingApiEndpoint bookingApi;
    private SharedPreferences sharedPreferences;
    private int userId;
    private int bookingId;

    // Format for display
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'Thg' MM, yyyy", new Locale("vi", "VN"));
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        // Initialize API service and SharedPreferences
        bookingApi = ApiServiceProvider.getBookingApi();
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Check login status
        if (!checkLoginStatus()) {
            redirectToLogin();
            return;
        }

        // Get booking ID from intent
        bookingId = getIntent().getIntExtra("bookingId", -1);
        if (bookingId == -1) {
            Toast.makeText(this, "Không tìm thấy mã đặt vé", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind views
        bindingView();
        bindingAction();

        // Lấy dữ liệu để tạo mã vạch & mã QR
   /*     String barcodeText = "321654687"; // Hoặc bookingDetail.getBookingReference() khi đã có dữ liệu
        Bitmap barcodeBitmap = generateBarcode(barcodeText, ivBarcode.getWidth() > 0 ? ivBarcode.getWidth() : 800, 200);
        if (barcodeBitmap != null) {
            ivBarcode.setImageBitmap(barcodeBitmap);
        }

        // Sinh QR chuyển khoản VietQR động nếu muốn
        // Thay các chuỗi này bằng thông tin đặt vé phù hợp
        String accountNumber = "0123456789";
        String bankBin = "970436";
        String accountName = "NGUYEN VAN A";
        String amount = "150000";
        String addInfo = "Thanh toan hoa don 001";
        String vietQRData = generateVietQR(accountNumber, bankBin, accountName, amount, addInfo);
        Bitmap qrBitmap = generateQRCode(vietQRData, 400);
        if (qrBitmap != null) {
            ivQrCode.setImageBitmap(qrBitmap);
        }*/

        // Fetch booking details
        fetchBookingDetail();
    }

    // Bind views from activity_pay.xml
    private void bindingView() {
        tvTicketDetailTitle = findViewById(R.id.text_view_ticket_detail_title);
        tvJfkCode = findViewById(R.id.text_view_jfk_code);
        tvLosAngelesCity = findViewById(R.id.text_view_los_angeles_city);
        tvLaxCode = findViewById(R.id.text_view_lax_code);
        tvFlightTime = findViewById(R.id.text_view_flight_time);
        tvOriginCity = findViewById(R.id.text_view_origin_city);
        tvFlightDate = findViewById(R.id.text_view_flight_date);
        tvFlightTimeValue = findViewById(R.id.text_view_flight_time_value);
        tvFlightClass = findViewById(R.id.text_view_flight_class);
        tvSeatsNumber = findViewById(R.id.text_view_seats_number);
        tvAirlineName = findViewById(R.id.text_view_airline_name);
        tvTicketPrice = findViewById(R.id.text_view_ticket_price);
        tvBarcodeNumber = findViewById(R.id.text_view_barcode_number);
        ivDeltaLogo = findViewById(R.id.image_view_delta_logo);
        ivPlaneIcon = findViewById(R.id.image_view_plane_icon);
        ivQrCode = findViewById(R.id.image_view_qr_code);
        ivBarcode = findViewById(R.id.image_view_barcode);
        btnDownloadTicket = findViewById(R.id.button_download_ticket);
        btnPayLater = findViewById(R.id.button_pay_later);
        btnPayNow = findViewById(R.id.button_pay_now);
        progressBar = findViewById(R.id.progress_bar);
    }

    // Bind button actions
    private void bindingAction() {
        btnDownloadTicket.setOnClickListener(this::onDownloadTicketClick);
        btnPayLater.setOnClickListener(this::onPayLaterClick);
        btnPayNow.setOnClickListener(this::onPayNowClick);
    }

    // Handle download ticket button click
    private void onDownloadTicketClick(View view) {
        Toast.makeText(this, "Tải vé - Đang phát triển", Toast.LENGTH_SHORT).show();
        // Implement ticket download logic here (e.g., generate PDF or image)
    }

    // Handle pay later button click
    private void onPayLaterClick(View view) {
        Toast.makeText(this, "Quay lại trang chủ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    // Handle pay now button click
    private void onPayNowClick(View view) {
        // Placeholder for payment processing
        Toast.makeText(this, "Thanh toán ngay - Đang phát triển", Toast.LENGTH_SHORT).show();
        // Example: Call a payment API
        // performPayment();
    }

    // Check login status
    private boolean checkLoginStatus() {
        userId = sharedPreferences.getInt("user_id", -1);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);
        if (userId <= 0 || !isLoggedIn) {
            Toast.makeText(this, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Redirect to login screen
    private void redirectToLogin() {
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Fetch booking details using API
    private void fetchBookingDetail() {
        progressBar.setVisibility(View.VISIBLE);

        Call<BookingDetailDto> call = bookingApi.getBookingDetail(userId, bookingId);
        call.enqueue(new Callback<BookingDetailDto>() {
            @Override
            public void onResponse(Call<BookingDetailDto> call, Response<BookingDetailDto> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    BookingDetailDto bookingDetail = response.body();
                    updateUI(bookingDetail);
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<BookingDetailDto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PayActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update UI with booking details
    private void updateUI(BookingDetailDto bookingDetail) {
        // Update title with booking reference
        tvTicketDetailTitle.setText("Chi Tiết Vé: " + bookingDetail.getBookingReference());

        // Update flight information
        FlightDetailDto flight = bookingDetail.getFlight();
        if (flight != null) {
            tvJfkCode.setText(getAirportCode(flight.getDepartureAirport()));
            tvOriginCity.setText(getAirportName(flight.getDepartureAirport()));
            tvLaxCode.setText(getAirportCode(flight.getArrivalAirport()));
            tvLosAngelesCity.setText(getAirportName(flight.getArrivalAirport()));
            tvAirlineName.setText(flight.getAirlineName() != null ? flight.getAirlineName() : "Chưa có thông tin");

            // Update flight time and date
            if (flight.getDepartureTime() != null && flight.getArrivalTime() != null) {
                long durationMillis = flight.getArrivalTime().getTime() - flight.getDepartureTime().getTime();
                long hours = durationMillis / (1000 * 60 * 60);
                long minutes = (durationMillis / (1000 * 60)) % 60;
                tvFlightTime.setText(String.format("%dh %dm", hours, minutes));
                tvFlightDate.setText(formatDate(flight.getDepartureTime()));
                tvFlightTimeValue.setText(formatTime(flight.getDepartureTime()));
            } else {
                tvFlightTime.setText("Chưa có thông tin");
                tvFlightDate.setText("");
                tvFlightTimeValue.setText("");
            }
        } else {
            tvJfkCode.setText("N/A");
            tvLaxCode.setText("N/A");
            tvOriginCity.setText("Chưa có thông tin");
            tvLosAngelesCity.setText("Chưa có thông tin");
            tvFlightTime.setText("Chưa có thông tin");
        }

        // Update passenger and seat information
        if (bookingDetail.getPassengers() != null && !bookingDetail.getPassengers().isEmpty()) {
            PassengerSeatDto passenger = bookingDetail.getPassengers().get(0); // Assuming first passenger for simplicity
            tvFlightClass.setText(passenger.getSeatClass());
            StringBuilder seats = new StringBuilder();
            for (PassengerSeatDto p : bookingDetail.getPassengers()) {
                seats.append(p.getSeatNumber()).append(", ");
            }
            if (seats.length() > 0) {
                seats.setLength(seats.length() - 2); // Remove trailing comma
            }
            tvSeatsNumber.setText(seats.toString());
        } else {
            tvFlightClass.setText("Chưa có thông tin");
            tvSeatsNumber.setText("N/A");
        }

        // Update price
        BigDecimal totalAmount = bookingDetail.getTotalAmount();
        tvTicketPrice.setText(totalAmount != null ? currencyFormat.format(totalAmount) + " VND" : "Chưa có thông tin");

        // Update barcode number (use booking reference as a placeholder)
        tvBarcodeNumber.setText(bookingDetail.getBookingReference());

        // Update payment button visibility based on payment status
        updatePaymentButtons(bookingDetail.getPaymentStatus());

        // Hiển thị mã vạch booking reference
        String bookingRef = bookingDetail.getBookingReference();
        Bitmap barcodeBitmap = generateBarcode(bookingRef, ivBarcode.getWidth() > 0 ? ivBarcode.getWidth() : 800, 200);
        if (barcodeBitmap != null) {
            ivBarcode.setImageBitmap(barcodeBitmap);
        }

        /*// Tạo chuỗi VietQR chuyển khoản sử dụng thông tin MB Bank, người nhận, số tiền, nội dung
        String accountNumber = "555508122003";
        String bankBin = "970405";
        String accountName = "LUONG QUANG VU";

        // Lấy số tiền từ tổng trong booking, chuỗi số, ko có dấu
        totalAmount = bookingDetail.getTotalAmount();
        String amount = (totalAmount != null) ? totalAmount.toBigInteger().toString() : "0";

        String addInfo = "Thanh toan ve may bay";

        // Tạo mã QR theo booking

        String vietQRData = generateVietQR(accountNumber, bankBin, accountName, amount, addInfo);

        Bitmap qrBitmap = generateQRCode(vietQRData, 400);
        if (qrBitmap != null) {
            ivQrCode.setImageBitmap(qrBitmap);
        }*/
    }

    // Update payment button visibility
    private void updatePaymentButtons(String paymentStatus) {
        if ("PAID".equalsIgnoreCase(paymentStatus)) {
            btnPayNow.setVisibility(View.GONE);
            btnPayLater.setVisibility(View.GONE);
            btnDownloadTicket.setVisibility(View.VISIBLE);
        } else if ("PENDING".equalsIgnoreCase(paymentStatus)) {
            btnPayNow.setVisibility(View.VISIBLE);
            btnPayLater.setVisibility(View.VISIBLE);
            btnDownloadTicket.setVisibility(View.GONE);
        } else {
            btnPayNow.setVisibility(View.GONE);
            btnPayLater.setVisibility(View.GONE);
            btnDownloadTicket.setVisibility(View.GONE);
        }
    }

    // Handle API error response
    private void handleErrorResponse(Response<BookingDetailDto> response) {
        String errorMessage = "Không thể tải chi tiết vé";
        if (response.code() == 401) {
            errorMessage = "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.";
            redirectToLogin();
        } else if (response.code() == 404) {
            errorMessage = "Không tìm thấy thông tin vé";
        } else if (response.code() >= 500) {
            errorMessage = "Lỗi máy chủ, vui lòng thử lại sau";
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Utility methods from BookingDetailActivity
    private String getAirportName(String airportStr) {
        if (airportStr == null) return "Chưa có thông tin";
        int idx = airportStr.lastIndexOf(" (");
        return idx > 0 ? airportStr.substring(0, idx) : airportStr;
    }

    private String getAirportCode(String airportStr) {
        if (airportStr == null) return "";
        int start = airportStr.lastIndexOf("(");
        int end = airportStr.lastIndexOf(")");
        return (start >= 0 && end > start) ? airportStr.substring(start + 1, end) : airportStr;
    }

    private String formatTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("vi", "VN"));
        return timeFormat.format(date);
    }

    private String formatDate(Date date) {
        if (date == null) return "";
        return dateFormat.format(date);
    }

    // Tạo barcode (mã vạch)
    private Bitmap generateBarcode(String text, int width, int height) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.CODE_128, width, height);
            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.createBitmap(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Tạo mã QR
    private Bitmap generateQRCode(String text, int size) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size);
            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Tạo dữ liệu VietQR (nếu muốn dùng QR cho chuyển khoản)
    private String generateVietQR(String accountNumber, String bankBin, String accountName, String amount, String addInfo) {
        StringBuilder qr = new StringBuilder();
        qr.append("000201"); // Phiên bản QR
        qr.append("010212"); // Loại giao dịch chuyển khoản
        String bankInfo = "0010A000000727";
        qr.append("38").append(String.format("%02d", bankInfo.length() + 8 + accountNumber.length()))
                .append(bankInfo)
                .append("0208").append(bankBin)
                .append("03").append(String.format("%02d", accountNumber.length())).append(accountNumber);
        qr.append("52040000");
        qr.append("5303704");
        qr.append("54").append(String.format("%02d", amount.length())).append(amount);
        qr.append("5802VN");
        qr.append("59").append(String.format("%02d", accountName.length())).append(accountName);
        qr.append("62").append(String.format("%02d", addInfo.length() + 4))
                .append("08").append(String.format("%02d", addInfo.length())).append(addInfo);
        return qr.toString();
    }
}