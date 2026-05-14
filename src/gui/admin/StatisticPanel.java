package gui.admin;

import bus.HopDongBUS;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StatisticPanel extends JPanel {

    private HopDongBUS hopDongBUS;
    private JLabel lblRevenue, lblCustomers, lblTotalBikes, lblRentedBikes;

    public StatisticPanel() {
        this.hopDongBUS = new HopDongBUS();
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        initComponents();
        loadStatistics();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("BẢNG THỐNG KÊ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(new Color(33, 43, 54));

        JButton btnRefresh = new JButton("🔄 Cập nhật số liệu");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRefresh.setBackground(new Color(52, 152, 219));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadStatistics());

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnRefresh, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        cardsPanel.setOpaque(false);

        lblRevenue = new JLabel("0 VNĐ", SwingConstants.CENTER);
        lblCustomers = new JLabel("0", SwingConstants.CENTER);
        lblTotalBikes = new JLabel("0", SwingConstants.CENTER);
        lblRentedBikes = new JLabel("0", SwingConstants.CENTER);

        cardsPanel.add(createStatCard("TỔNG DOANH THU", lblRevenue, new Color(46, 204, 113), new Color(39, 174, 96)));     // Xanh lá
        cardsPanel.add(createStatCard("KHÁCH HÀNG HOẠT ĐỘNG", lblCustomers, new Color(155, 89, 182), new Color(142, 68, 173))); // Tím
        cardsPanel.add(createStatCard("TỔNG SỐ XE", lblTotalBikes, new Color(52, 152, 219), new Color(41, 128, 185)));          // Xanh dương
        cardsPanel.add(createStatCard("XE ĐANG CHO THUÊ", lblRentedBikes, new Color(230, 126, 34), new Color(211, 84, 0)));     // Cam

        add(cardsPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color bgColor, Color bottomColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 2));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(new EmptyBorder(15, 0, 10, 0));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JPanel bottomDeco = new JPanel();
        bottomDeco.setPreferredSize(new Dimension(0, 10));
        bottomDeco.setBackground(bottomColor);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(bottomDeco, BorderLayout.SOUTH);

        return card;
    }

    private void loadStatistics() {
        try {
            double revenue = hopDongBUS.layTongDoanhThu();
            int customers = hopDongBUS.layTongSoKhachHang();
            int totalBikes = hopDongBUS.layTongSoXe();
            int rentedBikes = hopDongBUS.laySoXeDangThue();

            lblRevenue.setText(String.format("%,.0f VNĐ", revenue));
            lblCustomers.setText(String.valueOf(customers));
            lblTotalBikes.setText(String.valueOf(totalBikes));
            lblRentedBikes.setText(String.valueOf(rentedBikes));

        } catch (RuntimeException ex) {
            lblRevenue.setText("Lỗi kết nối");
            lblCustomers.setText("Lỗi kết nối");
            lblTotalBikes.setText("Lỗi kết nối");
            lblRentedBikes.setText("Lỗi kết nối");
            JOptionPane.showMessageDialog(this, "Lỗi khi tải số liệu thống kê: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }
}