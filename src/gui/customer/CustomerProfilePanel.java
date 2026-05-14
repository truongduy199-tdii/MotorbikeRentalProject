package gui.customer;

import bus.KhachHangBUS;
import dto.KhachHangDTO;
import utils.SessionUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Date;

public class CustomerProfilePanel extends JPanel {

    private KhachHangBUS khachHangBUS;
    private KhachHangDTO currentCustomer;
    private boolean isNewProfile = false;

    private JTextField txtFullName, txtCccd, txtPhone, txtEmail, txtAddress, txtBirthday, txtDriverLicense;
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
        lblTitle.setForeground(new Color(44, 53, 63));
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        centerPanel.setBackground(new Color(245, 247, 250)); // Nền đồng bộ MainFrame

        JPanel infoPanel = new JPanel(new GridLayout(8, 2, 10, 15));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 0, 0, 0), // FlatLaf xử lý shadow
                new EmptyBorder(20, 25, 20, 25)
        ));
        infoPanel.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc: 15");

        infoPanel.add(createStyledLabel("Họ và tên:"));
        txtFullName = new JTextField();
        txtFullName.putClientProperty(com.formdev.flatlaf.FlatClientProperties.PLACEHOLDER_TEXT, "Nhập họ tên đầy đủ");
        infoPanel.add(txtFullName);

        infoPanel.add(createStyledLabel("Số CCCD:"));
        txtCccd = new JTextField();
        txtCccd.putClientProperty(com.formdev.flatlaf.FlatClientProperties.PLACEHOLDER_TEXT, "Nhập 12 số CCCD");
        infoPanel.add(txtCccd);

        infoPanel.add(createStyledLabel("Số điện thoại:"));
        txtPhone = new JTextField();
        txtPhone.putClientProperty(com.formdev.flatlaf.FlatClientProperties.PLACEHOLDER_TEXT, "Nhập số điện thoại");
        infoPanel.add(txtPhone);

        infoPanel.add(createStyledLabel("Email:"));
        txtEmail = new JTextField();
        infoPanel.add(txtEmail);

        infoPanel.add(createStyledLabel("Ngày sinh (YYYY-MM-DD):"));
        txtBirthday = new JTextField();
        txtBirthday.putClientProperty(com.formdev.flatlaf.FlatClientProperties.PLACEHOLDER_TEXT, "VD: 2000-12-30");
        infoPanel.add(txtBirthday);

        infoPanel.add(createStyledLabel("Địa chỉ:"));
        txtAddress = new JTextField();
        infoPanel.add(txtAddress);

        infoPanel.add(createStyledLabel("Giấy phép lái xe:"));
        txtDriverLicense = new JTextField();
        txtDriverLicense.putClientProperty(com.formdev.flatlaf.FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã số GPLX");
        infoPanel.add(txtDriverLicense);

        JButton btnUpdateInfo = new JButton("Lưu hồ sơ");
        btnUpdateInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUpdateInfo.setBackground(new Color(23, 162, 184));
        btnUpdateInfo.setForeground(Color.WHITE);
        btnUpdateInfo.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc: 10");
        btnUpdateInfo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpdateInfo.addActionListener(e -> updateInfo());

        infoPanel.add(new JLabel(""));
        infoPanel.add(btnUpdateInfo);

        centerPanel.add(infoPanel);

        JPanel passPanel = new JPanel(new GridLayout(8, 2, 10, 15));
        passPanel.setBackground(Color.WHITE);
        passPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        passPanel.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc: 15");

        passPanel.add(createStyledLabel("Mật khẩu hiện tại:"));
        txtOldPass = new JPasswordField();
        txtOldPass.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "showRevealButton: true");
        passPanel.add(txtOldPass);

        passPanel.add(createStyledLabel("Mật khẩu mới:"));
        txtNewPass = new JPasswordField();
        txtNewPass.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "showRevealButton: true");
        passPanel.add(txtNewPass);

        passPanel.add(createStyledLabel("Nhập lại MK mới:"));
        txtConfirmPass = new JPasswordField();
        txtConfirmPass.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "showRevealButton: true");
        passPanel.add(txtConfirmPass);

        passPanel.add(new JLabel("")); passPanel.add(new JLabel(""));
        passPanel.add(new JLabel("")); passPanel.add(new JLabel(""));
        passPanel.add(new JLabel("")); passPanel.add(new JLabel(""));

        JButton btnChangePass = new JButton("Đổi mật khẩu");
        btnChangePass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnChangePass.setBackground(new Color(220, 53, 69));
        btnChangePass.setForeground(Color.WHITE);
        btnChangePass.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc: 10");
        btnChangePass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChangePass.addActionListener(e -> changePassword());

        passPanel.add(new JLabel(""));
        passPanel.add(btnChangePass);

        centerPanel.add(passPanel);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JLabel createStyledLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(80, 80, 80));
        return lbl;
    }

    private void loadCustomerData() {
        int userId = SessionUser.getCurrentUser().getUserId();
        currentCustomer = khachHangBUS.layThongTinTheoUserId(userId);

        if (currentCustomer != null) {
            isNewProfile = false;
            txtFullName.setText(currentCustomer.getFullName());
            txtFullName.setEditable(false);
            txtCccd.setText(currentCustomer.getCccd());
            txtCccd.setEditable(false);

            txtPhone.setText(currentCustomer.getPhone());
            txtEmail.setText(currentCustomer.getEmail());
            txtAddress.setText(currentCustomer.getAddress());

            if (currentCustomer.getBirthday() != null) {
                txtBirthday.setText(currentCustomer.getBirthday().toString());
            }
            txtDriverLicense.setText(currentCustomer.getDriverLicenseNumber());

        } else {
            isNewProfile = true;
            txtFullName.setEditable(true);
            txtCccd.setEditable(true);
            txtPhone.setText(SessionUser.getCurrentUser().getUsername());
        }
    }

    private void updateInfo() {
        try {
            String birthdayStr = txtBirthday.getText().trim();
            Date sqlBirthday = null;
            if (!birthdayStr.isEmpty()) {
                try {
                    sqlBirthday = Date.valueOf(birthdayStr);
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Ngày sinh không đúng định dạng. Vui lòng nhập YYYY-MM-DD (VD: 2000-12-30)");
                }
            } else {
                throw new IllegalArgumentException("Ngày sinh không được để trống!");
            }

            String driverLicense = txtDriverLicense.getText().trim();
            if (driverLicense.isEmpty()) {
                throw new IllegalArgumentException("Giấy phép lái xe không được để trống!");
            }

            if (isNewProfile) {
                KhachHangDTO newKh = new KhachHangDTO();
                newKh.setUserId(SessionUser.getCurrentUser().getUserId());
                newKh.setFullName(txtFullName.getText().trim());
                newKh.setCccd(txtCccd.getText().trim());
                newKh.setPhone(txtPhone.getText().trim());
                newKh.setEmail(txtEmail.getText().trim());
                newKh.setAddress(txtAddress.getText().trim());
                newKh.setBirthday(sqlBirthday);
                newKh.setDriverLicenseNumber(driverLicense);

                if (khachHangBUS.themMoiHoSo(newKh)) {
                    JOptionPane.showMessageDialog(this, "Tạo hồ sơ thành công! Bây giờ bạn đã có thể thuê xe.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    isNewProfile = false; // Chuyển trạng thái
                    txtFullName.setEditable(false);
                    txtCccd.setEditable(false);

                    Container parent = SwingUtilities.getAncestorOfClass(CustomerMainFrame.class, this);
                    if (parent != null) {
                        ((CustomerMainFrame) parent).unlockFeatures();
                    }
                }
            } else {
                currentCustomer.setPhone(txtPhone.getText().trim());
                currentCustomer.setEmail(txtEmail.getText().trim());
                currentCustomer.setAddress(txtAddress.getText().trim());
                currentCustomer.setBirthday(sqlBirthday);
                currentCustomer.setDriverLicenseNumber(driverLicense);

                if (khachHangBUS.capNhatThongTin(currentCustomer)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thông tin liên hệ thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePassword() {
        try {
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
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }

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