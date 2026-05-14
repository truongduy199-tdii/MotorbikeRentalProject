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
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập và mật khẩu không được để trống.");
        }

        String hashedInputPassword = SecurityHelper.hashPassword(password);
        TaiKhoanDTO account = taiKhoanDAO.kiemTraDangNhap(username, hashedInputPassword);

        if (account == null) {
            throw new IllegalArgumentException("Sai tên đăng nhập hoặc mật khẩu.");
        }

        if ("BLOCKED".equalsIgnoreCase(account.getStatus()) || "INACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new IllegalArgumentException("Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Quản trị viên.");
        }

        return account;
    }

    public boolean dangKy(TaiKhoanDTO tk, String plainPassword, String rePassword) {
        if (tk.getUsername() == null || tk.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }
        if (plainPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu phải có ít nhất 6 ký tự để đảm bảo an toàn.");
        }
        if (!plainPassword.equals(rePassword)) {
            throw new IllegalArgumentException("Mật khẩu nhập lại không khớp!");
        }

        if (taiKhoanDAO.kiemTraTonTaiUsername(tk.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập '" + tk.getUsername() + "' đã tồn tại! Vui lòng chọn tên khác.");
        }

        String hashedPass = SecurityHelper.hashPassword(plainPassword);
        tk.setPassword(hashedPass);

        return taiKhoanDAO.themTaiKhoan(tk);
    }
}