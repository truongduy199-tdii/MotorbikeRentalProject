package gui.customer;

import bus.KhachHangBUS;
import dto.KhachHangDTO;
import utils.SessionUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class CustomerProfilePanel extends JPanel {

    private KhachHangBUS khachHangBUS;
    private KhachHangDTO currentCustomer;

    private JTextField txtFullName, txtCccd, txtPhone, txtEmail, txtAddress;
    private JPasswordField txtOldPass, txtNewPass, txtConfirmPass;

    public CustomerProfilePanel() {
        this.khachHangBUS = new KhachHangBUS();
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        initComponents();
        loadCustomerData();
    }

    private void initComponents() {
        JLabel lblTitle = new JLabel("HỒ SƠ CÁ NHÂN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(25, 118, 210));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        centerPanel.setBackground(Color.WHITE);

        // --- CỘT 1: THÔNG TIN CÁ NHÂN ---
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Thông tin liên hệ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        infoPanel.add(new JLabel("Họ và tên (Cố định):"));
        txtFullName = new JTextField(); txtFullName.setEditable(false);
        infoPanel.add(txtFullName);

        infoPanel.add(new JLabel("Số CCCD (Cố định):"));
        txtCccd = new JTextField(); txtCccd.setEditable(false);
        infoPanel.add(txtCccd);

        infoPanel.add(new JLabel("Số điện thoại:"));
        txtPhone = new JTextField();
        infoPanel.add(txtPhone);

        infoPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        infoPanel.add(txtEmail);

        infoPanel.add(new JLabel("Địa chỉ:"));
        txtAddress = new JTextField();
        infoPanel.add(txtAddress);

        JButton btnUpdateInfo = new JButton("Cập nhật thông tin");
        btnUpdateInfo.setBackground(new Color(23, 162, 184));
        btnUpdateInfo.setForeground(Color.WHITE);
        btnUpdateInfo.addActionListener(e -> updateInfo());
        infoPanel.add(new JLabel("")); // Dummy cell
        infoPanel.add(btnUpdateInfo);

        centerPanel.add(infoPanel);

        // --- CỘT 2: ĐỔI MẬT KHẨU ---
        JPanel passPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        passPanel.setBackground(Color.WHITE);
        passPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), "Đổi mật khẩu", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        passPanel.add(new JLabel("Mật khẩu hiện tại:"));
        txtOldPass = new JPasswordField();
        passPanel.add(txtOldPass);

        passPanel.add(new JLabel("Mật khẩu mới:"));
        txtNewPass = new JPasswordField();
        passPanel.add(txtNewPass);

        passPanel.add(new JLabel("Nhập lại MK mới:"));
        txtConfirmPass = new JPasswordField();
        passPanel.add(txtConfirmPass);

        JButton btnChangePass = new JButton("Đổi mật khẩu");
        btnChangePass.setBackground(new Color(220, 53, 69));
        btnChangePass.setForeground(Color.WHITE);
        btnChangePass.addActionListener(e -> changePassword());
        passPanel.add(new JLabel("")); // Dummy cell
        passPanel.add(btnChangePass);

        centerPanel.add(passPanel);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadCustomerData() {
        int userId = SessionUser.getCurrentUser().getUserId();
        currentCustomer = khachHangBUS.layThongTinTheoUserId(userId);

        if (currentCustomer != null) {
            txtFullName.setText(currentCustomer.getFullName());
            txtCccd.setText(currentCustomer.getCccd());
            txtPhone.setText(currentCustomer.getPhone());
            txtEmail.setText(currentCustomer.getEmail());
            txtAddress.setText(currentCustomer.getAddress());
        }
    }

    private void updateInfo() {
        if (currentCustomer != null) {
            currentCustomer.setPhone(txtPhone.getText());
            currentCustomer.setEmail(txtEmail.getText());
            currentCustomer.setAddress(txtAddress.getText());

            if (khachHangBUS.capNhatThongTin(currentCustomer)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changePassword() {
        String oldPass = new String(txtOldPass.getPassword());
        String newPass = new String(txtNewPass.getPassword());
        String confirmPass = new String(txtConfirmPass.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới và xác nhận không khớp!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String oldHash = hashSHA256(oldPass);
        String newHash = hashSHA256(newPass);
        int userId = SessionUser.getCurrentUser().getUserId();

        if (khachHangBUS.doiMatKhau(userId, oldHash, newHash)) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công! Bạn có thể dùng mật khẩu mới ở lần đăng nhập sau.");
            txtOldPass.setText(""); txtNewPass.setText(""); txtConfirmPass.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Mật khẩu hiện tại không đúng!", "Từ chối", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Hàm băm mật khẩu chuẩn SHA-256
    private String hashSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi thuật toán mã hóa", e);
        }
    }
}