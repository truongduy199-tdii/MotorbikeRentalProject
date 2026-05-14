package bus;

import dao.KhachHangDAO;
import dto.KhachHangDTO;
import java.util.ArrayList;

public class KhachHangBUS {
    private KhachHangDAO khachHangDAO;

    public KhachHangBUS() {
        this.khachHangDAO = new KhachHangDAO();
    }

    public ArrayList<KhachHangDTO> getAllCustomers() {
        return khachHangDAO.getAllCustomers();
    }

    public ArrayList<KhachHangDTO> timKiemKhachHang(String keyword, String status) {
        return khachHangDAO.timKiemKhachHang(keyword, status);
    }

    private void kiemTraDuLieu(KhachHangDTO kh) throws IllegalArgumentException {
        if (kh.getFullName() == null || kh.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống.");
        }
        if (kh.getPhone() == null || kh.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        if (!kh.getPhone().matches("^0\\d{9}$")) { // Validate cơ bản SĐT Việt Nam
            throw new IllegalArgumentException("Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 chữ số.");
        }
        if (kh.getCccd() == null || kh.getCccd().trim().isEmpty()) {
            throw new IllegalArgumentException("CCCD không được để trống.");
        }
        if (!kh.getCccd().matches("^\\d{12}$")) {
            throw new IllegalArgumentException("CCCD phải là dãy số gồm đúng 12 chữ số.");
        }
    }

    public boolean suaKhachHang(KhachHangDTO kh) {
        kiemTraDuLieu(kh);
        return khachHangDAO.suaKhachHang(kh);
    }

    public boolean xoaKhachHang(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ.");
        }
        return khachHangDAO.xoaKhachHang(customerId);
    }

    public KhachHangDTO layThongTinTheoUserId(int userId) {
        return khachHangDAO.layThongTinTheoUserId(userId);
    }

    public boolean capNhatThongTin(KhachHangDTO kh) {
        if (kh.getPhone() == null || kh.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        if (!kh.getPhone().matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 chữ số.");
        }
        return khachHangDAO.capNhatThongTin(kh);
    }

    public boolean doiMatKhau(int userId, String oldPassHash, String newPassHash) {
        if (newPassHash == null || newPassHash.trim().isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống.");
        }
        return khachHangDAO.doiMatKhau(userId, oldPassHash, newPassHash);
    }

    public boolean themMoiHoSo(KhachHangDTO kh) {
        kiemTraDuLieu(kh);
        return khachHangDAO.themMoiKhachHang(kh);
    }




}