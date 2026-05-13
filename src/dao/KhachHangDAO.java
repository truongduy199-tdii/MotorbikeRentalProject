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
        String sql = "SELECT * FROM CUSTOMERS ORDER BY customer_id DESC";

        Connection conn = null;
        try {
            conn = MySQLConnect.getConnection();

            // KIỂM TRA NULL KẾT NỐI
            if (conn == null) {
                System.err.println("LỖI: Không thể kết nối tới Database!");
                return list; // Trả về danh sách rỗng
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

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
                kh.setStatus(rs.getString("status"));
                list.add(kh);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            System.err.println("❌ LỖI KHI LẤY DANH SÁCH KHÁCH HÀNG: " + e.getMessage());
            System.err.println("SQL: " + sql);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("✓ Đã lấy " + list.size() + " khách hàng từ database");
        return list;
    }
}