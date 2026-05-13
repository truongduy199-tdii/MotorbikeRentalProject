package utils;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class SecurityHelper {

    // Hàm mã hóa mật khẩu sang SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi mã hóa mật khẩu", ex);
        }
    }

    // Chạy file để lấy mã hash chuẩn
    public static void main(String[] args) {
        //System.out.println("Hash của '...': " + hashPassword("....."));
        System.out.println("Hash của '...': " + hashPassword("admin123"));
    }
}