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

    // Dành cho Customer: Lấy danh sách lịch sử thuê xe
    public ArrayList<HopDongDTO> layHopDongTheoUser(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("ID người dùng không hợp lệ.");
        }
        return hopDongDAO.layHopDongTheoUser(userId);
    }

    // ================= CÁC HÀM THỐNG KÊ (DASHBOARD) =================

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

    // 1. NGHIỆP VỤ DUYỆT YÊU CẦU
    public boolean duyetYeuCau(String contractCode, String currentStatus) {
        if (!currentStatus.equals("PENDING")) {
            throw new IllegalArgumentException("Chỉ có thể duyệt hợp đồng đang ở trạng thái 'PENDING' (Chờ duyệt).");
        }
        // HĐ -> ACTIVE, Xe -> RENTED
        return hopDongDAO.thayDoiTrangThaiHopDong(contractCode, "ACTIVE", "RENTED");
    }

    // 2. NGHIỆP VỤ TRẢ XE
    public boolean traXe(String contractCode, String currentStatus) {
        if (!currentStatus.equals("ACTIVE")) {
            throw new IllegalArgumentException("Chỉ có thể xác nhận trả xe cho hợp đồng đang 'ACTIVE' (Đang thuê).");
        }
        // HĐ -> COMPLETED, Xe -> AVAILABLE
        return hopDongDAO.thayDoiTrangThaiHopDong(contractCode, "COMPLETED", "AVAILABLE");
    }

    // 3. NGHIỆP VỤ HỦY HỢP ĐỒNG
    public boolean huyHopDong(String contractCode, String currentStatus) {
        if (currentStatus.equals("COMPLETED") || currentStatus.equals("CANCELLED")) {
            throw new IllegalArgumentException("Không thể hủy hợp đồng đã hoàn tất hoặc đã hủy.");
        }
        // HĐ -> CANCELLED, Xe -> AVAILABLE
        return hopDongDAO.thayDoiTrangThaiHopDong(contractCode, "CANCELLED", "AVAILABLE");
    }

    // 4. KIỂM TRA LOGIC KHI TẠO MỚI HỢP ĐỒNG (Chuẩn hóa ý của Bro)
    public boolean taoYeuCauThue(HopDongDTO hd) {
        if (hd.getRentalEnd().before(hd.getRentalStart())) {
            throw new IllegalArgumentException("Ngày kết thúc phải lớn hơn ngày bắt đầu thuê.");
        }
        // Gắn cứng tiền cọc là 1 triệu
        hd.setDepositAmount(TIEN_COC_CO_DINH);

        // (Tùy chọn) Tính tổng tiền dựa trên chênh lệch ngày nếu bro lấy được giá xe
        // long diff = hd.getRentalEnd().getTime() - hd.getRentalStart().getTime();
        // int days = (int) Math.ceil((double)diff / (1000*60*60*24));
        // hd.setTotalAmount(TIEN_COC_CO_DINH + (days * giaXeMotNgay));

        return hopDongDAO.taoYeuCauThue(hd);
    }


}