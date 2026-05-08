package dao;

import javax.swing.JOptionPane;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class MySQLConnect {
    private static Connection conn = null;

    public static Connection getConnection() {
        if (conn == null) {
            // Dùng FileInputStream để đọc trực tiếp từ thư mục gốc của project
            // Đường dẫn "resources/db.properties" trỏ thẳng vào thư mục resources của bạn
            try (FileInputStream input = new FileInputStream("resources/db.properties")) {

                // Tải dữ liệu từ file properties
                Properties prop = new Properties();
                prop.load(input);

                // Lấy thông tin kết nối
                String url = prop.getProperty("db.url");
                String user = prop.getProperty("db.username");
                String pass = prop.getProperty("db.password");

                // Thực hiện kết nối
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(url, user, pass);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Không thể kết nối đến Cơ sở dữ liệu.\nChi tiết lỗi: " + e.getMessage(),
                        "Lỗi Kết Nối CSDL",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Đã đóng kết nối CSDL.");
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }

    /*public static void main(String[] args) {
        System.out.println("Đang thử kết nối đến cơ sở dữ liệu...");

        Connection testConn = getConnection();

        if (testConn != null) {
            System.out.println("Kết nối CSDL thành công!");
            JOptionPane.showMessageDialog(null,
                    "Kết nối Cơ sở dữ liệu thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);

            try {
                testConn.close();
                System.out.println("Đã đóng kết nối an toàn.");
            } catch (Exception e) {
                System.out.println("Lỗi khi đóng kết nối: " + e.getMessage());
            }
        } else {
            System.out.println("Kết nối thất bại. Hãy kiểm tra lại file db.properties và đảm bảo MySQL đang chạy.");
        }
    }*/
}
