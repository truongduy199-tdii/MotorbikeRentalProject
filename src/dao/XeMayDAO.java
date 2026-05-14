package dao;

import dto.XeMayDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class XeMayDAO {
    // 1. Sửa hàm lấy danh sách (loại bỏ cột rental_price_per_hour)
    public ArrayList<XeMayDTO> layDanhSachXeMay() {
        ArrayList<XeMayDTO> ds = new ArrayList<>();
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
                // ĐÃ XÓA DÒNG: xe.setRentalPricePerHour(...);
                xe.setStatus(rs.getString("status"));
                xe.setRenterPhone(rs.getString("renter_phone"));
                ds.add(xe);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    // 2. Sửa hàm thêm xe (Chỉ còn 8 dấu ?)
    public boolean themXeMay(XeMayDTO xe) {
        String sql = "INSERT INTO VEHICLES (vehicle_code, brand, model, license_plate, color, manufacture_year, rental_price_per_day, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, xe.getVehicleCode());
            ps.setString(2, xe.getBrand());
            ps.setString(3, xe.getModel());
            ps.setString(4, xe.getLicensePlate());
            ps.setString(5, xe.getColor());
            ps.setInt(6, xe.getManufactureYear());
            ps.setDouble(7, xe.getRentalPricePerDay());
            ps.setString(8, xe.getStatus());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3. Sửa hàm sửa xe (Loại bỏ rental_price_per_hour)
    public boolean suaXeMay(XeMayDTO xe) {
        String sql = "UPDATE VEHICLES SET brand=?, model=?, license_plate=?, color=?, manufacture_year=?, rental_price_per_day=?, status=? WHERE vehicle_code=?";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, xe.getBrand());
            ps.setString(2, xe.getModel());
            ps.setString(3, xe.getLicensePlate());
            ps.setString(4, xe.getColor());
            ps.setInt(5, xe.getManufactureYear());
            ps.setDouble(6, xe.getRentalPricePerDay());
            ps.setString(7, xe.getStatus());
            ps.setString(8, xe.getVehicleCode());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}