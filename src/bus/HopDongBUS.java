package bus;

import dao.HopDongDAO;
import dto.HopDongDTO;
import java.util.ArrayList;

public class HopDongBUS {
    private HopDongDAO hopDongDAO;

    public HopDongBUS() {
        this.hopDongDAO = new HopDongDAO();
    }

    // Trả về danh sách để GUI hiển thị
    public ArrayList<HopDongDTO> layDanhSachHopDong() {
        return hopDongDAO.layDanhSachHopDong();
    }


}