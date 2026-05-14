package gui.admin;

import bus.KhachHangBUS;
import dto.KhachHangDTO;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CustomerManagementPanel extends JPanel {

    private JTable tblCustomers;
    private DefaultTableModel tableModel;
    private KhachHangBUS khachHangBUS;

    // Các component cho Top Panel (Tìm kiếm & Bộ lọc)
    private JTextField txtSearch;
    private JComboBox<String> cbStatusFilter;
    private JButton btnSearch;

    // Các component cho Input Panel (Thêm/Sửa dữ liệu)
    private JPanel inputPanel;
    private JTextField txtCustomerId, txtFullName, txtPhone, txtEmail, txtCccd, txtBirthday, txtAddress, txtDriverLicense;
    private JComboBox<String> cbStatusInput;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public CustomerManagementPanel() {
        khachHangBUS = new KhachHangBUS();

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadDataFromDB();
    }

    private void initComponents() {
        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ DANH MỤC KHÁCH HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(33, 43, 54));

        // 2. TopPanel: GridLayout chia 2 bên (Tìm kiếm - Chức năng)
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        topPanel.setOpaque(false);

        // -- 2.1 Bên trái: Vùng tìm kiếm
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setOpaque(false);
        GridBagConstraints gbcSearch = new GridBagConstraints();
        gbcSearch.fill = GridBagConstraints.HORIZONTAL;
        gbcSearch.insets = new Insets(0, 5, 0, 5);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setPreferredSize(new Dimension(70, 30));

        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tên, SĐT hoặc CCCD...");

        String[] statusSearch = {"Tất cả trạng thái", "ACTIVE", "BLOCKED"};
        cbStatusFilter = new JComboBox<>(statusSearch);

        btnSearch = createActionButton("Tìm", new Color(25, 118, 210));
        btnSearch.addActionListener(e -> handleSearch());

        gbcSearch.weightx = 0; gbcSearch.gridx = 0; searchPanel.add(lblSearch, gbcSearch);
        gbcSearch.weightx = 1.0; gbcSearch.gridx = 1; searchPanel.add(txtSearch, gbcSearch);
        gbcSearch.weightx = 0; gbcSearch.gridx = 2; searchPanel.add(cbStatusFilter, gbcSearch);
        gbcSearch.weightx = 0; gbcSearch.gridx = 3; searchPanel.add(btnSearch, gbcSearch);

        // -- 2.2 Bên phải: Vùng nút chức năng
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnEdit = createActionButton("Sửa", new Color(237, 108, 2));
        JButton btnLock = createActionButton("Khóa", new Color(211, 47, 47)); // Đổi tên thành Khóa
        JButton btnRefresh = createActionButton("Làm mới", new Color(117, 117, 117));

        btnEdit.addActionListener(e -> handlePrepareEdit());
        btnLock.addActionListener(e -> handleLockCustomer());

        // Cập nhật tính năng nút Làm Mới
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbStatusFilter.setSelectedIndex(0);
            clearInputFields();
            inputPanel.setVisible(false);
            loadDataFromDB();
        });

        actionPanel.add(btnEdit);
        actionPanel.add(btnLock);
        actionPanel.add(btnRefresh);

        topPanel.add(searchPanel);
        topPanel.add(actionPanel);

        // 3. Bảng dữ liệu Khách hàng
        String[] columnNames = {"Mã KH", "Họ Tên", "SĐT", "Email", "CCCD", "Ngày Sinh", "Địa Chỉ", "Bằng Lái", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tblCustomers = new JTable(tableModel);

        tblCustomers.setRowHeight(35);
        tblCustomers.setShowVerticalLines(false);
        tblCustomers.setGridColor(new Color(230, 230, 230));
        tblCustomers.setSelectionBackground(new Color(200, 225, 255));
        tblCustomers.setSelectionForeground(Color.BLACK);
        tblCustomers.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = tblCustomers.getTableHeader();
        header.setPreferredSize(new Dimension(100, 40));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        header.setOpaque(true);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblCustomers.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblCustomers.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tblCustomers.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblCustomers.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        tblCustomers.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        tblCustomers.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);

        tblCustomers.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblCustomers.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblCustomers.getColumnModel().getColumn(6).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(tblCustomers);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // 4. Khởi tạo InputPanel
        initInputPanel();

        // 5. Gom Layout vào Center Container
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
        inputPanel.setBorder(BorderFactory.createTitledBorder("Sửa thông tin khách hàng"));
        inputPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCustomerId = new JTextField(10);
        txtCustomerId.setEnabled(false); // Luôn luôn không cho sửa Mã KH
        txtFullName = new JTextField(15);
        txtPhone = new JTextField(10);
        txtEmail = new JTextField(15);
        txtCccd = new JTextField(10);
        txtBirthday = new JTextField(10);
        txtBirthday.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "dd/MM/yyyy");
        txtDriverLicense = new JTextField(10);
        txtAddress = new JTextField(20);
        cbStatusInput = new JComboBox<>(new String[]{"ACTIVE", "BLOCKED"});

        JButton btnSave = createActionButton("Lưu dữ liệu", new Color(46, 125, 50));
        JButton btnCancel = createActionButton("Hủy", Color.GRAY);

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã KH:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtCustomerId, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtFullName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtPhone, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("CCCD:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtCccd, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Ngày sinh:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtBirthday, gbc);

        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Bằng lái:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtDriverLicense, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 3; inputPanel.add(cbStatusInput, gbc);

        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; inputPanel.add(txtAddress, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 3; gbc.gridy = 5;
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGroup.add(btnCancel); btnGroup.add(btnSave);
        inputPanel.add(btnGroup, gbc);

        btnCancel.addActionListener(e -> {
            clearInputFields();
            inputPanel.setVisible(false);
        });
        btnSave.addActionListener(e -> handleSaveCustomer());
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

    public void loadDataFromDB() {
        try {
            tableModel.setRowCount(0);
            List<KhachHangDTO> list = khachHangBUS.getAllCustomers();
            for (KhachHangDTO kh : list) {
                addRowToTable(kh);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải dữ liệu Khách hàng.\nChi tiết: " + ex.getMessage(), "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRowToTable(KhachHangDTO kh) {
        String birthdayStr = kh.getBirthday() != null ? sdf.format(kh.getBirthday()) : "N/A";
        Object[] row = {
                kh.getCustomerId(),
                kh.getFullName(),
                kh.getPhone(),
                kh.getEmail(),
                kh.getCccd(),
                birthdayStr,
                kh.getAddress(),
                kh.getDriverLicenseNumber(),
                kh.getStatus()
        };
        tableModel.addRow(row);
    }

    private void handleSearch() {
        try {
            String keyword = txtSearch.getText().trim();
            String status = cbStatusFilter.getSelectedItem().toString();

            List<KhachHangDTO> list = khachHangBUS.timKiemKhachHang(keyword, status);
            tableModel.setRowCount(0);

            for (KhachHangDTO kh : list) {
                addRowToTable(kh);
            }

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng nào phù hợp.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSaveCustomer() {
        try {
            int id = Integer.parseInt(txtCustomerId.getText().trim());
            String name = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String cccd = txtCccd.getText().trim();
            String address = txtAddress.getText().trim();
            String driverLicense = txtDriverLicense.getText().trim();
            String status = cbStatusInput.getSelectedItem().toString();

            KhachHangDTO kh = new KhachHangDTO();
            kh.setCustomerId(id);
            kh.setFullName(name);
            kh.setPhone(phone);
            kh.setEmail(email);
            kh.setCccd(cccd);
            kh.setAddress(address);
            kh.setDriverLicenseNumber(driverLicense);
            kh.setStatus(status);

            String dobStr = txtBirthday.getText().trim();
            if (!dobStr.isEmpty()) {
                kh.setBirthday(new java.sql.Date(sdf.parse(dobStr).getTime()));
            }

            if (khachHangBUS.suaKhachHang(kh)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                clearInputFields();
                inputPanel.setVisible(false);
                loadDataFromDB();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã khách hàng phải là một số nguyên hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày sinh không đúng định dạng (dd/MM/yyyy)!", "Lỗi định dạng", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống CSDL: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handlePrepareEdit() {
        int selectedRow = tblCustomers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng trong bảng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        txtCustomerId.setText(tableModel.getValueAt(selectedRow, 0).toString());
        txtFullName.setText(tableModel.getValueAt(selectedRow, 1).toString());
        txtPhone.setText(tableModel.getValueAt(selectedRow, 2).toString());

        Object emailVal = tableModel.getValueAt(selectedRow, 3);
        txtEmail.setText(emailVal != null ? emailVal.toString() : "");

        txtCccd.setText(tableModel.getValueAt(selectedRow, 4).toString());

        String dob = tableModel.getValueAt(selectedRow, 5).toString();
        txtBirthday.setText(dob.equals("N/A") ? "" : dob);

        Object addressVal = tableModel.getValueAt(selectedRow, 6);
        txtAddress.setText(addressVal != null ? addressVal.toString() : "");

        Object licenseVal = tableModel.getValueAt(selectedRow, 7);
        txtDriverLicense.setText(licenseVal != null ? licenseVal.toString() : "");

        cbStatusInput.setSelectedItem(tableModel.getValueAt(selectedRow, 8).toString());

        inputPanel.setVisible(true);
        revalidate();
    }

    private void handleLockCustomer() {
        int selectedRow = tblCustomers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng trong bảng để khóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            String name = tableModel.getValueAt(selectedRow, 1).toString();

            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn khóa khách hàng: " + name + " (" + id + ")?\nHành động này sẽ vô hiệu hóa tài khoản của họ.", "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (khachHangBUS.xoaKhachHang(id)) {
                    JOptionPane.showMessageDialog(this, "Đã khóa khách hàng thành công!");
                    clearInputFields();
                    inputPanel.setVisible(false);
                    loadDataFromDB();
                } else {
                    JOptionPane.showMessageDialog(this, "Khóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tương tác CSDL: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInputFields() {
        txtCustomerId.setText("");
        txtFullName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtCccd.setText("");
        txtBirthday.setText("");
        txtAddress.setText("");
        txtDriverLicense.setText("");
        cbStatusInput.setSelectedIndex(0);
    }
}