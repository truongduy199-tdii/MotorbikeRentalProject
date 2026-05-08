package gui.admin;

import com.formdev.flatlaf.FlatClientProperties;
import bus.HopDongBUS;
import dto.HopDongDTO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ContractManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbStatusFilter;
    private HopDongBUS hopDongBUS = new HopDongBUS();

    public ContractManagementPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadDataFromDB(); // Tải dữ liệu giả để test giao diện
    }

    private void initComponents() {
        // --- 1. TIÊU ĐỀ (NORTH) ---
        JLabel lblTitle = new JLabel("QUẢN LÝ HỢP ĐỒNG & YÊU CẦU THUÊ XE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 43, 54));

        // --- 2. THANH CÔNG CỤ (Toolbar) ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbarPanel.setOpaque(false);

        // Ô tìm kiếm
        txtSearch = new JTextField(20);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm mã HĐ, tên khách...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        // Bộ lọc trạng thái
        String[] statusOptions = {"Tất cả trạng thái", "Pending (Chờ duyệt)", "Active (Đang thuê)", "Completed (Đã trả)"};
        cbStatusFilter = new JComboBox<>(statusOptions);

        // Các nút chức năng nghiệp vụ
        JButton btnApprove = createActionButton("Duyệt Yêu Cầu", new Color(46, 204, 113)); // Xanh lá - Chuyển Pending -> Active
        JButton btnReturn = createActionButton("Xác Nhận Trả Xe", new Color(52, 152, 219)); // Xanh dương - Chuyển Active -> Completed
        JButton btnCancel = createActionButton("Hủy Yêu Cầu", new Color(231, 76, 60));     // Đỏ
        JButton btnDetails = createActionButton("Xem Chi Tiết", new Color(243, 156, 18));   // Cam

        toolbarPanel.add(new JLabel("Tìm kiếm:"));
        toolbarPanel.add(txtSearch);
        toolbarPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbarPanel.add(new JLabel("Trạng thái:"));
        toolbarPanel.add(cbStatusFilter);
        toolbarPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        toolbarPanel.add(btnApprove);
        toolbarPanel.add(btnReturn);
        toolbarPanel.add(btnCancel);
        toolbarPanel.add(btnDetails);

        topPanel.add(toolbarPanel, BorderLayout.CENTER);

        // --- 3. BẢNG DỮ LIỆU HỢP ĐỒNG (CENTER) ---
        String[] columns = {"Mã HĐ", "Khách Hàng", "Tên Xe", "Ngày Thuê", "Dự Kiến Trả", "Tiền Cọc", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Khóa chỉnh sửa trực tiếp trên bảng
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(236, 240, 241));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // --- 4. GẮN VÀO PANEL CHÍNH ---
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // Nạp dữ liệu giả (Mock data)
    private void loadDataFromDB() {
        tableModel.setRowCount(0);

        // Gọi xuống CSDL thông qua BUS
        ArrayList<HopDongDTO> danhSach = hopDongBUS.layDanhSachHopDong();

        // Định dạng ngày giờ hiển thị
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (HopDongDTO hd : danhSach) {
            // Format tiền tệ cho đẹp (Ví dụ: 500000 -> 500,000)
            String formattedDeposit = String.format("%,.0f VNĐ", hd.getDepositAmount());

            // Chuyển ngày giờ sang dạng String
            String startStr = hd.getRentalStart() != null ? sdf.format(hd.getRentalStart()) : "";
            String endStr = hd.getRentalEnd() != null ? sdf.format(hd.getRentalEnd()) : "";

            // Đưa từng dòng dữ liệu thật vào bảng
            Object[] row = {
                    hd.getContractCode(),
                    hd.getCustomerName(),
                    hd.getVehicleName(),
                    startStr,
                    endStr,
                    formattedDeposit,
                    hd.getContractStatus()
            };
            tableModel.addRow(row);
        }
    }

    // Hàm tiện ích tạo nút chức năng (Giống với BikeManagementPanel để đồng bộ UI)
    private JButton createActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hiệu ứng Hover
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