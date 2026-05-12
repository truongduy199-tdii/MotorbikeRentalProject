package dao;

import javax.swing.JOptionPane;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class MySQLConnect {
    public static Connection getConnection() {
        Connection conn = null;
        // Dùng ClassLoader để lấy resource an toàn hơn
        try (InputStream input = MySQLConnect.class.getClassLoader().getResourceAsStream("db.properties")) {

            if (input == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy file db.properties. Hãy kiểm tra lại thư mục resources.");
                return null;
            }

            Properties prop = new Properties();
            prop.load(input);

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.username");
            String pass = prop.getProperty("db.password");

            // Cập nhật driver class mới
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Không thể kết nối đến Cơ sở dữ liệu.\nChi tiết lỗi: " + e.getMessage(),
                    "Lỗi Kết Nối CSDL",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return conn;
    }
}