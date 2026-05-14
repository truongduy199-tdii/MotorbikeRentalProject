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
import java.util.Date;
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

    private boolean isEditMode = false;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public CustomerManagementPanel() {
        khachHangBUS = new KhachHangBUS();

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250)); // Màu nền tổng thể giống BikePanel
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

        String[] statusSearch = {"Tất cả trạng thái", "ACTIVE", "INACTIVE", "BLACKLISTED"};
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

        JButton btnAdd = createActionButton("Thêm KH", new Color(46, 125, 50));
        JButton btnEdit = createActionButton("Sửa", new Color(237, 108, 2));
        JButton btnDelete = createActionButton("Xóa", new Color(211, 47, 47));
        JButton btnRefresh = createActionButton("Làm mới", new Color(117, 117, 117));

        btnAdd.addActionListener(e -> {
            isEditMode = false;
            clearInputFields();
            txtCustomerId.setEnabled(true);
            inputPanel.setBorder(BorderFactory.createTitledBorder("Thêm thông tin khách hàng mới"));
            inputPanel.setVisible(true);
            revalidate();
        });

        btnEdit.addActionListener(e -> handlePrepareEdit());
        btnDelete.addActionListener(e -> handleDeleteCustomer());
        btnRefresh.addActionListener(e -> loadDataFromDB());

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
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

        // Style cho Table y hệt BikeManagementPanel
        tblCustomers.setRowHeight(35);
        tblCustomers.setShowVerticalLines(false);
        tblCustomers.setGridColor(new Color(230, 230, 230));
        tblCustomers.setSelectionBackground(new Color(200, 225, 255));
        tblCustomers.setSelectionForeground(Color.BLACK);
        tblCustomers.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Tùy chỉnh Header
        JTableHeader header = tblCustomers.getTableHeader();
        header.setPreferredSize(new Dimension(100, 40));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        header.setOpaque(true);

        // Căn giữa một số cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblCustomers.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã KH
        tblCustomers.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // SĐT
        tblCustomers.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // CCCD
        tblCustomers.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Ngày Sinh
        tblCustomers.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Bằng Lái
        tblCustomers.getColumnModel().getColumn(8).setCellRenderer(centerRenderer); // Trạng Thái

        // Set width cho cột
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
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        inputPanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCustomerId = new JTextField(10);
        txtFullName = new JTextField(15);
        txtPhone = new JTextField(10);
        txtEmail = new JTextField(15);
        txtCccd = new JTextField(10);
        txtBirthday = new JTextField(10); // Format: dd/MM/yyyy
        txtBirthday.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "dd/MM/yyyy");
        txtDriverLicense = new JTextField(10);
        txtAddress = new JTextField(20);
        cbStatusInput = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "BLACKLISTED"});

        JButton btnSave = createActionButton("Lưu dữ liệu", new Color(46, 125, 50));
        JButton btnCancel = createActionButton("Hủy", Color.GRAY);

        // Hàng 1
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã KH:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtCustomerId, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtFullName, gbc);

        // Hàng 2
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtPhone, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtEmail, gbc);

        // Hàng 3
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("CCCD:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtCccd, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Ngày sinh:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtBirthday, gbc);

        // Hàng 4
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Bằng lái:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtDriverLicense, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 3; inputPanel.add(cbStatusInput, gbc);

        // Hàng 5 - Địa chỉ chiếm nhiều khoảng trống hơn
        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; inputPanel.add(txtAddress, gbc);
        gbc.gridwidth = 1; // Reset lại gridwidth

        // Hàng 6 - Nút bấm
        gbc.gridx = 3; gbc.gridy = 5;
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGroup.add(btnCancel); btnGroup.add(btnSave);
        inputPanel.add(btnGroup, gbc);

        // Events
        btnCancel.addActionListener(e -> inputPanel.setVisible(false));
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
        tableModel.setRowCount(0);
        List<KhachHangDTO> list = khachHangBUS.getAllCustomers();

        if (list == null) {
            JOptionPane.showMessageDialog(this, "Không thể tải dữ liệu Khách hàng. Vui lòng kiểm tra lại kết nối Cơ sở dữ liệu!", "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (KhachHangDTO kh : list) {
            addRowToTable(kh);
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
        String keyword = txtSearch.getText().trim().toLowerCase();
        String status = cbStatusFilter.getSelectedItem().toString();

        tableModel.setRowCount(0);
        List<KhachHangDTO> list = khachHangBUS.getAllCustomers();

        if (list == null) return;

        for (KhachHangDTO kh : list) {
            boolean matchKey = keyword.isEmpty() ||
                    kh.getFullName().toLowerCase().contains(keyword) ||
                    kh.getPhone().contains(keyword) ||
                    kh.getCccd().contains(keyword);

            boolean matchStatus = status.equals("Tất cả trạng thái") || kh.getStatus().equalsIgnoreCase(status);

            if (matchKey && matchStatus) {
                addRowToTable(kh);
            }
        }
    }

    private void handleSaveCustomer() {
        try {
            String idStr = txtCustomerId.getText().trim(); // Đổi tên biến thành idStr cho rõ ràng
            String name = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            String cccd = txtCccd.getText().trim();
            String address = txtAddress.getText().trim();
            String driverLicense = txtDriverLicense.getText().trim();
            String status = cbStatusInput.getSelectedItem().toString();
            String dobStr = txtBirthday.getText().trim();

            if (idStr.isEmpty() || name.isEmpty() || phone.isEmpty() || cccd.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã, Tên, SĐT và CCCD!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // --- KIỂM TRA VÀ ÉP KIỂU MÃ KHÁCH HÀNG SANG INT ---
            int id;
            try {
                id = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã khách hàng phải là một số nguyên hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return; // Dừng lại, không chạy tiếp nếu lỗi
            }
            // --------------------------------------------------

            Date birthday = null;
            if (!dobStr.isEmpty()) {
                try {
                    birthday = sdf.parse(dobStr);
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, "Ngày sinh không đúng định dạng (dd/MM/yyyy)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            KhachHangDTO kh = new KhachHangDTO();
            kh.setCustomerId(id); // <--- Đã sửa ở đây, truyền biến 'id' kiểu int vào
            kh.setFullName(name);
            kh.setPhone(phone);
            kh.setEmail(email);
            kh.setCccd(cccd);
            kh.setAddress(address);
            kh.setDriverLicenseNumber(driverLicense);
            kh.setStatus(status);
            kh.setBirthday(birthday);

            // Giả lập lưu
            JOptionPane.showMessageDialog(this, isEditMode ? "Giả lập sửa thành công!" : "Giả lập thêm thành công!");
            inputPanel.setVisible(false);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handlePrepareEdit() {
        int selectedRow = tblCustomers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng trong bảng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        isEditMode = true;
        inputPanel.setBorder(BorderFactory.createTitledBorder("Sửa thông tin khách hàng"));

        txtCustomerId.setText(tableModel.getValueAt(selectedRow, 0).toString());
        txtCustomerId.setEnabled(false); // Không cho sửa Mã

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

    private void handleDeleteCustomer() {
        int selectedRow = tblCustomers.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng trong bảng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = tableModel.getValueAt(selectedRow, 0).toString();
        String name = tableModel.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa khách hàng: " + name + " (" + id + ")?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            /* MỤC NÀY CẦN CÓ PHƯƠNG THỨC TRONG KhachHangBUS
            if (khachHangBUS.xoaKhachHang(id)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadDataFromDB();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            */
            JOptionPane.showMessageDialog(this, "Giả lập xóa thành công!");
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