package dao;

import dto.KhachHangDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class KhachHangDAO {

    // --- HÀM CHO ADMIN ---
    // Lấy toàn bộ danh sách khách hàng
    public ArrayList<KhachHangDTO> getAllCustomers() {
        ArrayList<KhachHangDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM CUSTOMERS ORDER BY created_at DESC";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhachHangDTO kh = new KhachHangDTO();
                kh.setCustomerId(rs.getInt("customer_id"));
                kh.setUserId(rs.getInt("user_id"));
                kh.setFullName(rs.getString("full_name"));
                kh.setPhone(rs.getString("phone"));
                kh.setEmail(rs.getString("email"));
                kh.setCccd(rs.getString("cccd"));
                kh.setBirthday(rs.getDate("birthday"));
                kh.setAddress(rs.getString("address"));
                kh.setDriverLicenseNumber(rs.getString("driver_license_number"));
                kh.setStatus(rs.getString("status")); // Lấy thêm trạng thái
                list.add(kh);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // --- HÀM CHO CUSTOMER ---
    // 1. Lấy thông tin khách hàng dựa trên ID tài khoản đang đăng nhập
    public KhachHangDTO layThongTinTheoUserId(int userId) {
        String sql = "SELECT * FROM CUSTOMERS WHERE user_id = ?";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhachHangDTO kh = new KhachHangDTO();
                    kh.setCustomerId(rs.getInt("customer_id"));
                    kh.setUserId(rs.getInt("user_id"));
                    kh.setFullName(rs.getString("full_name"));
                    kh.setPhone(rs.getString("phone"));
                    kh.setEmail(rs.getString("email"));
                    kh.setCccd(rs.getString("cccd"));
                    kh.setBirthday(rs.getDate("birthday"));
                    kh.setAddress(rs.getString("address"));
                    kh.setDriverLicenseNumber(rs.getString("driver_license_number"));
                    kh.setStatus(rs.getString("status"));
                    return kh;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // 2. Cập nhật thông tin liên hệ
    public boolean capNhatThongTin(KhachHangDTO kh) {
        String sql = "UPDATE CUSTOMERS SET phone = ?, email = ?, address = ? WHERE user_id = ?";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getPhone());
            ps.setString(2, kh.getEmail());
            ps.setString(3, kh.getAddress());
            ps.setInt(4, kh.getUserId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 3. Đổi mật khẩu trong bảng USERS
    public boolean doiMatKhau(int userId, String oldPassHash, String newPassHash) {
        String sql = "UPDATE USERS SET password_hash = ? WHERE user_id = ? AND password_hash = ?";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassHash);
            ps.setInt(2, userId);
            ps.setString(3, oldPassHash);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}