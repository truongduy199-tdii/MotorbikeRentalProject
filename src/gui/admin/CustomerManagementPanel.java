package gui.admin;

import bus.KhachHangBUS;
import dto.KhachHangDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CustomerManagementPanel extends JPanel {
    private JTable tblCustomers;
    private DefaultTableModel tableModel;
    private KhachHangBUS khachHangBUS;

    public CustomerManagementPanel() {
        khachHangBUS = new KhachHangBUS();
        initComponents();
        loadDataFormDB();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ KHÁCH HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(41, 128, 185));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(lblTitle, BorderLayout.NORTH);

        // 2. Bảng dữ liệu Khách hàng
        String[] columnNames = {"Mã KH", "Họ Tên", "SĐT", "Email", "CCCD", "Ngày Sinh", "Địa Chỉ", "Bằng Lái", "Trạng Thái"};

        // Ghi đè DefaultTableModel để khóa tính năng chỉnh sửa ô trực tiếp
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCustomers = new JTable(tableModel);
        tblCustomers.setRowHeight(30);
        tblCustomers.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblCustomers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblCustomers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Chỉnh kích thước các cột cho phù hợp
        tblCustomers.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblCustomers.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblCustomers.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tblCustomers);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        this.add(scrollPane, BorderLayout.CENTER);

        // 3. Panel chứa nút thao tác (có thể mở rộng thêm)
        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        btnRefresh.addActionListener(e -> loadDataFormDB());

        pnlAction.add(btnRefresh);
        this.add(pnlAction, BorderLayout.SOUTH);
    }

    private void loadDataFormDB() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        List<KhachHangDTO> list = khachHangBUS.getAllCustomers();

        // KIỂM TRA NULL: Ngăn chặn lỗi văng app nếu Database / BUS có vấn đề
        if (list == null || list.isEmpty()) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (KhachHangDTO kh : list) {
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
    }
}