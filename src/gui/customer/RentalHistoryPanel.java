package gui.customer;

import bus.HopDongBUS;
import dto.HopDongDTO;
import utils.SessionUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RentalHistoryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private HopDongBUS hopDongBUS;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public RentalHistoryPanel() {
        this.hopDongBUS = new HopDongBUS();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        initComponents();
        loadData();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("LỊCH SỬ THUÊ XE CỦA BẠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Mã HĐ", "Tên Xe", "Ngày bắt đầu", "Ngày kết thúc", "Tiền cọc/Tổng", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);

        // Lấy tất cả hợp đồng
        ArrayList<HopDongDTO> allContracts = hopDongBUS.layDanhSachHopDong();

        // Lấy tên/username khách đang đăng nhập để lọc
        String currentUser = SessionUser.getCurrentUser() != null ? SessionUser.getCurrentUser().getUsername() : "";

        for (HopDongDTO hd : allContracts) {
            // Lọc: Chỉ hiển thị những hợp đồng thuộc về user đang đăng nhập
            if (hd.getCustomerName() != null && hd.getCustomerName().equalsIgnoreCase(currentUser)) {

                String startStr = hd.getRentalStart() != null ? dateFormat.format(hd.getRentalStart()) : "N/A";
                String endStr = hd.getRentalEnd() != null ? dateFormat.format(hd.getRentalEnd()) : "N/A";

                tableModel.addRow(new Object[]{
                        hd.getContractCode(),
                        hd.getVehicleName(),
                        startStr,
                        endStr,
                        String.format("%,.0f VNĐ", hd.getDepositAmount()),
                        hd.getContractStatus()
                });
            }
        }
    }
}