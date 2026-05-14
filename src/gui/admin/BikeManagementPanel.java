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
    private JButton btnSearch;
    private JPanel inputPanel;
    private JTextField txtBikeCode, txtBikeName, txtPlate, txtPriceDay, txtColor, txtYear;
    private JComboBox<String> cbStatusInput;
    private XeMayBUS xeMayBUS = new XeMayBUS();
    private boolean isEditMode = false;

    public BikeManagementPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadDataFromDB();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("QUẢN LÝ DANH MỤC XE MÁY");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 43, 54));

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        topPanel.setOpaque(false);

        // Vùng Tìm kiếm
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setOpaque(false);
        GridBagConstraints gbcSearch = new GridBagConstraints();
        gbcSearch.fill = GridBagConstraints.HORIZONTAL;
        gbcSearch.insets = new Insets(0, 5, 0, 5);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setPreferredSize(new Dimension(70, 30));

        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mã, tên hoặc biển số...");

        String[] statusSearch = {"Tất cả trạng thái", "AVAILABLE", "RENTED", "MAINTENANCE", "DELETED"};
        cbStatusFilter = new JComboBox<>(statusSearch);

        btnSearch = createActionButton("Tìm", new Color(25, 118, 210));
        btnSearch.addActionListener(e -> handleSearch());

        gbcSearch.weightx = 0; gbcSearch.gridx = 0; searchPanel.add(lblSearch, gbcSearch);
        gbcSearch.weightx = 1.0; gbcSearch.gridx = 1; searchPanel.add(txtSearch, gbcSearch);
        gbcSearch.weightx = 0; gbcSearch.gridx = 2; searchPanel.add(cbStatusFilter, gbcSearch);
        gbcSearch.weightx = 0; gbcSearch.gridx = 3; searchPanel.add(btnSearch, gbcSearch);

        // Vùng nút chức năng
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnAdd = createActionButton("Thêm xe", new Color(46, 125, 50));
        JButton btnEdit = createActionButton("Sửa", new Color(237, 108, 2));
        JButton btnRefresh = createActionButton("Làm mới", new Color(117, 117, 117));

        btnAdd.addActionListener(e -> {
            isEditMode = false;
            clearInputFields();
            txtBikeCode.setEnabled(true);
            inputPanel.setBorder(BorderFactory.createTitledBorder("Thêm thông tin xe mới"));
            inputPanel.setVisible(true);
            revalidate();
        });

        btnEdit.addActionListener(e -> handlePrepareEdit());

        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbStatusFilter.setSelectedIndex(0);
            loadDataFromDB();
        });

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnRefresh);

        topPanel.add(searchPanel);
        topPanel.add(actionPanel);

        String[] columns = {"Mã xe", "Tên xe", "Biển số", "Màu", "Năm SX", "Giá Thuê/Ngày", "Trạng thái", "SĐT khách thuê"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);

        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(200, 225, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(100, 40));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(70,130,180));
        header.setForeground(new Color(255,255,255));
        header.setOpaque(true);

        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        initInputPanel();

        JPanel centerContainer = new JPanel(new BorderLayout(0, 20));
        centerContainer.setOpaque(false);
        centerContainer.add(topPanel, BorderLayout.NORTH);
        centerContainer.add(scrollPane, BorderLayout.CENTER);
        centerContainer.add(inputPanel, BorderLayout.SOUTH);

        add(lblTitle, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
    }

    private void initInputPanel() {
        inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin xe mới"));
        inputPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtBikeCode = new JTextField(10);
        txtBikeName = new JTextField(15);
        txtPlate = new JTextField(10);
        txtColor = new JTextField(10);
        txtYear = new JTextField(5);
        txtPriceDay = new JTextField(10);
        cbStatusInput = new JComboBox<>(new String[]{"AVAILABLE", "MAINTENANCE", "DELETED"});

        JButton btnSave = createActionButton("Lưu dữ liệu", new Color(46, 125, 50));
        JButton btnCancel = createActionButton("Hủy", Color.GRAY);

        // Hàng 1
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã xe:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtBikeCode, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Tên xe:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtBikeName, gbc);

        // Hàng 2
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Biển số:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtPlate, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Màu sắc:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtColor, gbc);

        // Hàng 3
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Giá Thuê/Ngày:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtPriceDay, gbc);
        gbc.gridx = 2; gbc.gridy = 2; inputPanel.add(new JLabel("Năm sản xuất:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtYear, gbc);

        // Hàng 4
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; inputPanel.add(cbStatusInput, gbc);

        gbc.gridx = 3; gbc.gridy = 3;
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGroup.add(btnCancel); btnGroup.add(btnSave);
        inputPanel.add(btnGroup, gbc);

        btnCancel.addActionListener(e -> inputPanel.setVisible(false));
        btnSave.addActionListener(e -> handleAddBike());
    }

    private void loadDataFromDB() {
        try {
            tableModel.setRowCount(0);
            ArrayList<XeMayDTO> listXe = xeMayBUS.layDanhSachXeMay();
            for (XeMayDTO xe : listXe) {
                addRowToTable(xe);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu để tải danh sách xe.\nVui lòng kiểm tra lại kết nối mạng hoặc XAMPP.", "Lỗi khởi tạo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearch() {
        try {
            String keyword = txtSearch.getText().trim();
            String status = cbStatusFilter.getSelectedItem().toString();

            ArrayList<XeMayDTO> listXe = xeMayBUS.timKiemXeMay(keyword, status);

            tableModel.setRowCount(0);
            for (XeMayDTO xe : listXe) {
                addRowToTable(xe);
            }

            if (listXe.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy xe máy nào phù hợp với điều kiện tìm kiếm.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRowToTable(XeMayDTO xe) {
        String priceDay = String.format("%,.0f VNĐ", xe.getRentalPricePerDay());
        Object[] row = {
                xe.getVehicleCode(),
                xe.getVehicleName(),
                xe.getLicensePlate(),
                xe.getColor(),
                xe.getManufactureYear(),
                priceDay,
                xe.getStatus(),
                xe.getRenterPhone()
        };
        tableModel.addRow(row);
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void handleAddBike() {
        try {
            String code = txtBikeCode.getText().trim();
            String name = txtBikeName.getText().trim();
            String plate = txtPlate.getText().trim();
            String color = txtColor.getText().trim();
            int year = Integer.parseInt(txtYear.getText().trim());
            double priceDay = Double.parseDouble(txtPriceDay.getText().trim());
            String status = cbStatusInput.getSelectedItem().toString();

            if (isEditMode) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String oldStatus = tableModel.getValueAt(selectedRow, 6).toString();
                    if (oldStatus.equals("RENTED") && !status.equals("RENTED")) {
                        JOptionPane.showMessageDialog(this,
                                "Xe đang được khách thuê (RENTED).\nKhông thể tự ý đổi sang trạng thái " + status + "!\nVui lòng kết thúc/thanh lý hợp đồng của xe này trước.",
                                "Cảnh báo bảo toàn dữ liệu",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            String brand = name.contains(" ") ? name.substring(0, name.indexOf(" ")) : name;
            String model = name.contains(" ") ? name.substring(name.indexOf(" ") + 1) : "";

            XeMayDTO xe = new XeMayDTO();
            xe.setVehicleCode(code);
            xe.setBrand(brand);
            xe.setModel(model);
            xe.setLicensePlate(plate);
            xe.setColor(color);
            xe.setManufactureYear(year);
            xe.setRentalPricePerDay(priceDay);
            xe.setStatus(status);

            if (!isEditMode) {
                if (xeMayBUS.themXeMay(xe)) {
                    JOptionPane.showMessageDialog(this, "Thêm xe thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    inputPanel.setVisible(false);
                    loadDataFromDB();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể thêm xe. Vui lòng kiểm tra lại (có thể trùng mã xe hoặc biển số).", "Lỗi thao tác", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (xeMayBUS.suaXeMay(xe)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật xe thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    inputPanel.setVisible(false);
                    loadDataFromDB();
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi thao tác", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Năm sản xuất và Giá thuê phải là số hợp lệ!", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);

        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handlePrepareEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một xe trong bảng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        isEditMode = true;
        inputPanel.setBorder(BorderFactory.createTitledBorder("Sửa thông tin xe"));

        txtBikeCode.setText(tableModel.getValueAt(selectedRow, 0).toString());
        txtBikeCode.setEnabled(false);

        txtBikeName.setText(tableModel.getValueAt(selectedRow, 1).toString());
        txtPlate.setText(tableModel.getValueAt(selectedRow, 2).toString());
        txtColor.setText(tableModel.getValueAt(selectedRow, 3).toString());
        txtYear.setText(tableModel.getValueAt(selectedRow, 4).toString());

        String pDayStr = tableModel.getValueAt(selectedRow, 5).toString().replaceAll("[^\\d]", "");
        txtPriceDay.setText(pDayStr);

        cbStatusInput.setSelectedItem(tableModel.getValueAt(selectedRow, 6).toString());

        inputPanel.setVisible(true);
        revalidate();
    }

    private void clearInputFields() {
        txtBikeCode.setText("");
        txtBikeName.setText("");
        txtPlate.setText("");
        txtColor.setText("");
        txtYear.setText("");
        txtPriceDay.setText("");
        // Đã xóa txtPriceHour
        cbStatusInput.setSelectedIndex(0);
    }
}