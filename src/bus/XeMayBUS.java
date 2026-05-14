package bus;

import dao.XeMayDAO;
import dto.XeMayDTO;
import java.util.ArrayList;

public class XeMayBUS {
    private XeMayDAO xeMayDAO;

    public XeMayBUS() {
        this.xeMayDAO = new XeMayDAO();
    }

    // Thêm vào trong class XeMayBUS
    public boolean themXeMay(XeMayDTO xe) {
        // Có thể thêm Validator kiểm tra rỗng ở đây
        return xeMayDAO.themXeMay(xe);
    }

    public boolean suaXeMay(XeMayDTO xe) {
        return xeMayDAO.suaXeMay(xe);
    }

    public ArrayList<XeMayDTO> layDanhSachXeMay() {
        return xeMayDAO.layDanhSachXeMay();
    }
}