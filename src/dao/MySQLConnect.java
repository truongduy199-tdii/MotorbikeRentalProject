package dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class MySQLConnect {
    public static Connection getConnection() {
        Connection conn = null;
        try (InputStream input = MySQLConnect.class.getClassLoader().getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new RuntimeException("Không tìm thấy file db.properties. Hãy kiểm tra lại thư mục resources.");
            }

            Properties prop = new Properties();
            prop.load(input);

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.username");
            String pass = prop.getProperty("db.password");
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            throw new RuntimeException("Lỗi kết nối Cơ sở dữ liệu: " + e.getMessage(), e);
        }
        return conn;
    }
}