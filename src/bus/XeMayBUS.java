package bus;

import dao.XeMayDAO;
import dto.XeMayDTO;
import java.util.ArrayList;

public class XeMayBUS {
    private XeMayDAO xeMayDAO;

    public XeMayBUS() {
        this.xeMayDAO = new XeMayDAO();
    }

    public ArrayList<XeMayDTO> layDanhSachXeMay() {
        return xeMayDAO.layDanhSachXeMay();
    }
}