package dao;

import dto.HopDongDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class HopDongDAO {

    public ArrayList<HopDongDTO> layDanhSachHopDong() {
        ArrayList<HopDongDTO> list = new ArrayList<>();

        // Đã sửa 'r.created_at' thành 'r.contract_id'
        String sql = "SELECT r.contract_code, c.full_name, CONCAT(v.brand, ' ', v.model) AS vehicle_name, " +
                "r.rental_start, r.rental_end, r.deposit_amount, r.contract_status " +
                "FROM RENTAL_CONTRACTS r " +
                "JOIN CUSTOMERS c ON r.customer_id = c.customer_id " +
                "JOIN VEHICLES v ON r.vehicle_id = v.vehicle_id " +
                "ORDER BY r.contract_id DESC";

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}