package com.example.prm_pe;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.WriterException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Hiển thị mã vạch (barcode)
        ImageView barcodeImage = findViewById(R.id.barcodeImage);
        Bitmap barcodeBitmap = generateBarcode("321654687", 800, 200);
        if (barcodeBitmap != null) {
            barcodeImage.setImageBitmap(barcodeBitmap);
        }

        // Thông tin tài khoản và số tiền động
        String accountNumber = "0123456789"; // Số tài khoản nhận tiền
        String bankBin = "970436"; // BIN ngân hàng (Vietcombank: 970436, BIDV: 970418, Techcombank: 970407, v.v.)
        String accountName = "NGUYEN VAN A";
        String amount = "150000"; // Số tiền động (đơn vị VNĐ, không có dấu phẩy, chỉ số)
        String addInfo = "Thanh toan hoa don 001"; // Nội dung chuyển khoản

        // Sinh chuỗi QR VietQR động
        String vietQRData = generateVietQR(accountNumber, bankBin, accountName, amount, addInfo);

        // Hiển thị mã QR VietQR
        ImageView qrImage = findViewById(R.id.qrImage);
        Bitmap qrBitmap = generateQRCode(vietQRData, 400);
        if (qrBitmap != null) {
            qrImage.setImageBitmap(qrBitmap);
        }
    }

    // Hàm sinh barcode (mã vạch)
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

    // Hàm sinh mã QR
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

    /**
     * Hàm sinh chuỗi QR VietQR động (chuẩn EMVCo, đơn giản hóa)
     * @param accountNumber Số tài khoản nhận tiền
     * @param bankBin       BIN ngân hàng (6 số, tra cứu tại https://vietqr.net/banks/)
     * @param accountName   Tên chủ tài khoản (IN HOA, không dấu)
     * @param amount        Số tiền (chuỗi số, đơn vị VNĐ)
     * @param addInfo       Nội dung chuyển khoản
     * @return Chuỗi dữ liệu QR VietQR
     */
    private String generateVietQR(String accountNumber, String bankBin, String accountName, String amount, String addInfo) {
        // Đây là ví dụ đơn giản hóa, đủ dùng cho phần lớn app ngân hàng Việt Nam hiện nay
        // Nếu cần chuẩn hóa 100%, nên dùng thư viện hoặc API VietQR chính thức

        StringBuilder qr = new StringBuilder();
        qr.append("000201"); // Phiên bản QR
        qr.append("010212"); // Loại giao dịch chuyển khoản
        // Thông tin ngân hàng, tài khoản
        String bankInfo = "0010A000000727";
        qr.append("38").append(String.format("%02d", bankInfo.length() + 8 + accountNumber.length()))
                .append(bankInfo)
                .append("0208").append(bankBin)
                .append("03").append(String.format("%02d", accountNumber.length())).append(accountNumber);
        qr.append("52040000"); // Merchant category code
        qr.append("5303704");  // Currency (704 = VND)
        qr.append("54").append(String.format("%02d", amount.length())).append(amount); // Số tiền
        qr.append("5802VN");   // Quốc gia
        qr.append("59").append(String.format("%02d", accountName.length())).append(accountName); // Tên chủ TK
        qr.append("62").append(String.format("%02d", addInfo.length() + 4))
                .append("08").append(String.format("%02d", addInfo.length())).append(addInfo); // Nội dung
        // CRC sẽ được app ngân hàng tự tính, không cần thêm ở đây
        return qr.toString();
    }
}
