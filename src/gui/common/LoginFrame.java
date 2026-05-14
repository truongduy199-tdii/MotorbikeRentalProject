package gui.common;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import bus.TaiKhoanBUS;
import utils.SessionUser;
import dto.TaiKhoanDTO;
import gui.admin.AdminMainFrame;
import gui.customer.CustomerMainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblRegister;

    private TaiKhoanBUS taiKhoanBUS;

    public LoginFrame() {
        taiKhoanBUS = new TaiKhoanBUS();

        setTitle("Đăng Nhập - Hệ Thống Cho Thuê Xe Máy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // màu nền tổng thể
        getContentPane().setBackground(new Color(245, 247, 250));
        setLayout(new GridBagLayout());

        initComponents();
    }

    private void initComponents() {
        // PANEL CHÍNH
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Thêm bo góc và bóng đổ
        loginPanel.putClientProperty(FlatClientProperties.STYLE,
                "arc: 20; " +
                        "[light]background: darken(@background, 0%);");

        // PHẦN TIÊU ĐỀ
        JLabel lblLogo = new JLabel();
        lblLogo.setIcon(new ImageIcon(getClass().getResource("/images/motor.png")));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("HỆ THỐNG CHO THUÊ XE MÁY");
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

        // NÚT ĐĂNG NHẬP
        btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(25, 118, 207));
        btnLogin.setFocusPainted(false);
        btnLogin.putClientProperty(FlatClientProperties.STYLE, "arc: 10");
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setMaximumSize(new Dimension(150, 45));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hiệu ứng Hover cho nút bấm
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(21, 101, 192));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(new Color(25, 118, 210));
            }
        });

        // Action click Đăng nhập
        btnLogin.addActionListener(e -> handleLogin());

        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linksPanel.setBackground(Color.WHITE);
        linksPanel.setMaximumSize(new Dimension(300, 30));

        lblRegister = new JLabel("<html><u>Chưa có tài khoản? Đăng ký ngay!</u></html>");
        // Thêm sự kiện click chuột cho link Đăng ký bên trong LoginFrame
        lblRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose(); // Đóng form Login
                new RegisterFrame().setVisible(true); // Bật form Register
            }
        });
        lblRegister.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRegister.setForeground(new Color(25, 118, 210));
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        linksPanel.add(lblRegister);

        // THÊM CÁC THÀNH PHẦN VÀO PANEL
        loginPanel.add(lblLogo);
        loginPanel.add(lblTitle);

        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginPanel.add(txtUsername);

        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginPanel.add(txtPassword);

        loginPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        loginPanel.add(btnLogin);

        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginPanel.add(linksPanel);

        add(loginPanel, new GridBagConstraints());
    }

    // Thay thế logic đăng nhập bên trong sự kiện click của btnLogin
    private void handleLogin() {
        try {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            // Đẩy xuống BUS xử lý, nếu có lỗi nó sẽ nhảy xuống khối catch
            TaiKhoanDTO account = taiKhoanBUS.kiemTraDangNhap(username, password);

            SessionUser.setCurrentUser(account);

            // Phân quyền chuyển trang
            if (account.getRole() != null && account.getRole().equalsIgnoreCase("ADMIN")) {
                gui.admin.AdminMainFrame adminFrame = new gui.admin.AdminMainFrame();
                adminFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                adminFrame.setVisible(true);
            } else {
                gui.customer.CustomerMainFrame customerFrame = new gui.customer.CustomerMainFrame();
                customerFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                customerFrame.setVisible(true);
            }
            this.dispose(); // Đóng form Login

        } catch (IllegalArgumentException ex) {
            // Bắt lỗi: Sai pass, trống thông tin, TÀI KHOẢN BỊ KHÓA
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Đăng Nhập", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            // Bắt lỗi: Mất kết nối CSDL
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi Máy Chủ", JOptionPane.ERROR_MESSAGE);
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
            new LoginFrame().setVisible(true);
        });
    }
}