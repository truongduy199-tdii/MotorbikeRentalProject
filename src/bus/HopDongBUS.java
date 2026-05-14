package bus;

import dao.HopDongDAO;
import dto.HopDongDTO;
import java.util.ArrayList;

public class HopDongBUS {
    private HopDongDAO hopDongDAO;
    private final double TIEN_COC_CO_DINH = 1000000.0; // 1 Triệu VNĐ Cố định

    public HopDongBUS() {
        this.hopDongDAO = new HopDongDAO();
    }

    public ArrayList<HopDongDTO> layDanhSachHopDong() {
        return hopDongDAO.layDanhSachHopDong();
    }

    public ArrayList<HopDongDTO> timKiemHopDong(String keyword, String status) {
        return hopDongDAO.timKiemHopDong(keyword, status);
    }

    // Customer: Lấy danh sách lịch sử thuê xe
    public ArrayList<HopDongDTO> layHopDongTheoUser(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("ID người dùng không hợp lệ.");
        }
        return hopDongDAO.layHopDongTheoUser(userId);
    }

    public double layTongDoanhThu() {
        return hopDongDAO.layTongDoanhThu();
    }

    public int layTongSoKhachHang() {
        return hopDongDAO.layTongSoKhachHang();
    }

    public int layTongSoXe() {
        return hopDongDAO.layTongSoXe();
    }

    public int laySoXeDangThue() {
        return hopDongDAO.laySoXeDangThue();
    }

    public boolean duyetYeuCau(String contractCode, String currentStatus) {
        if (!currentStatus.equals("PENDING")) {
            throw new IllegalArgumentException("Chỉ có thể duyệt hợp đồng đang ở trạng thái 'PENDING' (Chờ duyệt).");
        }
        return hopDongDAO.thayDoiTrangThaiHopDong(contractCode, "ACTIVE", "RENTED");
    }

    public boolean traXe(String contractCode, String currentStatus) {
        if (!currentStatus.equals("ACTIVE")) {
            throw new IllegalArgumentException("Chỉ có thể xác nhận trả xe cho hợp đồng đang 'ACTIVE' (Đang thuê).");
        }

        return hopDongDAO.thayDoiTrangThaiHopDong(contractCode, "COMPLETED", "AVAILABLE");
    }

    public boolean huyHopDong(String contractCode, String currentStatus) {
        if (currentStatus.equals("COMPLETED") || currentStatus.equals("CANCELLED")) {
            throw new IllegalArgumentException("Không thể hủy hợp đồng đã hoàn tất hoặc đã hủy.");
        }
        return hopDongDAO.thayDoiTrangThaiHopDong(contractCode, "CANCELLED", "AVAILABLE");
    }

    public boolean taoYeuCauThue(HopDongDTO hd) {
        if (hd.getRentalEnd().before(hd.getRentalStart())) {
            throw new IllegalArgumentException("Ngày kết thúc phải lớn hơn ngày bắt đầu thuê.");
        }
        // Gắn cứng tiền cọc 1 triệu
        hd.setDepositAmount(TIEN_COC_CO_DINH);
        return hopDongDAO.taoYeuCauThue(hd);
    }

    public boolean capNhatHopDong(HopDongDTO hd) {
        if (hd.getDepositAmount() < 0) {
            throw new IllegalArgumentException("Tiền cọc không được là số âm!");
        }
        if (hd.getTotalAmount() < 0) {
            throw new IllegalArgumentException("Tổng tiền không được là số âm!");
        }
        // Gọi xuống DAO để lưu vào CSDL
        return hopDongDAO.capNhatHopDong(hd);
    }


}