package bus;

import dao.TaiKhoanDAO;
import dto.TaiKhoanDTO;
import utils.SecurityHelper;

public class TaiKhoanBUS {
    private TaiKhoanDAO taiKhoanDAO;

    public TaiKhoanBUS() {
        this.taiKhoanDAO = new TaiKhoanDAO();
    }

    public TaiKhoanDTO kiemTraDangNhap(String username, String password) {
        // 1. Mã hóa mật khẩu người dùng nhập vào
        String hashedInputPassword = SecurityHelper.hashPassword(password);

        // 2. So sánh với database
        return taiKhoanDAO.kiemTraDangNhap(username, hashedInputPassword);
    }
}