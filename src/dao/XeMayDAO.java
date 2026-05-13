package dao;

import dto.XeMayDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class XeMayDAO {
    public ArrayList<XeMayDTO> layDanhSachXeMay() {
        ArrayList<XeMayDTO> ds = new ArrayList<>();
        // Lấy tất cả các cột để tránh lỗi thiếu dữ liệu
        String sql = "SELECT * FROM VEHICLES";

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                XeMayDTO xe = new XeMayDTO();
                xe.setVehicleId(rs.getInt("vehicle_id"));
                xe.setVehicleCode(rs.getString("vehicle_code"));
                xe.setBrand(rs.getString("brand"));
                xe.setModel(rs.getString("model"));
                xe.setLicensePlate(rs.getString("license_plate"));
                xe.setColor(rs.getString("color"));
                xe.setManufactureYear(rs.getInt("manufacture_year"));
                xe.setRentalPricePerDay(rs.getDouble("rental_price_per_day"));
                xe.setRentalPricePerHour(rs.getDouble("rental_price_per_hour"));
                xe.setStatus(rs.getString("status"));
                ds.add(xe);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Nếu bảng vẫn trống, hãy check lỗi ở Console (Output) của IntelliJ
        }
        return ds;
    }
}