package gui.customer;

import bus.HopDongBUS;
import dto.HopDongDTO;
import dto.XeMayDTO;
import utils.SessionUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Timestamp;

public class RentalRequestPanel extends JPanel {
    private XeMayDTO bike;
    private CustomerMainFrame parentFrame;
    private JTextField txtStart, txtEnd;

    public RentalRequestPanel(CustomerMainFrame parentFrame, XeMayDTO bike) {
        this.parentFrame = parentFrame;
        this.bike = bike;
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 50, 30, 50));
        initComponents();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("TẠO YÊU CẦU THUÊ XE: " + bike.getVehicleName().toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(25, 118, 210));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15)); // Đã giảm số dòng
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Ngày bắt đầu (yyyy-mm-dd hh:mm:ss):"));
        txtStart = new JTextField("2026-05-20 08:00:00");
        formPanel.add(txtStart);

        formPanel.add(new JLabel("Ngày kết thúc (yyyy-mm-dd hh:mm:ss):"));
        txtEnd = new JTextField("2026-05-22 08:00:00");
        formPanel.add(txtEnd);

        formPanel.add(new JLabel("Đơn giá thuê theo ngày:"));
        formPanel.add(new JLabel(String.format("%,.0f VNĐ/Ngày", bike.getRentalPricePerDay())));

        formPanel.add(new JLabel("Tiền cọc bắt buộc:"));
        formPanel.add(new JLabel("1,000,000 VNĐ")); // Cập nhật đúng 1 triệu

        add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton btnBack = new JButton("Quay lại");
        btnBack.addActionListener(e -> parentFrame.showCard("BIKE_BROWSING"));

        JButton btnSubmit = new JButton("Xác nhận & Gửi yêu cầu");
        btnSubmit.setBackground(new Color(40, 167, 69));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.addActionListener(e -> submitRequest());

        bottomPanel.add(btnBack);
        bottomPanel.add(btnSubmit);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void submitRequest() {
        try {
            Timestamp start = Timestamp.valueOf(txtStart.getText().trim());
            Timestamp end = Timestamp.valueOf(txtEnd.getText().trim());

            // Tự tính số ngày thuê thực tế
            long diffMillis = end.getTime() - start.getTime();
            if (diffMillis <= 0) {
                JOptionPane.showMessageDialog(this, "Ngày trả xe phải lớn hơn ngày mượn xe!", "Lỗi logic", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Tính số ngày (Làm tròn lên, ví dụ thuê 1.5 ngày tính 2 ngày)
            int days = (int) Math.ceil((double) diffMillis / (1000 * 60 * 60 * 24));
            double deposit = 1000000.0;
            double totalAmount = deposit + (days * bike.getRentalPricePerDay());

            HopDongDTO hd = new HopDongDTO();
            hd.setCustomerId(SessionUser.getCurrentUser().getUserId());
            hd.setVehicleId(bike.getVehicleId());
            hd.setRentalStart(start);
            hd.setRentalEnd(end);
            hd.setDepositAmount(deposit);
            hd.setTotalAmount(totalAmount);

            HopDongBUS hopDongBUS = new HopDongBUS();
            if (hopDongBUS.taoYeuCauThue(hd)) {
                JOptionPane.showMessageDialog(this, "Gửi yêu cầu thành công!\nSố ngày thuê: " + days + " ngày\nTổng tiền dự kiến (đã gồm cọc): " + String.format("%,.0f VNĐ", totalAmount) + "\n\nVui lòng chờ Admin duyệt hợp đồng.");
                parentFrame.showCard("RENTAL_HISTORY");
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi gửi yêu cầu. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException ex) {
            // Lỗi do người dùng gõ sai định dạng thời gian
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ!\nVui lòng nhập đúng: yyyy-mm-dd hh:mm:ss\n(Ví dụ: 2026-05-20 14:30:00)", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            // Lỗi kết nối CSDL
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống CSDL: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }
}