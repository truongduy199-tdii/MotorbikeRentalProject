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
        String sql = "SELECT v.*, c.phone AS renter_phone " +
                "FROM VEHICLES v " +
                "LEFT JOIN RENTAL_CONTRACTS r ON v.vehicle_id = r.vehicle_id AND r.contract_status = 'ACTIVE' " +
                "LEFT JOIN CUSTOMERS c ON r.customer_id = c.customer_id";

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
                xe.setRenterPhone(rs.getString("renter_phone"));
                ds.add(xe);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Nếu bảng vẫn trống, hãy check lỗi ở Console (Output) của IntelliJ
        }
        return ds;
    }

    // Thêm vào trong class XeMayDAO
    public boolean themXeMay(XeMayDTO xe) {
        String sql = "INSERT INTO VEHICLES (vehicle_code, brand, model, license_plate, color, manufacture_year, rental_price_per_day, rental_price_per_hour, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, xe.getVehicleCode());
            ps.setString(2, xe.getBrand());
            ps.setString(3, xe.getModel());
            ps.setString(4, xe.getLicensePlate());
            ps.setString(5, xe.getColor());
            ps.setInt(6, xe.getManufactureYear());
            ps.setDouble(7, xe.getRentalPricePerDay());
            ps.setDouble(8, xe.getRentalPricePerHour());
            ps.setString(9, xe.getStatus());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean suaXeMay(XeMayDTO xe) {
        String sql = "UPDATE VEHICLES SET brand=?, model=?, license_plate=?, color=?, manufacture_year=?, rental_price_per_day=?, rental_price_per_hour=?, status=? WHERE vehicle_code=?";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, xe.getBrand());
            ps.setString(2, xe.getModel());
            ps.setString(3, xe.getLicensePlate());
            ps.setString(4, xe.getColor());
            ps.setInt(5, xe.getManufactureYear());
            ps.setDouble(6, xe.getRentalPricePerDay());
            ps.setDouble(7, xe.getRentalPricePerHour());
            ps.setString(8, xe.getStatus());
            ps.setString(9, xe.getVehicleCode());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoaXeMay(String vehicleCode) {
        String sql = "DELETE FROM VEHICLES WHERE vehicle_code=?";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, vehicleCode);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}