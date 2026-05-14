package dao;

import dto.HopDongDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class HopDongDAO {

    public ArrayList<HopDongDTO> layDanhSachHopDong() {
        ArrayList<HopDongDTO> list = new ArrayList<>();
        String sql = "SELECT r.contract_code, c.full_name, r.vehicle_id, CONCAT(v.brand, ' ', v.model) AS vehicle_name, " +
                "r.rental_start, r.rental_end, r.deposit_amount, r.total_amount, r.contract_status " +
                "FROM RENTAL_CONTRACTS r " +
                "JOIN CUSTOMERS c ON r.customer_id = c.customer_id " +
                "JOIN VEHICLES v ON r.vehicle_id = v.vehicle_id " +
                "ORDER BY r.rental_start DESC";

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                HopDongDTO hd = new HopDongDTO();
                hd.setContractCode(rs.getString("contract_code"));
                hd.setCustomerName(rs.getString("full_name"));

                hd.setVehicleId(rs.getInt("vehicle_id"));
                hd.setVehicleName(rs.getString("vehicle_name"));
                hd.setRentalStart(rs.getTimestamp("rental_start"));
                hd.setRentalEnd(rs.getTimestamp("rental_end"));
                hd.setDepositAmount(rs.getDouble("deposit_amount"));
                hd.setTotalAmount(rs.getDouble("total_amount"));
                hd.setContractStatus(rs.getString("contract_status"));
                list.add(hd);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi lấy danh sách hợp đồng: " + e.getMessage(), e);
        }
        return list;
    }

    public ArrayList<HopDongDTO> timKiemHopDong(String keyword, String status) {
        ArrayList<HopDongDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT r.contract_code, c.full_name, r.vehicle_id, CONCAT(v.brand, ' ', v.model) AS vehicle_name, " +
                        "r.rental_start, r.rental_end, r.deposit_amount, r.total_amount, r.contract_status " +
                        "FROM RENTAL_CONTRACTS r " +
                        "JOIN CUSTOMERS c ON r.customer_id = c.customer_id " +
                        "JOIN VEHICLES v ON r.vehicle_id = v.vehicle_id WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (LOWER(r.contract_code) LIKE ? OR LOWER(c.full_name) LIKE ?) ");
        }
        if (status != null && !status.equals("Tất cả trạng thái")) {
            sql.append(" AND r.contract_status = ? ");
        }
        sql.append(" ORDER BY r.rental_start DESC");

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKey = "%" + keyword.trim().toLowerCase() + "%";
                ps.setString(paramIndex++, searchKey);
                ps.setString(paramIndex++, searchKey);
            }
            if (status != null && !status.equals("Tất cả trạng thái")) {
                ps.setString(paramIndex++, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HopDongDTO hd = new HopDongDTO();
                    hd.setContractCode(rs.getString("contract_code"));
                    hd.setCustomerName(rs.getString("full_name"));
                    hd.setVehicleId(rs.getInt("vehicle_id"));
                    hd.setVehicleName(rs.getString("vehicle_name"));
                    hd.setRentalStart(rs.getTimestamp("rental_start"));
                    hd.setRentalEnd(rs.getTimestamp("rental_end"));
                    hd.setDepositAmount(rs.getDouble("deposit_amount"));
                    hd.setTotalAmount(rs.getDouble("total_amount"));
                    hd.setContractStatus(rs.getString("contract_status"));
                    list.add(hd);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tìm kiếm hợp đồng: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean thayDoiTrangThaiHopDong(String contractCode, String newContractStatus, String newVehicleStatus) {
        String sqlContract = "UPDATE RENTAL_CONTRACTS SET contract_status = ?, " +
                "actual_return_time = CASE WHEN ? = 'COMPLETED' THEN NOW() ELSE actual_return_time END " +
                "WHERE contract_code = ?";

        String sqlVehicle = "UPDATE VEHICLES SET status = ? WHERE vehicle_id = (SELECT vehicle_id FROM RENTAL_CONTRACTS WHERE contract_code = ?)";

        Connection conn = null;
        try {
            conn = MySQLConnect.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psContract = conn.prepareStatement(sqlContract);
                 PreparedStatement psVehicle = conn.prepareStatement(sqlVehicle)) {

                psContract.setString(1, newContractStatus);
                psContract.setString(2, newContractStatus);
                psContract.setString(3, contractCode);
                psContract.executeUpdate();

                psVehicle.setString(1, newVehicleStatus);
                psVehicle.setString(2, contractCode);
                psVehicle.executeUpdate();

                conn.commit();
                return true;
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đồng bộ trạng thái Hợp đồng & Xe: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public ArrayList<HopDongDTO> layHopDongTheoUser(int userId) {
        ArrayList<HopDongDTO> list = new ArrayList<>();
        String sql = "SELECT r.contract_code, r.vehicle_id, CONCAT(v.brand, ' ', v.model) AS vehicle_name, " +
                "r.rental_start, r.rental_end, r.deposit_amount, r.total_amount, r.contract_status " +
                "FROM RENTAL_CONTRACTS r " +
                "JOIN CUSTOMERS c ON r.customer_id = c.customer_id " +
                "JOIN VEHICLES v ON r.vehicle_id = v.vehicle_id " +
                "WHERE c.user_id = ? ORDER BY r.rental_start DESC";

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HopDongDTO hd = new HopDongDTO();
                    hd.setContractCode(rs.getString("contract_code"));
                    hd.setVehicleId(rs.getInt("vehicle_id"));
                    hd.setVehicleName(rs.getString("vehicle_name"));
                    hd.setRentalStart(rs.getTimestamp("rental_start"));
                    hd.setRentalEnd(rs.getTimestamp("rental_end"));
                    hd.setDepositAmount(rs.getDouble("deposit_amount"));
                    hd.setTotalAmount(rs.getDouble("total_amount"));
                    hd.setContractStatus(rs.getString("contract_status"));
                    list.add(hd);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi lấy lịch sử hợp đồng của khách hàng: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean taoYeuCauThue(HopDongDTO hd) {
        String sql = "INSERT INTO RENTAL_CONTRACTS (contract_code, customer_id, vehicle_id, created_by, " +
                "rental_start, rental_end, deposit_amount, total_amount, contract_status) " +
                "VALUES (?, (SELECT customer_id FROM CUSTOMERS WHERE user_id = ?), ?, 1, ?, ?, ?, ?, 'PENDING')";

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "HD" + (System.currentTimeMillis() % 1000000));
            ps.setInt(2, hd.getCustomerId());
            ps.setInt(3, hd.getVehicleId());
            ps.setTimestamp(4, hd.getRentalStart());
            ps.setTimestamp(5, hd.getRentalEnd());
            ps.setDouble(6, hd.getDepositAmount());
            ps.setDouble(7, hd.getTotalAmount());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo yêu cầu thuê xe xuống CSDL: " + e.getMessage(), e);
        }
    }

    public double layTongDoanhThu() {
        String sql = "SELECT SUM(total_amount) FROM RENTAL_CONTRACTS WHERE contract_status = 'COMPLETED'";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tính tổng doanh thu: " + e.getMessage(), e);
        }
        return 0;
    }

    public int layTongSoKhachHang() {
        String sql = "SELECT COUNT(*) FROM CUSTOMERS WHERE status = 'ACTIVE'";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm số khách hàng: " + e.getMessage(), e);
        }
        return 0;
    }

    public int layTongSoXe() {
        String sql = "SELECT COUNT(*) FROM VEHICLES WHERE status != 'DELETED'";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm tổng số xe: " + e.getMessage(), e);
        }
        return 0;
    }

    public int laySoXeDangThue() {
        String sql = "SELECT COUNT(*) FROM VEHICLES WHERE status = 'RENTED'";
        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm số xe đang thuê: " + e.getMessage(), e);
        }
        return 0;
    }

    public boolean capNhatHopDong(HopDongDTO hd) {
        String sql = "UPDATE RENTAL_CONTRACTS SET deposit_amount = ?, total_amount = ?, contract_status = ? WHERE contract_code = ?";

        try (Connection conn = MySQLConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, hd.getDepositAmount());
            ps.setDouble(2, hd.getTotalAmount());
            ps.setString(3, hd.getContractStatus());
            ps.setString(4, hd.getContractCode());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật hợp đồng: " + e.getMessage(), e);
        }
    }
}