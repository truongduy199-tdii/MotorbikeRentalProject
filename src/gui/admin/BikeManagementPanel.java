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
    private JTextField txtBikeCode, txtBikeName, txtPlate, txtPriceDay, txtPriceHour, txtColor, txtYear;
    private JComboBox<String> cbStatusInput;
    private JTextArea txtDesc;

    private XeMayBUS xeMayBUS = new XeMayBUS();

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

        // --- Cập nhật TopPanel: Sử dụng GridLayout để hai bên trái/phải co giãn đều nhau ---
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        topPanel.setOpaque(false);

        // Vùng bên trái: Tìm kiếm (Sử dụng GridBagLayout để thanh Search co giãn theo chiều ngang)
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setOpaque(false);
        GridBagConstraints gbcSearch = new GridBagConstraints();
        gbcSearch.fill = GridBagConstraints.HORIZONTAL;
        gbcSearch.insets = new Insets(0, 5, 0, 5);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setPreferredSize(new Dimension(70, 30));

        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mã, tên hoặc biển số...");

        String[] statusSearch = {"Tất cả trạng thái", "AVAILABLE", "RENTED", "MAINTENANCE", "INACTIVE"};
        cbStatusFilter = new JComboBox<>(statusSearch);

        btnSearch = createActionButton("Tìm", new Color(25, 118, 210));
        btnSearch.addActionListener(e -> handleSearch());

        // Layout cho searchPanel
        gbcSearch.weightx = 0; gbcSearch.gridx = 0; searchPanel.add(lblSearch, gbcSearch);
        gbcSearch.weightx = 1.0; gbcSearch.gridx = 1; searchPanel.add(txtSearch, gbcSearch); // weightx = 1.0 giúp ô nhập co giãn
        gbcSearch.weightx = 0; gbcSearch.gridx = 2; searchPanel.add(cbStatusFilter, gbcSearch);
        gbcSearch.weightx = 0; gbcSearch.gridx = 3; searchPanel.add(btnSearch, gbcSearch);

        // Vùng bên phải: Nút chức năng (FlowLayout.RIGHT)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnAdd = createActionButton("Thêm mới", new Color(46, 125, 50));
        JButton btnEdit = createActionButton("Sửa", new Color(237, 108, 2));
        JButton btnDelete = createActionButton("Xóa", new Color(211, 47, 47));
        JButton btnRefresh = createActionButton("Làm mới", new Color(117, 117, 117));

        btnAdd.addActionListener(e -> {
            inputPanel.setVisible(true);
            revalidate();
        });

        btnRefresh.addActionListener(e -> loadDataFromDB());

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);

        topPanel.add(searchPanel);
        topPanel.add(actionPanel);

        // --- Cập nhật TableModel: Thêm đầy đủ các cột theo yêu cầu ---
        String[] columns = {"Mã xe", "Tên xe", "Biển số", "Màu", "Năm SX", "Giá/Ngày", "Giá/Giờ", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

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
        txtPriceHour = new JTextField(10);
        cbStatusInput = new JComboBox<>(new String[]{"AVAILABLE", "INACTIVE"});

        JButton btnSave = createActionButton("Lưu dữ liệu", new Color(46, 125, 50));
        JButton btnCancel = createActionButton("Hủy", Color.GRAY);

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã xe:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtBikeCode, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Tên xe:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtBikeName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Biển số:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtPlate, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Màu sắc:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtColor, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Giá/Ngày:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtPriceDay, gbc);
        gbc.gridx = 2; inputPanel.add(new JLabel("Giá/Giờ:"), gbc);
        gbc.gridx = 3; inputPanel.add(txtPriceHour, gbc);

        gbc.gridx = 3; gbc.gridy = 3;
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGroup.add(btnCancel); btnGroup.add(btnSave);
        inputPanel.add(btnGroup, gbc);

        btnCancel.addActionListener(e -> inputPanel.setVisible(false));
        btnSave.addActionListener(e -> handleAddBike());
    }

    private void loadDataFromDB() {
        tableModel.setRowCount(0);
        ArrayList<XeMayDTO> listXe = xeMayBUS.layDanhSachXeMay();
        for (XeMayDTO xe : listXe) {
            addRowToTable(xe);
        }
    }

    private void handleSearch() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        String status = cbStatusFilter.getSelectedItem().toString();

        tableModel.setRowCount(0);
        ArrayList<XeMayDTO> listXe = xeMayBUS.layDanhSachXeMay();

        for (XeMayDTO xe : listXe) {
            boolean matchKey = keyword.isEmpty() ||
                    xe.getVehicleCode().toLowerCase().contains(keyword) ||
                    xe.getVehicleName().toLowerCase().contains(keyword) ||
                    xe.getLicensePlate().toLowerCase().contains(keyword);

            boolean matchStatus = status.equals("Tất cả trạng thái") || xe.getStatus().equals(status);

            if (matchKey && matchStatus) {
                addRowToTable(xe);
            }
        }
    }

    private void handleAddBike() {
        JOptionPane.showMessageDialog(this, "Đang thực hiện thêm xe: " + txtBikeName.getText());
        inputPanel.setVisible(false);
        loadDataFromDB();
    }

    // --- Cập nhật addRowToTable: Đổ dữ liệu vào đúng các cột mới ---
    private void addRowToTable(XeMayDTO xe) {
        String priceDay = String.format("%,.0f VNĐ", xe.getRentalPricePerDay());
        String priceHour = String.format("%,.0f VNĐ", xe.getRentalPricePerHour());

        Object[] row = {
                xe.getVehicleCode(),
                xe.getVehicleName(), // Kết hợp Brand + Model
                xe.getLicensePlate(),
                xe.getColor(),
                xe.getManufactureYear(),
                priceDay,
                priceHour,
                xe.getStatus()
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
}