package dao;

import dto.TaiKhoanDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaiKhoanDAO {

    public TaiKhoanDTO kiemTraDangNhap(String username, String passwordHash) {
        TaiKhoanDTO user = null;
        // Bỏ điều kiện status = 'ACTIVE' ở đây để tầng BUS bắt lỗi tài khoản bị khóa chi tiết hơn
        String sql = "SELECT u.user_id, u.username, u.role, u.status, IFNULL(c.full_name, 'Administrator') AS full_name " +
                "FROM USERS u " +
                "LEFT JOIN CUSTOMERS c ON u.user_id = c.user_id " +
                "WHERE u.username = ? AND u.password_hash = ?";

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new TaiKhoanDTO();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getString("status"));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi CSDL khi đăng nhập: " + e.getMessage(), e);
        }
        return user;
    }

    // THÊM HÀM MỚI: Kiểm tra trùng tên đăng nhập
    public boolean kiemTraTonTaiUsername(String username) {
        String sql = "SELECT 1 FROM USERS WHERE username = ?";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Trả về true nếu đã có người dùng tên này
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi CSDL khi kiểm tra username: " + e.getMessage(), e);
        }
    }

    public boolean themTaiKhoan(TaiKhoanDTO tk) {
        String sql = "INSERT INTO USERS (username, password_hash, role, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tk.getUsername());
            ps.setString(2, tk.getPassword());
            ps.setString(3, "CUSTOMER");
            ps.setString(4, "ACTIVE");

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi CSDL khi đăng ký tài khoản: " + e.getMessage(), e);
        }
    }
}