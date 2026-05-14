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
    private JButton btnRefresh;

    private HopDongBUS hopDongBUS = new HopDongBUS();

    private JPanel inputPanel;
    private JTextField txtContractCode, txtCustomerName, txtVehicleId, txtVehicleName;
    private JTextField txtRentalStart, txtRentalEnd, txtDeposit, txtTotalAmount;
    private JComboBox<String> cbStatusInput;

    private JButton btnApproveContract;
    private JButton btnRejectContract;
    private JButton btnConfirmEdit;
    private JButton btnCancelInput;

    public ContractManagementPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadDataFromDB();
    }

    private void initComponents() {
        // TIÊU ĐỀ
        JLabel lblTitle = new JLabel("QUẢN LÝ HỢP ĐỒNG & YÊU CẦU THUÊ XE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 43, 54));

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);
        topPanel.add(lblTitle, BorderLayout.NORTH);

        // THANH CÔNG CỤ
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbarPanel.setOpaque(false);

        txtSearch = new JTextField(15);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm mã HĐ, tên khách...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        String[] statusOptions = {"Tất cả trạng thái", "Pending (Chờ duyệt)", "Active (Đang thuê)", "Completed (Đã trả)", "Cancelled (Đã hủy)"};
        cbStatusFilter = new JComboBox<>(statusOptions);

        btnSearch = createActionButton("Tìm", new Color(25, 118, 210));
        btnRefresh = createActionButton("Làm mới", new Color(117, 117, 117));

        JButton btnDetails = createActionButton("Xem Chi Tiết", new Color(243, 156, 18));

        toolbarPanel.add(new JLabel("Tìm kiếm:"));
        toolbarPanel.add(txtSearch);
        toolbarPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        toolbarPanel.add(new JLabel("Trạng thái:"));
        toolbarPanel.add(cbStatusFilter);
        toolbarPanel.add(btnSearch);
        toolbarPanel.add(btnRefresh);

        toolbarPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        toolbarPanel.add(btnDetails);

        topPanel.add(toolbarPanel, BorderLayout.CENTER);

        // BẢNG DỮ LIỆU
        String[] columns = {"Mã HĐ", "Khách Hàng", "Mã Xe", "Tên Xe", "Ngày Thuê", "Dự Kiến Trả", "Tiền Cọc", "Tổng Tiền", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && inputPanel.isVisible() && table.getSelectedRow() != -1) {
                fillDataToPanel();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        initInputPanel();

        // GẮN VÀO PANEL CHÍNH
        JPanel centerContainer = new JPanel(new BorderLayout(0, 20));
        centerContainer.setOpaque(false);
        centerContainer.add(topPanel, BorderLayout.NORTH);
        centerContainer.add(scrollPane, BorderLayout.CENTER);
        centerContainer.add(inputPanel, BorderLayout.SOUTH);

        add(centerContainer, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> handleSearch());

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbStatusFilter.setSelectedIndex(0);
            inputPanel.setVisible(false);
            loadDataFromDB();
        });

        btnDetails.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một Hợp đồng trong bảng để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            inputPanel.setVisible(true);
            fillDataToPanel();
        });
    }

    private void initInputPanel() {
        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết / Cập nhật Hợp đồng"));
        inputPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtContractCode = new JTextField(10);
        txtContractCode.setEnabled(false);
        txtCustomerName = new JTextField(15);
        txtVehicleId = new JTextField(10);
        txtVehicleName = new JTextField(15);

        txtRentalStart = new JTextField(12);
        txtRentalEnd = new JTextField(12);
        txtDeposit = new JTextField(10);
        txtTotalAmount = new JTextField(10);

        cbStatusInput = new JComboBox<>(new String[]{"PENDING", "ACTIVE", "COMPLETED", "CANCELLED"});

        btnApproveContract = createActionButton("Duyệt Yêu Cầu", new Color(46, 204, 113));
        btnRejectContract = createActionButton("Từ Chối Yêu Cầu", new Color(231, 76, 60));
        btnConfirmEdit = createActionButton("Xác Nhận Cập Nhật", new Color(41, 128, 185));
        btnCancelInput = createActionButton("Đóng", Color.GRAY);

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã HĐ:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtContractCode, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Khách hàng:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtCustomerName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Mã Xe:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtVehicleId, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Tên Xe:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtVehicleName, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Ngày thuê:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtRentalStart, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Dự kiến trả:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtRentalEnd, gbc);

        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Tiền cọc:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtDeposit, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Tổng tiền:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtTotalAmount, gbc);

        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; inputPanel.add(cbStatusInput, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGroup.setBackground(Color.WHITE);

        btnGroup.add(btnApproveContract);
        btnGroup.add(btnRejectContract);
        btnGroup.add(btnConfirmEdit);
        btnGroup.add(btnCancelInput);

        inputPanel.add(btnGroup, gbc);

        btnCancelInput.addActionListener(e -> inputPanel.setVisible(false));

        btnApproveContract.addActionListener(e -> handleAction("Duyệt Hợp Đồng", "Approve"));
        btnRejectContract.addActionListener(e -> handleAction("Từ Chối Hợp Đồng", "Cancel"));
        btnConfirmEdit.addActionListener(e -> handleSaveEdit());
    }

    private void fillDataToPanel() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        txtContractCode.setText(tableModel.getValueAt(selectedRow, 0).toString());
        txtCustomerName.setText(tableModel.getValueAt(selectedRow, 1).toString());
        txtVehicleId.setText(tableModel.getValueAt(selectedRow, 2).toString());
        txtVehicleName.setText(tableModel.getValueAt(selectedRow, 3).toString());
        txtRentalStart.setText(tableModel.getValueAt(selectedRow, 4).toString());
        txtRentalEnd.setText(tableModel.getValueAt(selectedRow, 5).toString());

        String cDStr = tableModel.getValueAt(selectedRow, 6).toString().replaceAll("[^\\d]", "");
        txtDeposit.setText(cDStr);

        String tAmountStr = tableModel.getValueAt(selectedRow, 7).toString().replaceAll("[^\\d]", "");
        txtTotalAmount.setText(tAmountStr);

        String status = tableModel.getValueAt(selectedRow, 8).toString();
        cbStatusInput.setSelectedItem(status);

        boolean isPending = status.equalsIgnoreCase("PENDING");

        cbStatusInput.setEnabled(isPending);

        btnApproveContract.setVisible(isPending);
        btnRejectContract.setVisible(isPending);

        revalidate();
        repaint();
    }

    private void handleSaveEdit() {
        try {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn cập nhật thông tin Hợp đồng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            String contractCode = txtContractCode.getText();
            double deposit = Double.parseDouble(txtDeposit.getText());
            double total = Double.parseDouble(txtTotalAmount.getText());
            String status = cbStatusInput.getSelectedItem().toString();

            HopDongDTO hd = new HopDongDTO();
            hd.setContractCode(contractCode);
            hd.setDepositAmount(deposit);
            hd.setTotalAmount(total);
            hd.setContractStatus(status);

            if(hopDongBUS.capNhatHopDong(hd)) {
                JOptionPane.showMessageDialog(this, "Cập nhật Hợp đồng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                inputPanel.setVisible(false);
                loadDataFromDB();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại. Vui lòng kiểm tra lại!", "Lỗi thao tác", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tiền cọc và Tổng tiền phải là số hợp lệ (không chứa chữ hoặc ký tự đặc biệt)!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống CSDL: " + e.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAction(String actionName, String actionType) {
        String contractCode = txtContractCode.getText();
        String currentStatus = cbStatusInput.getSelectedItem().toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận " + actionName + " mã: " + contractCode + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean success = false;
            switch (actionType) {
                case "Approve":
                    success = hopDongBUS.duyetYeuCau(contractCode, currentStatus);
                    break;
                case "Cancel":
                    success = hopDongBUS.huyHopDong(contractCode, currentStatus);
                    break;
            }

            if (success) {
                JOptionPane.showMessageDialog(this, actionName + " thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                inputPanel.setVisible(false);
                loadDataFromDB();
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Thao tác không hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDataFromDB() {
        try {
            tableModel.setRowCount(0);
            ArrayList<HopDongDTO> danhSach = hopDongBUS.layDanhSachHopDong();
            populateTable(danhSach);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải Hợp đồng.\n" + ex.getMessage(), "Lỗi DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearch() {
        try {
            String keyword = txtSearch.getText().trim();
            String rawStatus = cbStatusFilter.getSelectedItem().toString();

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

    private void populateTable(ArrayList<HopDongDTO> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (HopDongDTO hd : list) {
            String formattedDeposit = String.format("%,.0f VNĐ", hd.getDepositAmount());
            String formattedTotal = String.format("%,.0f VNĐ", hd.getTotalAmount());
            String startStr = hd.getRentalStart() != null ? sdf.format(hd.getRentalStart()) : "";
            String endStr = hd.getRentalEnd() != null ? sdf.format(hd.getRentalEnd()) : "";

            tableModel.addRow(new Object[]{
                    hd.getContractCode(),
                    hd.getCustomerName(),
                    hd.getVehicleId(),
                    hd.getVehicleName(),
                    startStr,
                    endStr,
                    formattedDeposit,
                    formattedTotal,
                    hd.getContractStatus()
            });
        }
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 5");
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

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