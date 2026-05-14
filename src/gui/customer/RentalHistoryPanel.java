package gui.customer;

import bus.HopDongBUS;
import dto.HopDongDTO;
import dto.TaiKhoanDTO;
import utils.SessionUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class RentalHistoryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private HopDongBUS hopDongBUS;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public RentalHistoryPanel() {
        this.hopDongBUS = new HopDongBUS();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 247, 250));
        topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("LỊCH SỬ THUÊ XE CỦA BẠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(44, 53, 63));
        topPanel.add(lblTitle, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Mã HĐ", "Tên Xe", "Ngày bắt đầu", "Ngày kết thúc", "Tổng tiền", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);

        // Định dạng bảng hiện đại
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);
        // CĂN GIỮA BẢNG LỊCH SỬ THUÊ
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        ((javax.swing.table.DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }

    public void loadData() {
        try {
            tableModel.setRowCount(0);

            TaiKhoanDTO currentUser = SessionUser.getCurrentUser();
            if (currentUser == null) return;

            ArrayList<HopDongDTO> userContracts = hopDongBUS.layHopDongTheoUser(currentUser.getUserId());

            for (HopDongDTO hd : userContracts) {
                String startStr = hd.getRentalStart() != null ? dateFormat.format(hd.getRentalStart()) : "N/A";
                String endStr = hd.getRentalEnd() != null ? dateFormat.format(hd.getRentalEnd()) : "N/A";

                tableModel.addRow(new Object[]{
                        hd.getContractCode(),
                        hd.getVehicleName(),
                        startStr,
                        endStr,
                        String.format("%,.0f VNĐ", hd.getTotalAmount()),
                        hd.getContractStatus()
                });
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải lịch sử thuê xe: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }
}