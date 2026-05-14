package dao;

import dto.TaiKhoanDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaiKhoanDAO {

    public TaiKhoanDTO kiemTraDangNhap(String username, String passwordHash) {
        TaiKhoanDTO user = null;

        // Sử dụng LEFT JOIN để lấy full_name từ bảng CUSTOMERS.
        // Nếu là Admin (không có trong bảng CUSTOMERS), full_name sẽ mặc định là 'Administrator'
        String sql = "SELECT u.user_id, u.username, u.role, u.status, IFNULL(c.full_name, 'Administrator') AS full_name " +
                "FROM USERS u " +
                "LEFT JOIN CUSTOMERS c ON u.user_id = c.user_id " +
                "WHERE u.username = ? AND u.password_hash = ? AND u.status = 'ACTIVE'";

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
            e.printStackTrace();
        }
        return user;
    }
}