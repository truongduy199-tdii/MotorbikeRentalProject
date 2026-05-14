package dao;

import dto.KhachHangDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class KhachHangDAO {

    // --- HÀM CHO ADMIN ---
    public ArrayList<KhachHangDTO> getAllCustomers() {
        ArrayList<KhachHangDTO> list = new ArrayList<>();
        // Đã sửa lỗi ORDER BY created_at (Cột không tồn tại) -> Thành customer_id
        String sql = "SELECT * FROM CUSTOMERS ORDER BY customer_id ";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToDTO(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách khách hàng: " + e.getMessage(), e);
        }
        return list;
    }

    // 1. Hàm TÌM KIẾM bằng SQL (Chuẩn 3 lớp)
    public ArrayList<KhachHangDTO> timKiemKhachHang(String keyword, String status) {
        ArrayList<KhachHangDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM CUSTOMERS WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(full_name) LIKE ? OR phone LIKE ? OR cccd LIKE ?) ");
        }
        if (status != null && !status.equals("Tất cả trạng thái")) {
            sql.append(" AND status = ? ");
        }
        sql.append(" ORDER BY customer_id ");

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKey = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(paramIndex++, searchKey);
                ps.setString(paramIndex++, searchKey);
                ps.setString(paramIndex++, searchKey);
            }
            if (status != null && !status.equals("Tất cả trạng thái")) {
                ps.setString(paramIndex++, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi truy vấn tìm kiếm khách hàng: " + e.getMessage(), e);
        }
        return list;
    }

    // 2. Hàm SỬA Khách hàng (Admin)
    public boolean suaKhachHang(KhachHangDTO kh) {
        String sql = "UPDATE CUSTOMERS SET full_name=?, phone=?, email=?, cccd=?, birthday=?, address=?, driver_license_number=?, status=? WHERE customer_id=?";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getFullName());
            ps.setString(2, kh.getPhone());
            ps.setString(3, kh.getEmail());
            ps.setString(4, kh.getCccd());
            ps.setDate(5, kh.getBirthday());
            ps.setString(6, kh.getAddress());
            ps.setString(7, kh.getDriverLicenseNumber());
            ps.setString(8, kh.getStatus());
            ps.setInt(9, kh.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi cập nhật CSDL khách hàng: " + e.getMessage(), e);
        }
    }

    // 3. Hàm XÓA Khách hàng (Thực tế là khóa tài khoản - Đổi status thành BLOCKED/INACTIVE)
    public boolean xoaKhachHang(int customerId) {
        String sql = "UPDATE CUSTOMERS SET status = 'BLOCKED' WHERE customer_id = ?";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa/khóa khách hàng: " + e.getMessage(), e);
        }
    }

    public boolean doiMatKhau(int userId, String oldPassHash, String newPassHash) {
        String sql = "UPDATE USERS SET password_hash = ? WHERE user_id = ? AND password_hash = ?";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassHash);
            ps.setInt(2, userId);
            ps.setString(3, oldPassHash);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tương tác CSDL khi đổi mật khẩu: " + e.getMessage(), e);
        }
    }

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
            throw new RuntimeException("Lỗi cập nhật CSDL khi đổi thông tin cá nhân: " + e.getMessage(), e);
        }
    }

    // --- HÀM CHO CUSTOMER ---
    public KhachHangDTO layThongTinTheoUserId(int userId) {
        String sql = "SELECT * FROM CUSTOMERS WHERE user_id = ?";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thông tin user: " + e.getMessage(), e);
        }
        return null;
    }

    // Hàm tiện ích để Map dữ liệu tránh lặp code
    private KhachHangDTO mapResultSetToDTO(ResultSet rs) throws Exception {
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