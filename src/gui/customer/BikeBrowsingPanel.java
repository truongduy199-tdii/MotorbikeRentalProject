package gui.customer;

import bus.XeMayBUS;
import dto.XeMayDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;

public class BikeBrowsingPanel extends JPanel {

    private JTable tableBikes;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private XeMayBUS xeMayBUS;
    private CustomerMainFrame parentFrame;
    private ArrayList<XeMayDTO> listBikes;
    private JTextField txtSearch;

    public BikeBrowsingPanel(CustomerMainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.xeMayBUS = new XeMayBUS();

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initComponents();
        loadBikeData();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("DANH SÁCH XE CHO THUÊ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 53, 63)); // Màu chữ giống Admin
        topPanel.add(lblTitle, BorderLayout.WEST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(new Color(245, 247, 250));

        // Thanh tìm kiếm FlatLaf
        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.putClientProperty(com.formdev.flatlaf.FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên xe, biển số...");
        txtSearch.putClientProperty(com.formdev.flatlaf.FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterData(); }
            public void removeUpdate(DocumentEvent e) { filterData(); }
            public void changedUpdate(DocumentEvent e) { filterData(); }
        });

        // Nút Thuê Xe
        JButton btnRent = new JButton("Thuê xe này");
        btnRent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRent.setBackground(new Color(52, 152, 219)); // Xanh chuẩn Admin
        btnRent.setForeground(Color.WHITE);
        btnRent.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc: 10");
        btnRent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRent.addActionListener(e -> handleRentRequest());

        actionPanel.add(txtSearch);
        actionPanel.add(btnRent);
        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // BẢNG DỮ LIỆU
        String[] columns = {"Mã Xe", "Tên Xe", "Hãng", "Biển số", "Màu sắc", "Giá / Ngày", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableBikes = new JTable(tableModel);
        tableBikes.setRowHeight(35);
        tableBikes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableBikes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableBikes.getTableHeader().setBackground(new Color(240, 240, 240));
        tableBikes.getTableHeader().setOpaque(false);
        tableBikes.setSelectionBackground(new Color(52, 152, 219));
        tableBikes.setSelectionForeground(Color.WHITE);

        rowSorter = new TableRowSorter<>(tableModel);
        tableBikes.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(tableBikes);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);
        btnRent.setPreferredSize(new Dimension(150, 40));

        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tableBikes.getColumnCount(); i++) {
            tableBikes.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        ((javax.swing.table.DefaultTableCellRenderer) tableBikes.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }

    private void searchTable(String text) {
        if (text.trim().length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void loadBikeData() {
        try {
            tableModel.setRowCount(0);
            listBikes = xeMayBUS.layDanhSachXeMay();
            for (dto.XeMayDTO xe : listBikes) {
                // CHỈ HIỂN THỊ XE ĐANG TRỐNG (AVAILABLE)
                if ("AVAILABLE".equalsIgnoreCase(xe.getStatus())) {
                    Object[] row = {
                            xe.getVehicleCode(), xe.getVehicleName(), xe.getBrand(),
                            xe.getLicensePlate(), xe.getColor(),
                            String.format("%,.0f VNĐ", xe.getRentalPricePerDay()),
                            xe.getStatus()
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải danh sách xe: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRentRequest() {
        int viewRow = tableBikes.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chiếc xe trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tableBikes.convertRowIndexToModel(viewRow);
        String vehicleCode = tableModel.getValueAt(modelRow, 0).toString();

        XeMayDTO selectedBike = null;
        for (XeMayDTO xe : listBikes) {
            if (xe.getVehicleCode().equals(vehicleCode)) {
                selectedBike = xe;
                break;
            }
        }

        if (selectedBike != null && !selectedBike.getStatus().equals("AVAILABLE")) {
            JOptionPane.showMessageDialog(this, "Xe này hiện đang được thuê hoặc bảo trì. Vui lòng chọn xe khác!", "Từ chối", JOptionPane.ERROR_MESSAGE);
            return;
        }

        parentFrame.showRentalRequest(selectedBike);
    }

    private void filterData() {
        if (rowSorter == null) return;

        String text = txtSearch.getText().trim();
        if (text.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
}