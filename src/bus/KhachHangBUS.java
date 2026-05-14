package bus;

import dao.KhachHangDAO;
import dto.KhachHangDTO;
import java.util.ArrayList;

public class KhachHangBUS {
    private KhachHangDAO khachHangDAO;

    public KhachHangBUS() {
        this.khachHangDAO = new KhachHangDAO();
    }

    // Dành cho Admin
    public ArrayList<KhachHangDTO> getAllCustomers() {
        return khachHangDAO.getAllCustomers();
    }

    // Dành cho Customer
    public KhachHangDTO layThongTinTheoUserId(int userId) {
        return khachHangDAO.layThongTinTheoUserId(userId);
    }

    public boolean capNhatThongTin(KhachHangDTO kh) {
        return khachHangDAO.capNhatThongTin(kh);
    }

    public boolean doiMatKhau(int userId, String oldPassHash, String newPassHash) {
        return khachHangDAO.doiMatKhau(userId, oldPassHash, newPassHash);
    }
}