package dao;

import dto.KhachHangDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public List<KhachHangDTO> getAllCustomers() {
        List<KhachHangDTO> list = new ArrayList<>();
        // Mọi thông tin hiện tại đều đã nằm trong bảng CUSTOMERS
        String sql = "SELECT * FROM CUSTOMERS ORDER BY customer_id DESC";

        try (Connection conn = MySQLConnect.getConnection()) {
            if (conn == null) return null;

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    KhachHangDTO kh = new KhachHangDTO();
                    kh.setCustomerId(rs.getInt("customer_id"));
                    kh.setUserId(rs.getInt("user_id"));
                    // Lấy trực tiếp từ bảng CUSTOMERS
                    kh.setFullName(rs.getString("full_name"));
                    kh.setPhone(rs.getString("phone"));
                    kh.setEmail(rs.getString("email"));
                    kh.setCccd(rs.getString("cccd"));
                    kh.setBirthday(rs.getDate("birthday"));
                    kh.setAddress(rs.getString("address"));
                    kh.setDriverLicenseNumber(rs.getString("driver_license_number"));
                    kh.setStatus(rs.getString("status"));
                    list.add(kh);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ LỖI KHI LẤY DANH SÁCH KHÁCH HÀNG: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return list;
    }
}