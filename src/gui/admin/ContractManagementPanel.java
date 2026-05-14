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
    private JButton btnSearch;
    private HopDongBUS hopDongBUS = new HopDongBUS();

    public ContractManagementPanel () {
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
        String[] statusOptions = {"Tất cả trạng thái", "Pending (Chờ duyệt)", "Active (Đang thuê)", "Completed (Đã trả)", "Cancelled (Đã hủy)"};
        cbStatusFilter = new JComboBox<>(statusOptions);

        // ---> BỔ SUNG CODE TẠO NÚT TÌM KIẾM Ở ĐÂY <---
        btnSearch = createActionButton("Tìm", new Color(25, 118, 210)); // Màu xanh dương

        // Các nút chức năng nghiệp vụ
        JButton btnApprove = createActionButton("Duyệt Yêu Cầu", new Color(46, 204, 113));
        JButton btnReturn = createActionButton("Xác Nhận Trả Xe", new Color(52, 152, 219));
        JButton btnCancel = createActionButton("Hủy Yêu Cầu", new Color(231, 76, 60));
        JButton btnDetails = createActionButton("Xem Chi Tiết", new Color(243, 156, 18));

        toolbarPanel.add(new JLabel("Tìm kiếm:"));
        toolbarPanel.add(txtSearch);
        toolbarPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbarPanel.add(new JLabel("Trạng thái:"));
        toolbarPanel.add(cbStatusFilter);

        // ---> THÊM NÚT TÌM VÀO THANH CÔNG CỤ <---
        toolbarPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbarPanel.add(btnSearch);

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

        // ---> GỌI HÀM GẮN SỰ KIỆN Ở CUỐI CÙNG <---
        addEventsToButtons(btnApprove, btnReturn, btnCancel, btnDetails);
    } // (Kết thúc hàm initComponents)


    // Nạp dữ liệu giả (Mock data)
    private void loadDataFromDB() {
        try {
            tableModel.setRowCount(0);
            ArrayList<HopDongDTO> danhSach = hopDongBUS.layDanhSachHopDong();
            populateTable(danhSach);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải Hợp đồng.\n" + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
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

    private void addEventsToButtons(JButton btnApprove, JButton btnReturn, JButton btnCancel, JButton btnDetails) {
        btnApprove.addActionListener(e -> handleUpdateStatus("Duyệt Hợp Đồng", "Approve"));
        btnReturn.addActionListener(e -> handleUpdateStatus("Xác Nhận Trả Xe", "Return"));
        btnCancel.addActionListener(e -> handleUpdateStatus("Hủy Hợp Đồng", "Cancel"));

        btnSearch.addActionListener(e -> handleSearch()); // (Nhớ gán biến btnSearch ở trên nhé)
    }

    private void handleSearch() {
        try {
            String keyword = txtSearch.getText().trim();
            String rawStatus = cbStatusFilter.getSelectedItem().toString();

            // Map ComboBox text sang DB Enum
            String dbStatus = "Tất cả trạng thái";
            if (rawStatus.contains("Pending")) dbStatus = "PENDING";
            else if (rawStatus.contains("Active")) dbStatus = "ACTIVE";
            else if (rawStatus.contains("Completed")) dbStatus = "COMPLETED";
            else if (rawStatus.contains("Cancelled")) dbStatus = "CANCELLED";

            ArrayList<HopDongDTO> list = hopDongBUS.timKiemHopDong(keyword, dbStatus);
            tableModel.setRowCount(0);
            populateTable(list);

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hợp đồng nào phù hợp.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    // XỬ LÝ CHUNG CHO 3 NÚT: DUYỆT / TRẢ / HỦY
    private void handleUpdateStatus(String actionName, String actionType) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Hợp đồng trong bảng để " + actionName.toLowerCase() + "!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String contractCode = tableModel.getValueAt(selectedRow, 0).toString();
        String currentStatus = tableModel.getValueAt(selectedRow, 6).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận " + actionName + " mã: " + contractCode + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean success = false;
            switch (actionType) {
                case "Approve":
                    success = hopDongBUS.duyetYeuCau(contractCode, currentStatus);
                    break;
                case "Return":
                    success = hopDongBUS.traXe(contractCode, currentStatus);
                    break;
                case "Cancel":
                    success = hopDongBUS.huyHopDong(contractCode, currentStatus);
                    break;
            }

            if (success) {
                JOptionPane.showMessageDialog(this, actionName + " thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDataFromDB(); // Tải lại bảng ngay lập tức
            }
        } catch (IllegalArgumentException ex) {
            // Bắt lỗi logic nghiệp vụ từ BUS (Ví dụ: Cố tình Duyệt hợp đồng đã Completed)
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Thao tác không hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            // Bắt lỗi DB (Ví dụ: Sập Transaction)
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Hàm tiện ích in dữ liệu ra bảng
    private void populateTable(ArrayList<HopDongDTO> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (HopDongDTO hd : list) {
            String formattedDeposit = String.format("%,.0f VNĐ", hd.getDepositAmount());
            String startStr = hd.getRentalStart() != null ? sdf.format(hd.getRentalStart()) : "";
            String endStr = hd.getRentalEnd() != null ? sdf.format(hd.getRentalEnd()) : "";

            tableModel.addRow(new Object[]{
                    hd.getContractCode(),
                    hd.getCustomerName(),
                    hd.getVehicleName(),
                    startStr,
                    endStr,
                    formattedDeposit,
                    hd.getContractStatus()
            });
        }
    }



}