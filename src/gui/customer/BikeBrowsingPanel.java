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
    private TableRowSorter<DefaultTableModel> rowSorter; // Thêm bộ lọc cho bảng
    private XeMayBUS xeMayBUS;
    private CustomerMainFrame parentFrame;
    private ArrayList<XeMayDTO> listBikes;

    public BikeBrowsingPanel(CustomerMainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.xeMayBUS = new XeMayBUS();

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250)); // Nền màu xám cực nhạt giúp bảng nổi bật hơn
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initComponents();
        loadBikeData();
    }

    private void initComponents() {
        // --- PHẦN HEADER: Tiêu đề + Thanh tìm kiếm ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));

        JLabel lblTitle = new JLabel("DANH SÁCH XE CHO THUÊ", SwingConstants.CENTER); // Thêm SwingConstants.CENTER
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(41, 128, 185)); // Màu xanh dương giống admin
        topPanel.add(lblTitle, BorderLayout.CENTER); // Đổi từ WEST sang CENTER

        // Ô Tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(new Color(245, 247, 250));
        JLabel lblSearch = new JLabel("🔍 Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Sự kiện gõ phím để lọc dữ liệu trực tiếp
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { searchTable(txtSearch.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { searchTable(txtSearch.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { searchTable(txtSearch.getText()); }
        });

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        topPanel.add(searchPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // --- PHẦN BẢNG DỮ LIỆU ---
        String[] columnNames = {"Mã Xe", "Hãng", "Tên Xe", "Biển Số", "Màu Sắc", "Giá/Ngày", "Giá/Giờ", "Trạng Thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableBikes = new JTable(tableModel);
        rowSorter = new TableRowSorter<>(tableModel);
        tableBikes.setRowSorter(rowSorter); // Gắn bộ lọc vào bảng

        tableBikes.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tableBikes.setRowHeight(35); // Cho dòng cao lên cho dễ nhìn
        tableBikes.setSelectionBackground(new Color(173, 216, 230)); // Màu xanh nhạt khi bôi đen dòng

        // Chỉnh style cho Header của bảng
        tableBikes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableBikes.getTableHeader().setBackground(new Color(52, 152, 219));
        tableBikes.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tableBikes);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);

        // --- PHẦN DƯỚI CÙNG: Nút hành động ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(245, 247, 250));

        JButton btnRent = new JButton("YÊU CẦU THUÊ XE NÀY 🚀");
        btnRent.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnRent.setBackground(new Color(40, 167, 69));
        btnRent.setForeground(Color.WHITE);
        btnRent.setPreferredSize(new Dimension(250, 40));
        btnRent.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRent.addActionListener(e -> handleRentRequest());

        bottomPanel.add(btnRent);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Hàm lọc dữ liệu trên bảng
    private void searchTable(String text) {
        if (text.trim().length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            // (?i) giúp tìm kiếm không phân biệt hoa thường
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    public void loadBikeData() {
        tableModel.setRowCount(0);
        listBikes = xeMayBUS.layDanhSachXeMay();

        for (XeMayDTO xe : listBikes) {
            Object[] row = {
                    xe.getVehicleCode(), xe.getBrand(), xe.getModel(),
                    xe.getLicensePlate(), xe.getColor(),
                    String.format("%,.0f VNĐ", xe.getRentalPricePerDay()),
                    String.format("%,.0f VNĐ", xe.getRentalPricePerHour()),
                    xe.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void handleRentRequest() {
        int viewRow = tableBikes.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một chiếc xe trên bảng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Chuyển index từ view (bảng đã lọc) về model gốc để lấy đúng đối tượng xe
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
            JOptionPane.showMessageDialog(this, "Xe này hiện không trống. Vui lòng chọn xe khác!", "Từ chối", JOptionPane.ERROR_MESSAGE);
            return;
        }

        parentFrame.showRentalRequest(selectedBike);
    }
}