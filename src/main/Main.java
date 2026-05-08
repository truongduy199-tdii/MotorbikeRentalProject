package main;

import com.formdev.flatlaf.FlatLightLaf;
import gui.admin.AdminMainFrame;
import gui.common.LoginFrame;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import java.awt.Font;

public class Main {
    public static void main(String[] args) {
        // 1. Thiết lập giao diện hiện đại với FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            // Tùy chọn: Set font chữ mặc định cho toàn bộ ứng dụng (giúp chữ không bị lỗi font hoặc quá nhỏ)
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));

        } catch (Exception ex) {
            System.err.println("Không thể khởi tạo giao diện FlatLaf");
        }

        // 2. Khởi chạy màn hình đăng nhập trong luồng an toàn của Swing
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
