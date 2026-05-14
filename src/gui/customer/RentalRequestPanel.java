package gui.customer;

import bus.HopDongBUS;
import dto.HopDongDTO;
import dto.XeMayDTO;
import utils.SessionUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RentalRequestPanel extends JPanel {

    private CustomerMainFrame parentFrame;
    private XeMayDTO currentBike;
    private HopDongBUS hopDongBUS;

    private JLabel lblBikeName, lblLicense, lblPrice;
    private JTextField txtStartDate, txtEndDate;
    private JLabel lblTotalCost;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public RentalRequestPanel(CustomerMainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.hopDongBUS = new HopDongBUS();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 50, 30, 50));
        setBackground(new Color(245, 247, 250));

        initComponents();
    }

    private void initComponents() {
        // Tiêu đề
        JLabel lblTitle = new JLabel("YÊU CẦU THUÊ XE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // Form điền thông tin
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Thông tin xe
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Xe được chọn:"), gbc);
        gbc.gridx = 1;
        lblBikeName = new JLabel("...");
        lblBikeName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(lblBikeName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Biển số:"), gbc);
        gbc.gridx = 1;
        lblLicense = new JLabel("...");
        formPanel.add(lblLicense, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Giá thuê/ngày:"), gbc);
        gbc.gridx = 1;
        lblPrice = new JLabel("0 VNĐ");
        formPanel.add(lblPrice, gbc);

        // Nhập ngày
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày bắt đầu (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        txtStartDate = new JTextField(15);
        // Mặc định là ngày hôm nay
        txtStartDate.setText(dateFormat.format(new Date()));
        formPanel.add(txtStartDate, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Ngày dự kiến trả (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        txtEndDate = new JTextField(15);
        formPanel.add(txtEndDate, gbc);

        // Tổng tiền
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Tổng tiền dự kiến:"), gbc);
        gbc.gridx = 1;
        lblTotalCost = new JLabel("0 VNĐ");
        lblTotalCost.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalCost.setForeground(Color.RED);
        formPanel.add(lblTotalCost, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Các nút bấm
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(245, 247, 250));

        JButton btnCalculate = new JButton("Tính tiền");
        JButton btnSubmit = new JButton("Gửi yêu cầu thuê");
        btnSubmit.setBackground(new Color(0, 123, 255));
        btnSubmit.setForeground(Color.WHITE);

        btnCalculate.addActionListener(e -> calculateTotal());

        btnSubmit.addActionListener(e -> submitRequest());

        bottomPanel.add(btnCalculate);
        bottomPanel.add(btnSubmit);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setVehicleData(XeMayDTO bike) {
        this.currentBike = bike;
        lblBikeName.setText(bike.getVehicleName());
        lblLicense.setText(bike.getLicensePlate());
        lblPrice.setText(String.format("%,.0f VNĐ", bike.getRentalPricePerDay()));
        txtEndDate.setText("");
        lblTotalCost.setText("0 VNĐ");
    }

    private long calculateTotal() {
        if (currentBike == null) return -1;
        try {
            Date start = dateFormat.parse(txtStartDate.getText().trim());
            Date end = dateFormat.parse(txtEndDate.getText().trim());

            // Validate logic ngày
            if (end.before(start)) {
                JOptionPane.showMessageDialog(this, "Ngày trả phải lớn hơn hoặc bằng ngày bắt đầu!", "Lỗi ngày", JOptionPane.ERROR_MESSAGE);
                return -1;
            }

            // Tính số ngày (nếu trả trong ngày tính là 1 ngày)
            long diffInMillies = Math.abs(end.getTime() - start.getTime());
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;

            long total = (long) (diffInDays * currentBike.getRentalPricePerDay());
            lblTotalCost.setText(String.format("%,d VNĐ", total));
            return total;

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng ngày dd/MM/yyyy", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private void submitRequest() {
        long totalCost = calculateTotal();
        if (totalCost == -1) return;

        try {
            Date start = dateFormat.parse(txtStartDate.getText().trim());
            Date end = dateFormat.parse(txtEndDate.getText().trim());

            // Tạo DTO mới
            HopDongDTO newContract = new HopDongDTO();
            // Tạm thời gen mã ngẫu nhiên hoặc để DB tự tăng
            newContract.setContractCode("HD_" + System.currentTimeMillis());

            String customerName = SessionUser.getCurrentUser() != null ? SessionUser.getCurrentUser().getUsername() : "Unknown";
            newContract.setCustomerName(customerName);
            newContract.setVehicleName(currentBike.getVehicleName());
            newContract.setRentalStart(new Timestamp(start.getTime()));
            newContract.setRentalEnd(new Timestamp(end.getTime()));
            newContract.setDepositAmount(totalCost); // Giả sử tiền cọc = tổng tiền
            newContract.setContractStatus("Chờ duyệt");

            // Lưu ý: Bạn cần thêm hàm themHopDong(HopDongDTO hd) vào HopDongBUS và HopDongDAO
            // hopDongBUS.themHopDong(newContract); 

            JOptionPane.showMessageDialog(this, "Gửi yêu cầu thuê xe thành công! Vui lòng chờ Admin duyệt.");
            parentFrame.showHistory();

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }
}