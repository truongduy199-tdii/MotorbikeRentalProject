package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnect {
    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASENAME = "motorbikedb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASENAME
                        + "?useUnicode=true&characterEncoding=UTF-8";

                Class.forName("com.mysql.cj.jdbc.Driver");

                connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
                System.out.println("Kết nối CSDL thành công!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy thư viện MySQL JDBC Driver (mysql-connector-java.jar).");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi: Không thể kết nối tới Cơ sở dữ liệu. Hãy kiểm tra lại thông tin cấu hình.");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đã đóng kết nối CSDL.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Connection conn = MySQLConnect.getConnection();

        if (conn != null) {
            System.out.println("Sẵn sàng viết các hàm DAO!");
            MySQLConnect.closeConnection();
        }
    }
}
