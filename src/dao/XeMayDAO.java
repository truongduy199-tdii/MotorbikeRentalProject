package dao;

import dto.XeMayDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class XeMayDAO {

    public ArrayList<XeMayDTO> layDanhSachXeMay() {
        ArrayList<XeMayDTO> list = new ArrayList<>(); // Dùng chung biến list của Nam

        // KẾT HỢP SQL: Gộp subquery lấy SĐT (Long) và chọn cột/sắp xếp (Nam)
        String sql = "SELECT v.vehicle_id, v.vehicle_code, v.brand, v.model, CONCAT(v.brand, ' ', v.model) AS vehicle_name, " +
                "v.license_plate, v.color, v.manufacture_year, v.rental_price_per_day, v.status, " +
                "(SELECT c.phone FROM RENTAL_CONTRACTS r " +
                " JOIN CUSTOMERS c ON r.customer_id = c.customer_id " +
                " WHERE r.vehicle_id = v.vehicle_id AND r.contract_status = 'ACTIVE' " +
                " ORDER BY r.contract_id DESC LIMIT 1) AS renter_phone " +
                "FROM VEHICLES v ORDER BY v.vehicle_code ASC";

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                XeMayDTO xe = new XeMayDTO();
                
                // Các trường cơ bản (Của Nam)
                xe.setVehicleId(rs.getInt("vehicle_id"));
                xe.setVehicleCode(rs.getString("vehicle_code"));
                xe.setBrand(rs.getString("brand"));
                xe.setModel(rs.getString("model"));
                xe.setVehicleName(rs.getString("vehicle_name"));
                xe.setLicensePlate(rs.getString("license_plate"));
                xe.setColor(rs.getString("color"));
                xe.setManufactureYear(rs.getInt("manufacture_year")); // Của cả 2

                // Logic giá tiền (Của Nam)
                double pricePerDay = rs.getDouble("rental_price_per_day");
                xe.setRentalPricePerDay(pricePerDay);
                xe.setRentalPricePerHour(pricePerDay / 10); 

                // Logic trạng thái
                String status = rs.getString("status");
                xe.setStatus(status);

                // Logic SĐT khách thuê (Của Long)
                if ("RENTED".equals(status)) {
                    xe.setRenterPhone(rs.getString("renter_phone"));
                } else {
                    xe.setRenterPhone(null); // File DTO tự động hiển thị "---"
                }

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