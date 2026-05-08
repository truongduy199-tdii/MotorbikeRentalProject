package dao;

import javax.swing.JOptionPane;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class MySQLConnect {

    // Tạo MỚI kết nối cho mỗi lần gọi để dùng an toàn với try-with-resources
    public static Connection getConnection() {
        Connection conn = null;
        try (FileInputStream input = new FileInputStream("resources/db.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.username");
            String pass = prop.getProperty("db.password");

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