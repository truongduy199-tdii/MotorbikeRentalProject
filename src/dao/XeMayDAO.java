package dao;

import dto.XeMayDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class XeMayDAO {
    // Thêm hàm này vào trong class XeMayDAO
    public ArrayList<XeMayDTO> timKiemXeMay(String keyword, String status) {
        ArrayList<XeMayDTO> list = new ArrayList<>();

        // Sử dụng StringBuilder để linh hoạt nối chuỗi SQL tùy thuộc vào điều kiện tìm kiếm
        StringBuilder sql = new StringBuilder(
                "SELECT v.vehicle_id, v.vehicle_code, v.brand, v.model, CONCAT(v.brand, ' ', v.model) AS vehicle_name, " +
                        "v.license_plate, v.color, v.manufacture_year, v.rental_price_per_day, v.status, " +
                        "(SELECT c.phone FROM RENTAL_CONTRACTS r " +
                        " JOIN CUSTOMERS c ON r.customer_id = c.customer_id " +
                        " WHERE r.vehicle_id = v.vehicle_id AND r.contract_status = 'ACTIVE' " +
                        " ORDER BY r.contract_id DESC LIMIT 1) AS renter_phone " +
                        "FROM VEHICLES v WHERE 1=1 "
        );

        // Nối thêm điều kiện Keyword nếu người dùng có nhập
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(v.vehicle_code) LIKE ? OR LOWER(CONCAT(v.brand, ' ', v.model)) LIKE ? OR LOWER(v.license_plate) LIKE ?) ");
        }

        // Nối thêm điều kiện Trạng thái nếu người dùng chọn trạng thái cụ thể
        if (status != null && !status.equals("Tất cả trạng thái")) {
            sql.append(" AND v.status = ? ");
        }

        sql.append(" ORDER BY v.vehicle_code ASC");

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // Truyền giá trị cho các dấu ? trong câu SQL
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKey = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(paramIndex++, searchKey); // Cho mã xe
                ps.setString(paramIndex++, searchKey); // Cho tên xe
                ps.setString(paramIndex++, searchKey); // Cho biển số
            }

            if (status != null && !status.equals("Tất cả trạng thái")) {
                ps.setString(paramIndex++, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    XeMayDTO xe = new XeMayDTO();
                    xe.setVehicleId(rs.getInt("vehicle_id"));
                    xe.setVehicleCode(rs.getString("vehicle_code"));
                    xe.setBrand(rs.getString("brand"));
                    xe.setModel(rs.getString("model"));
                    xe.setVehicleName(rs.getString("vehicle_name"));
                    xe.setLicensePlate(rs.getString("license_plate"));
                    xe.setColor(rs.getString("color"));
                    xe.setManufactureYear(rs.getInt("manufacture_year"));
                    xe.setRentalPricePerDay(rs.getDouble("rental_price_per_day"));

                    String dbStatus = rs.getString("status");
                    xe.setStatus(dbStatus);
                    if ("RENTED".equals(dbStatus)) {
                        xe.setRenterPhone(rs.getString("renter_phone"));
                    } else {
                        xe.setRenterPhone(null);
                    }
                    list.add(xe);
                }
            }
        } catch (Exception e) {
            // Ném lỗi lên trên để GUI bắt và hiển thị JOptionPane
            throw new RuntimeException("Lỗi truy vấn tìm kiếm cơ sở dữ liệu: " + e.getMessage(), e);
        }
        return list;
    }

    public ArrayList<XeMayDTO> layDanhSachXeMay() {
        ArrayList<XeMayDTO> list = new ArrayList<>();

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
                xe.setVehicleId(rs.getInt("vehicle_id"));
                xe.setVehicleCode(rs.getString("vehicle_code"));
                xe.setBrand(rs.getString("brand"));
                xe.setModel(rs.getString("model"));
                xe.setVehicleName(rs.getString("vehicle_name"));
                xe.setLicensePlate(rs.getString("license_plate"));
                xe.setColor(rs.getString("color"));
                xe.setManufactureYear(rs.getInt("manufacture_year"));

                double pricePerDay = rs.getDouble("rental_price_per_day");
                xe.setRentalPricePerDay(pricePerDay);
                xe.setRentalPricePerHour(pricePerDay / 10);

                String status = rs.getString("status");
                xe.setStatus(status);

                if ("RENTED".equals(status)) {
                    xe.setRenterPhone(rs.getString("renter_phone"));
                } else {
                    xe.setRenterPhone(null);
                }
                list.add(xe);
            }
        } catch (Exception e) {
            // THAY THẾ e.printStackTrace() bằng việc ném ngoại lệ
            throw new RuntimeException("Lỗi khi lấy danh sách xe máy từ CSDL: " + e.getMessage(), e);
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
            ps.setInt(6, xe.getManufactureYear());
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
            ps.setInt(6, xe.getManufactureYear());
            ps.setDouble(6, xe.getRentalPricePerDay());
            ps.setString(7, xe.getStatus());
            ps.setString(8, xe.getVehicleCode());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}