package gui.admin;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import bus.XeMayBUS;
import dto.XeMayDTO;

public class BikeManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbStatusFilter;

    private XeMayBUS xeMayBUS = new XeMayBUS();

    public BikeManagementPanel() {
        setLayout(new BorderLayout(20, 20)); // Khoảng cách giữa các vùng
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding cho toàn màn hình

        initComponents();
        loadDataFromDB();
    }

    private void initComponents() {
        // --- 1. Tiêu đề (NORTH) ---
        JLabel lblTitle = new JLabel("QUẢN LÝ DANH MỤC XE MÁY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 43, 54));

        // --- 2. Thanh Công Cụ - Toolbar ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false); // Xuyên thấu để thấy nền xám
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbarPanel.setOpaque(false);

        // Ô tìm kiếm
        txtSearch = new JTextField(20);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm theo tên, biển số...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        // Bộ lọc trạng thái
        String[] statusOptions = {"Tất cả trạng thái", "Available", "Rented", "Maintenance"};
        cbStatusFilter = new JComboBox<>(statusOptions);

        // Các nút chức năng
        JButton btnAdd = createActionButton("Thêm Mới", new Color(46, 204, 113)); // Xanh lá
        JButton btnEdit = createActionButton("Cập Nhật", new Color(52, 152, 219)); // Xanh dương
        JButton btnDelete = createActionButton("Xóa", new Color(231, 76, 60));     // Đỏ
        JButton btnMaintain = createActionButton("Bảo Trì", new Color(241, 196, 15)); // Vàng

        toolbarPanel.add(new JLabel("Tìm kiếm:"));
        toolbarPanel.add(txtSearch);
        toolbarPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbarPanel.add(new JLabel("Trạng thái:"));
        toolbarPanel.add(cbStatusFilter);
        toolbarPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        toolbarPanel.add(btnAdd);
        toolbarPanel.add(btnEdit);
        toolbarPanel.add(btnDelete);
        toolbarPanel.add(btnMaintain);

        topPanel.add(toolbarPanel, BorderLayout.CENTER);

        // --- 3. Bảng Dữ Liệu (CENTER) ---
        String[] columns = {"Mã Xe", "Tên Xe", "Biển Số", "Loại Xe", "Giá Thuê/Ngày", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa trực tiếp trên bảng
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35); // Chiều cao dòng thoải mái
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false); // Ẩn đường kẻ dọc để giao diện thoáng hơn (phong cách hiện đại)

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220))); // Viền nhẹ

        // Gắn vào giao diện chính
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Nạp dữ liệu giả
    private void loadDataFromDB() {
        tableModel.setRowCount(0); // Làm sạch bảng trước khi tải lại

        ArrayList<XeMayDTO> listXe = xeMayBUS.layDanhSachXeMay();

        for (XeMayDTO xe : listXe) {
            // Định dạng giá tiền (Ví dụ: 150000 -> 150,000 VNĐ)
            String formattedPrice = String.format("%,.0f VNĐ", xe.getRentalPricePerDay());

            Object[] row = {
                    xe.getVehicleCode(),
                    xe.getVehicleName(),
                    xe.getLicensePlate(),
                    formattedPrice,
                    xe.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    // Hàm tiện ích tạo nút chức năng
    private JButton createActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hiệu ứng Hover làm tối màu đi một chút
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }
}