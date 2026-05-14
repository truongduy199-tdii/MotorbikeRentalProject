package gui.customer;

import bus.HopDongBUS;
import dto.HopDongDTO;
import dto.XeMayDTO;
import utils.SessionUser;
import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RentalRequestPanel extends JPanel {
    private XeMayDTO bike;
    private CustomerMainFrame parentFrame;

    // Các trường nhập liệu
    private JTextField txtStartDate, txtStartTime, txtEndDate;
    private JComboBox<String> cbEndTime;

    // Các nhãn hiển thị tiền
    private JLabel lblDays, lblRentFee, lblTotal;

    // Biến lưu trữ dữ liệu tính toán
    private Timestamp finalStartTs, finalEndTs;
    private double depositAmount = 1000000.0;
    private double totalAmount = 0.0;
    private long totalDays = 0;

    public RentalRequestPanel(CustomerMainFrame parentFrame, XeMayDTO bike) {
        this.parentFrame = parentFrame;
        this.bike = bike;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        initComponents();
    }

    private void initComponents() {
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(new Color(245, 247, 250));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(new EmptyBorder(25, 40, 25, 40));
        formCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20");

        // 1. TIÊU ĐỀ
        JLabel lblTitle = new JLabel("XÁC NHẬN YÊU CẦU THUÊ XE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(44, 53, 63));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        formCard.add(lblTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2. THÔNG TIN CHI TIẾT XE
        JPanel bikeInfoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        bikeInfoPanel.setBackground(new Color(240, 248, 255)); // Nền xanh nhạt
        bikeInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        bikeInfoPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        bikeInfoPanel.add(new JLabel("<html><b>Tên xe:</b> " + bike.getVehicleName().toUpperCase() + "</html>"));
        bikeInfoPanel.add(new JLabel("<html><b>Biển số:</b> " + bike.getLicensePlate() + "</html>"));
        bikeInfoPanel.add(new JLabel("<html><b>Hãng/Màu:</b> " + bike.getBrand() + " - " + bike.getColor() + "</html>"));
        bikeInfoPanel.add(new JLabel("<html><b>Giá thuê:</b> <font color='red'>" + String.format("%,.0f VNĐ/ngày", bike.getRentalPricePerDay()) + "</font></html>"));

        formCard.add(bikeInfoPanel);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // 3. FORM NHẬP THỜI GIAN
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thời gian thuê", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        inputPanel.add(createStyledLabel("Ngày lấy xe (dd/MM/yyyy):"));
        txtStartDate = new JTextField();
        txtStartDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "VD: 15/05/2026");
        inputPanel.add(txtStartDate);

        inputPanel.add(createStyledLabel("Giờ lấy xe (HH:mm):"));
        txtStartTime = new JTextField("08:00"); // Gợi ý mặc định
        txtStartTime.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "VD: 08:30");
        inputPanel.add(txtStartTime);

        inputPanel.add(createStyledLabel("Ngày trả xe (dd/MM/yyyy):"));
        txtEndDate = new JTextField();
        txtEndDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "VD: 20/05/2026");
        inputPanel.add(txtEndDate);

        inputPanel.add(createStyledLabel("Giờ trả xe:"));
        cbEndTime = new JComboBox<>(new String[]{"10:00", "16:00"}); // Combobox cố định giờ
        cbEndTime.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputPanel.add(cbEndTime);

        formCard.add(inputPanel);
        formCard.add(Box.createRigidArea(new Dimension(0, 10)));

        // NÚT TÍNH TOÁN
        JButton btnCalculate = new JButton("Kiểm tra & Tính tiền");
        btnCalculate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCalculate.setBackground(new Color(255, 193, 7)); // Màu vàng cảnh báo/info
        btnCalculate.setForeground(Color.DARK_GRAY);
        btnCalculate.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnCalculate.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCalculate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalculate.addActionListener(e -> calculateFinancials(true));
        formCard.add(btnCalculate);
        formCard.add(Box.createRigidArea(new Dimension(0, 15)));

        // 4. BẢNG HIỂN THỊ TÀI CHÍNH & THANH TOÁN
        JPanel financePanel = new JPanel(new GridLayout(5, 1, 0, 8));
        financePanel.setBackground(new Color(250, 250, 250));
        financePanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        financePanel.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        lblDays = new JLabel("Số ngày thuê: 0 ngày");
        lblRentFee = new JLabel("Tiền thuê xe: 0 VNĐ");
        JLabel lblDepositFixed = new JLabel("Tiền cọc (Cố định): 1,000,000 VNĐ");

        lblTotal = new JLabel("TỔNG CHI TRẢ: 0 VNĐ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(new Color(220, 53, 69)); // Chữ đỏ nổi bật

        JLabel lblPayment = new JLabel("Phương thức thanh toán: BANKING (Chuyển khoản)");
        lblPayment.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblPayment.setForeground(new Color(40, 167, 69));

        financePanel.add(lblDays);
        financePanel.add(lblRentFee);
        financePanel.add(lblDepositFixed);
        financePanel.add(lblTotal);
        financePanel.add(lblPayment);

        formCard.add(financePanel);
        formCard.add(Box.createRigidArea(new Dimension(0, 25)));

        // 5. NÚT HÀNH ĐỘNG
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton btnCancel = new JButton("Quay lại");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setBackground(new Color(108, 117, 125));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnCancel.setPreferredSize(new Dimension(120, 40));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> parentFrame.showCard("BIKE_BROWSING"));

        JButton btnSubmit = new JButton("Xác nhận & Thuê");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(40, 167, 69));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnSubmit.setPreferredSize(new Dimension(160, 40));
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSubmit.addActionListener(e -> submitRequest());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSubmit);
        formCard.add(btnPanel);

        wrapperPanel.add(formCard);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createStyledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }

    private boolean calculateFinancials(boolean showAlert) {
        try {
            String startDateStr = txtStartDate.getText().trim();
            String startTimeStr = txtStartTime.getText().trim();
            String endDateStr = txtEndDate.getText().trim();
            String endTimeStr = cbEndTime.getSelectedItem().toString();

            if (startDateStr.isEmpty() || startTimeStr.isEmpty() || endDateStr.isEmpty()) {
                if (showAlert) JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thời gian lấy và trả xe!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            sdf.setLenient(false);
            Date startD = sdf.parse(startDateStr + " " + startTimeStr);
            Date endD = sdf.parse(endDateStr + " " + endTimeStr);

            finalStartTs = new Timestamp(startD.getTime());
            finalEndTs = new Timestamp(endD.getTime());

            if (finalEndTs.before(finalStartTs) || finalEndTs.equals(finalStartTs)) {
                if (showAlert) JOptionPane.showMessageDialog(this, "Thời gian trả xe phải sau thời gian lấy xe!", "Lỗi logic", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            long diffMillis = finalEndTs.getTime() - finalStartTs.getTime();
            totalDays = diffMillis / (1000 * 60 * 60 * 24);
            if (diffMillis % (1000 * 60 * 60 * 24) > 0) {
                totalDays++;
            }
            if (totalDays <= 0) totalDays = 1;

            double rentFee = totalDays * bike.getRentalPricePerDay();
            totalAmount = depositAmount + rentFee;

            lblDays.setText("Số ngày thuê: " + totalDays + " ngày");
            lblRentFee.setText("Tiền thuê xe: " + String.format("%,.0f VNĐ", rentFee));
            lblTotal.setText("TỔNG CHI TRẢ: " + String.format("%,.0f VNĐ", totalAmount));

            return true;

        } catch (Exception ex) {
            if (showAlert) JOptionPane.showMessageDialog(this, "Sai định dạng ngày/giờ!\n- Ngày nhập dạng: dd/MM/yyyy (VD: 15/05/2026)\n- Giờ nhập dạng: HH:mm (VD: 08:30)", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void submitRequest() {
        if (!calculateFinancials(true)) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn gửi yêu cầu thuê xe này?\nTổng số tiền phải thanh toán là: " + String.format("%,.0f VNĐ", totalAmount) + "\n(Bao gồm tiền cọc và tiền thuê)",
                "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                HopDongDTO hd = new HopDongDTO();
                hd.setCustomerId(SessionUser.getCurrentUser().getUserId());
                hd.setVehicleId(bike.getVehicleId());
                hd.setRentalStart(finalStartTs);
                hd.setRentalEnd(finalEndTs);
                hd.setDepositAmount(depositAmount);
                hd.setTotalAmount(totalAmount);
                hd.setContractStatus("PENDING");

                HopDongBUS hopDongBUS = new HopDongBUS();
                if (hopDongBUS.taoYeuCauThue(hd)) {
                    JOptionPane.showMessageDialog(this, "Gửi yêu cầu thuê xe thành công!\nTrạng thái đơn: ĐANG CHỜ DUYỆT (PENDING).\nVui lòng chờ Admin xác nhận.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    parentFrame.showCard("RENTAL_HISTORY_REFRESH"); // Chuyển về và làm mới tab lịch sử
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi khi gửi yêu cầu. Vui lòng thử lại sau!", "Lỗi máy chủ", JOptionPane.ERROR_MESSAGE);
                }
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}