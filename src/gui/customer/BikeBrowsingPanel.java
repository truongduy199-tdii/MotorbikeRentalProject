package gui.customer;

import bus.XeMayBUS;
import dto.XeMayDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class BikeBrowsingPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private XeMayBUS xeMayBUS;
    private CustomerMainFrame parentFrame;
    private ArrayList<XeMayDTO> currentList;

    public BikeBrowsingPanel(CustomerMainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.xeMayBUS = new XeMayBUS();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        initComponents();
        loadData();
    }

    private void initComponents() {
        // Tiêu đề
        JLabel lblTitle = new JLabel("DANH SÁCH XE CÓ SẴN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // Bảng dữ liệu
        String[] columns = {"ID", "Biển số", "Tên Xe", "Màu sắc", "Giá thuê/Ngày (VNĐ)"};
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

        // Nút hành động
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(245, 247, 250));
        JButton btnRent = new JButton("Thuê chiếc xe này");
        btnRent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRent.setBackground(new Color(40, 167, 69));
        btnRent.setForeground(Color.WHITE);

        btnRent.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một xe trong bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            XeMayDTO selectedBike = currentList.get(selectedRow);
            // Gọi Frame chính chuyển sang màn hình gửi yêu cầu
            parentFrame.showRentalRequest(selectedBike);
        });

        bottomPanel.add(btnRent);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        currentList = new ArrayList<>();
        ArrayList<XeMayDTO> allBikes = xeMayBUS.layDanhSachXeMay();

        for (XeMayDTO bike : allBikes) {
            // Chỉ hiển thị xe đang có sẵn (giả định status = "Có sẵn" hoặc "Available")
            if (bike.getStatus() != null && (bike.getStatus().equalsIgnoreCase("Có sẵn") || bike.getStatus().equalsIgnoreCase("Available"))) {
                currentList.add(bike);
                tableModel.addRow(new Object[]{
                        bike.getVehicleId(),
                        bike.getLicensePlate(),
                        bike.getVehicleName(),
                        bike.getColor(),
                        String.format("%,.0f", bike.getRentalPricePerDay())
                });
            }
        }
    }
}