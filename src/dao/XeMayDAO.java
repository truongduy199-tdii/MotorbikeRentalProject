package dao;

import dto.XeMayDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class XeMayDAO {

    public ArrayList<XeMayDTO> layDanhSachXeMay() {
        ArrayList<XeMayDTO> list = new ArrayList<>();

        // ĐÃ SỬA: Thêm vehicle_id vào câu lệnh SELECT
        String sql = "SELECT vehicle_id, vehicle_code, brand, model, CONCAT(brand, ' ', model) AS vehicle_name, " +
                "license_plate, color, manufacture_year, rental_price_per_day, status " +
                "FROM VEHICLES ORDER BY vehicle_code ASC";

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                XeMayDTO xe = new XeMayDTO();
                // ĐÃ SỬA: Phải lấy vehicle_id để làm khóa ngoại khi tạo hợp đồng
                xe.setVehicleId(rs.getInt("vehicle_id"));

                xe.setVehicleCode(rs.getString("vehicle_code"));
                xe.setBrand(rs.getString("brand"));
                xe.setModel(rs.getString("model"));
                xe.setVehicleName(rs.getString("vehicle_name"));
                xe.setLicensePlate(rs.getString("license_plate"));
                xe.setColor(rs.getString("color"));

                double pricePerDay = rs.getDouble("rental_price_per_day");
                xe.setRentalPricePerDay(pricePerDay);
                xe.setRentalPricePerHour(pricePerDay / 10); // Tạm tính giá giờ
                xe.setStatus(rs.getString("status"));

                list.add(xe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean themXeMay(XeMayDTO xe) {
        String sql = "INSERT INTO VEHICLES (vehicle_code, brand, model, license_plate, color, manufacture_year, rental_price_per_day, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, xe.getVehicleCode());
            ps.setString(2, xe.getBrand());
            ps.setString(3, xe.getModel());
            ps.setString(4, xe.getLicensePlate());
            ps.setString(5, xe.getColor());
            ps.setInt(6, 2022);
            ps.setDouble(7, xe.getRentalPricePerDay());
            ps.setString(8, xe.getStatus());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean suaXeMay(XeMayDTO xe) {
        String sql = "UPDATE VEHICLES SET brand = ?, model = ?, license_plate = ?, color = ?, manufacture_year = ?, rental_price_per_day = ?, status = ? WHERE vehicle_code = ?";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, xe.getBrand());
            ps.setString(2, xe.getModel());
            ps.setString(3, xe.getLicensePlate());
            ps.setString(4, xe.getColor());
            ps.setInt(5, 2022);
            ps.setDouble(6, xe.getRentalPricePerDay());
            ps.setString(7, xe.getStatus());
            ps.setString(8, xe.getVehicleCode());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}