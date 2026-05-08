package dao;

import dto.XeMayDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class XeMayDAO {

    public ArrayList<XeMayDTO> layDanhSachXeMay() {
        ArrayList<XeMayDTO> list = new ArrayList<>();
        // Nối brand và model thành vehicle_name, lấy các trường cần thiết
        String sql = "SELECT vehicle_code, CONCAT(brand, ' ', model) AS vehicle_name, " +
                "license_plate, rental_price_per_day, status " +
                "FROM VEHICLES ORDER BY created_at DESC";

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                XeMayDTO xe = new XeMayDTO();
                xe.setVehicleCode(rs.getString("vehicle_code"));
                xe.setVehicleName(rs.getString("vehicle_name"));
                xe.setLicensePlate(rs.getString("license_plate"));
                xe.setRentalPricePerDay(rs.getDouble("rental_price_per_day"));
                xe.setStatus(rs.getString("status"));

                list.add(xe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}