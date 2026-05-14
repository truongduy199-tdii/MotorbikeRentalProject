package bus;

import dao.HopDongDAO;
import dto.HopDongDTO;
import java.util.ArrayList;

public class HopDongBUS {
    private HopDongDAO hopDongDAO;

    public HopDongBUS() {
        this.hopDongDAO = new HopDongDAO();
    }

    public ArrayList<HopDongDTO> layDanhSachHopDong() {
        return hopDongDAO.layDanhSachHopDong();
    }

    public ArrayList<HopDongDTO> layHopDongTheoUser(int userId) {
        return hopDongDAO.layHopDongTheoUser(userId);
    }

    // Chuyển dữ liệu yêu cầu thuê xuống DAO
    public boolean taoYeuCauThue(HopDongDTO hd) {
        return hopDongDAO.taoYeuCauThue(hd);
    }
}