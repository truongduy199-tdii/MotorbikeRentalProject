package dao;

import dto.HopDongDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class HopDongDAO {

    // 1. Lấy tất cả hợp đồng (dành cho Admin)
    public ArrayList<HopDongDTO> layDanhSachHopDong() {
        ArrayList<HopDongDTO> list = new ArrayList<>();
        String sql = "SELECT r.contract_code, c.full_name, CONCAT(v.brand, ' ', v.model) AS vehicle_name, " +
                "r.rental_start, r.rental_end, r.deposit_amount, r.contract_status " +
                "FROM RENTAL_CONTRACTS r " +
                "JOIN CUSTOMERS c ON r.customer_id = c.customer_id " +
                "JOIN VEHICLES v ON r.vehicle_id = v.vehicle_id " +
                "ORDER BY r.rental_start DESC"; // ORDER BY rental_start thay vì created_at

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HopDongDTO hd = new HopDongDTO();
                hd.setContractCode(rs.getString("contract_code"));
                hd.setCustomerName(rs.getString("full_name"));
                hd.setVehicleName(rs.getString("vehicle_name"));
                hd.setRentalStart(rs.getTimestamp("rental_start"));
                hd.setRentalEnd(rs.getTimestamp("rental_end"));
                hd.setDepositAmount(rs.getDouble("deposit_amount"));
                hd.setContractStatus(rs.getString("contract_status"));
                list.add(hd);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 2. Lấy danh sách hợp đồng theo ID của khách hàng
    public ArrayList<HopDongDTO> layHopDongTheoUser(int userId) {
        ArrayList<HopDongDTO> list = new ArrayList<>();
        String sql = "SELECT r.contract_code, CONCAT(v.brand, ' ', v.model) AS vehicle_name, " +
                "r.rental_start, r.rental_end, r.deposit_amount, r.total_amount, r.contract_status " +
                "FROM RENTAL_CONTRACTS r " +
                "JOIN CUSTOMERS c ON r.customer_id = c.customer_id " +
                "JOIN VEHICLES v ON r.vehicle_id = v.vehicle_id " +
                "WHERE c.user_id = ? ORDER BY r.rental_start DESC"; // ORDER BY rental_start

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HopDongDTO hd = new HopDongDTO();
                    hd.setContractCode(rs.getString("contract_code"));
                    hd.setVehicleName(rs.getString("vehicle_name"));
                    hd.setRentalStart(rs.getTimestamp("rental_start"));
                    hd.setRentalEnd(rs.getTimestamp("rental_end"));
                    hd.setDepositAmount(rs.getDouble("deposit_amount"));
                    hd.setTotalAmount(rs.getDouble("total_amount"));
                    hd.setContractStatus(rs.getString("contract_status"));
                    list.add(hd);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // 3. Tạo yêu cầu thuê xe mới (Lưu xuống DB với trạng thái PENDING)
    public boolean taoYeuCauThue(HopDongDTO hd) {
        // ĐÃ SỬA: Loại bỏ cột rental_type cho khớp với cấu trúc Database
        String sql = "INSERT INTO RENTAL_CONTRACTS (contract_code, customer_id, vehicle_id, created_by, " +
                "rental_start, rental_end, deposit_amount, total_amount, contract_status) " +
                "VALUES (?, (SELECT customer_id FROM CUSTOMERS WHERE user_id = ?), ?, 1, ?, ?, ?, ?, 'PENDING')";

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Tạo mã hợp đồng ngẫu nhiên HD + 6 số
            ps.setString(1, "HD" + (System.currentTimeMillis() % 1000000));
            ps.setInt(2, hd.getCustomerId());
            ps.setInt(3, hd.getVehicleId());
            ps.setTimestamp(4, hd.getRentalStart());
            ps.setTimestamp(5, hd.getRentalEnd());

            // Vì đã bỏ rentalType, nên depositAmount và totalAmount đẩy lên 1 bậc
            ps.setDouble(6, hd.getDepositAmount());
            ps.setDouble(7, hd.getTotalAmount());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}