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

    public ArrayList<XeMayDTO> timKiemXeMay(String keyword, String status) {
        return xeMayDAO.timKiemXeMay(keyword, status);
    }

    private void kiemTraDuLieu(XeMayDTO xe) throws IllegalArgumentException {
        if (xe == null) {
            throw new IllegalArgumentException("Dữ liệu xe máy không được để trống.");
        }
        if (xe.getVehicleCode() == null || xe.getVehicleCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã xe không được để trống.");
        }
        if (xe.getBrand() == null || xe.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("Hãng xe không được để trống.");
        }
        if (xe.getLicensePlate() == null || xe.getLicensePlate().trim().isEmpty()) {
            throw new IllegalArgumentException("Biển số xe không được để trống.");
        }
        if (xe.getRentalPricePerDay() <= 0) {
            throw new IllegalArgumentException("Giá thuê một ngày phải lớn hơn 0.");
        }
        if (xe.getManufactureYear() < 2000) {
            throw new IllegalArgumentException("Năm sản xuất phải từ năm 2000 trở đi.");
        }
    }

    public boolean themXeMay(XeMayDTO xe) {
        kiemTraDuLieu(xe);
        return xeMayDAO.themXeMay(xe);
    }

    public boolean suaXeMay(XeMayDTO xe) {
        kiemTraDuLieu(xe);
        return xeMayDAO.suaXeMay(xe);
    }
}