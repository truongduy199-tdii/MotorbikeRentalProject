package dao;

import dto.TaiKhoanDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TaiKhoanDAO {

    public TaiKhoanDTO kiemTraDangNhap(String username, String passwordHash) {
        TaiKhoanDTO user = null;

        // CHỈ LẤY CÁC CỘT TỒN TẠI TRONG BẢNG USERS
        String sql = "SELECT user_id, username, role, status FROM USERS WHERE username = ? AND password_hash = ? AND status = 'ACTIVE'";

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new TaiKhoanDTO();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getString("status"));

                    // Do file LoginFrame có dòng gọi account.getFullName() để hiển thị câu "Xin chào..."
                    // nên ta gán tạm fullName bằng username để tránh bị hiện chữ "Xin chào null"
                    user.setFullName(rs.getString("username"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean themTaiKhoan(TaiKhoanDTO tk) {
        String sql = "INSERT INTO USERS (username, password_hash, role, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tk.getUsername());
            ps.setString(2, tk.getPassword());
            ps.setString(3, "CUSTOMER"); // Mặc định là khách hàng
            ps.setString(4, "ACTIVE"); // Mặc định trạng thái là hoạt động

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}