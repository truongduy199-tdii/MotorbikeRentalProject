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
    private JComboBox<String> cbType;
    private JLabel lblTotal;

    public RentalRequestPanel(CustomerMainFrame parentFrame, XeMayDTO bike) {
        this.parentFrame = parentFrame;
        this.bike = bike;
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(30, 50, 30, 50));
        initComponents();
    }

    private void initComponents() {
        // Tiêu đề
        JLabel lblTitle = new JLabel("TẠO YÊU CẦU THUÊ XE: " + bike.getVehicleName().toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(25, 118, 210));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // Form điền thông tin
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Loại hình thuê:"));
        cbType = new JComboBox<>(new String[]{"Thuê theo Ngày", "Thuê theo Giờ"});
        formPanel.add(cbType);

        formPanel.add(new JLabel("Ngày bắt đầu (Định dạng: yyyy-mm-dd hh:mm:ss):"));
        txtStart = new JTextField("2026-05-20 08:00:00");
        formPanel.add(txtStart);

        formPanel.add(new JLabel("Ngày kết thúc (Định dạng: yyyy-mm-dd hh:mm:ss):"));
        txtEnd = new JTextField("2026-05-22 08:00:00");
        formPanel.add(txtEnd);

        formPanel.add(new JLabel("Giá tham khảo:"));
        formPanel.add(new JLabel(String.format("%,.0f VNĐ/Ngày | %,.0f VNĐ/Giờ", bike.getRentalPricePerDay(), bike.getRentalPricePerHour())));

        formPanel.add(new JLabel("Tiền cọc yêu cầu:"));
        formPanel.add(new JLabel("500,000 VNĐ"));

        add(formPanel, BorderLayout.CENTER);

        // Nút bấm
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
            HopDongDTO hd = new HopDongDTO();
            hd.setCustomerId(SessionUser.getCurrentUser().getUserId()); // Dùng userId
            hd.setVehicleId(bike.getVehicleId());
            hd.setRentalStart(Timestamp.valueOf(txtStart.getText()));
            hd.setRentalEnd(Timestamp.valueOf(txtEnd.getText()));
            hd.setRentalType(cbType.getSelectedIndex() == 0 ? "DAY" : "HOUR");

            // Giả lập tính tiền cọc và tổng tiền cơ bản
            hd.setDepositAmount(500000);
            hd.setTotalAmount(bike.getRentalPricePerDay() * 2); // Tạm tính 2 ngày

            HopDongBUS hopDongBUS = new HopDongBUS();
            if (hopDongBUS.taoYeuCauThue(hd)) {
                JOptionPane.showMessageDialog(this, "Gửi yêu cầu thành công! Vui lòng chờ Admin duyệt hợp đồng.");
                parentFrame.showCard("RENTAL_HISTORY"); // Chuyển sang lịch sử để xem
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi gửi yêu cầu. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày tháng đúng định dạng: yyyy-mm-dd hh:mm:ss", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }
}