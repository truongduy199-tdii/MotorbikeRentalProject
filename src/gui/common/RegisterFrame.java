package gui.common;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import bus.TaiKhoanBUS;
import dto.TaiKhoanDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtRePassword;
    private JButton btnRegister;
    private JLabel lblBackToLogin;

    private TaiKhoanBUS taiKhoanBUS;

    public RegisterFrame() {
        taiKhoanBUS = new TaiKhoanBUS();

        setTitle("Đăng Ký - Hệ Thống Cho Thuê Xe Máy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // màu nền tổng thể giống LoginFrame
        getContentPane().setBackground(new Color(245, 247, 250));
        setLayout(new GridBagLayout());

        initComponents();
    }

    private void initComponents() {
        // PANEL CHÍNH
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));
        registerPanel.setBackground(Color.WHITE);
        registerPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Thêm bo góc và bóng đổ (FlatLaf)
        registerPanel.putClientProperty(FlatClientProperties.STYLE,
                "arc: 20; " +
                        "[light]background: darken(@background, 0%);");

        // PHẦN TIÊU ĐỀ (Giữ lại logo nếu bạn muốn đồng bộ, hoặc có thể bỏ đi để tiết kiệm không gian dọc)
        JLabel lblLogo = new JLabel();
        try {
            lblLogo.setIcon(new ImageIcon(getClass().getResource("/images/motor.png")));
        } catch (Exception e) {
            // Bỏ qua nếu không load được ảnh
        }
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("ĐĂNG KÝ TÀI KHOẢN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(25, 118, 210));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(10, 0, 30, 0));

        // 1. Username
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tên đăng nhập");
        txtUsername.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txtUsername.setMaximumSize(new Dimension(300, 40));

        // 2. Password
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Mật khẩu");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        txtPassword.setMaximumSize(new Dimension(300, 40));

        // 3. Re-Password (Nhập lại mật khẩu)
        txtRePassword = new JPasswordField(20);
        txtRePassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtRePassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập lại mật khẩu");
        txtRePassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
        txtRePassword.setMaximumSize(new Dimension(300, 40));

        // NÚT ĐĂNG KÝ
        btnRegister = new JButton("ĐĂNG KÝ");
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBackground(new Color(25, 118, 207));
        btnRegister.setFocusPainted(false);
        btnRegister.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setMaximumSize(new Dimension(150, 45));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hiệu ứng Hover cho nút bấm
        btnRegister.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btnRegister.setBackground(new Color(21, 101, 192));
            }
            public void mouseExited(MouseEvent evt) {
                btnRegister.setBackground(new Color(25, 118, 210));
            }
        });

        // Action click Đăng ký
        btnRegister.addActionListener(e -> handleRegister());

        // LINK QUAY LẠI ĐĂNG NHẬP
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linksPanel.setBackground(Color.WHITE);
        linksPanel.setMaximumSize(new Dimension(300, 30));

        lblBackToLogin = new JLabel("<html><u>Đã có tài khoản? Quay lại đăng nhập</u></html>");
        lblBackToLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBackToLogin.setForeground(new Color(25, 118, 210));
        lblBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Sự kiện click để quay về Login
        lblBackToLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); // Đóng form Register
                new LoginFrame().setVisible(true); // Mở form Login
            }
        });

        linksPanel.add(lblBackToLogin);

        // THÊM CÁC THÀNH PHẦN VÀO PANEL 
        registerPanel.add(lblLogo);
        registerPanel.add(lblTitle);

        registerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        registerPanel.add(txtUsername);

        registerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        registerPanel.add(txtPassword);

        registerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        registerPanel.add(txtRePassword);

        registerPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        registerPanel.add(btnRegister);

        registerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        registerPanel.add(linksPanel);

        add(registerPanel, new GridBagConstraints());
    }

    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String rePassword = new String(txtRePassword.getPassword());

        // 1. Kiểm tra rỗng
        if (username.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Kiểm tra mật khẩu khớp nhau
        if (!password.equals(rePassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu nhập lại không khớp!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TaiKhoanDTO newAccount = new TaiKhoanDTO();
        newAccount.setUsername(username);
        newAccount.setPassword(password);

        // Lưu ý: Nếu hàm dangKy trong BUS của bạn có tham số khác, hãy điều chỉnh lại cho khớp.
        String message = taiKhoanBUS.dangKy(newAccount, rePassword, password);

        if (message.equals("Đăng ký thành công!")) {
            JOptionPane.showMessageDialog(this, message + " Vui lòng đăng nhập.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Chuyển hướng về trang Đăng nhập
            this.dispose();
            new LoginFrame().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, message, "Lỗi đăng ký", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        SwingUtilities.invokeLater(() -> {
            new RegisterFrame().setVisible(true);
        });
    }
}