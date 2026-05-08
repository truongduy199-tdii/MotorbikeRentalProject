package bus;

import dao.KhachHangDAO;
import dto.KhachHangDTO;
import java.util.List;
import java.util.ArrayList;

public class KhachHangBUS {
    private KhachHangDAO khachHangDAO;

    public KhachHangBUS() {
        // Rất nhiều bạn quên dòng này dẫn đến lỗi NullPointerException
        khachHangDAO = new KhachHangDAO();
    }

    public List<KhachHangDTO> getAllCustomers() {
        List<KhachHangDTO> list = khachHangDAO.getAllCustomers();
        // Tránh lỗi null bảo vệ GUI
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }
}