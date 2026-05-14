package main;

import com.formdev.flatlaf.FlatLightLaf;
import gui.admin.AdminMainFrame;
import gui.common.LoginFrame;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import java.awt.Font;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));

        } catch (Exception ex) {
            System.err.println("Không thể khởi tạo giao diện FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
