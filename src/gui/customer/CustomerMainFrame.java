package gui.customer;

import dto.XeMayDTO;
import gui.common.LoginFrame;
import utils.SessionUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class CustomerMainFrame extends JFrame {

    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private ArrayList<JButton> menuButtons = new ArrayList<>();
    private boolean isProfileComplete = false;

    public CustomerMainFrame() {
        setTitle("Khách Hàng - Hệ Thống Thuê Xe Máy");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(new Color(44, 53, 63));
        sidebarPanel.setPreferredSize(new Dimension(240, 0));

        JPanel headerPanel = new JPanel(new GridLayout(3, 1));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        JLabel lblRole = new JLabel("CUSTOMER", SwingConstants.CENTER);
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblRole.setForeground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Xin chào,", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblWelcome.setForeground(new Color(200, 200, 200));

        JLabel lblName = new JLabel(SessionUser.getCurrentUser().getFullName(), SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(new Color(52, 152, 219));

        headerPanel.add(lblRole);
        headerPanel.add(lblWelcome);
        headerPanel.add(lblName);
        sidebarPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel menuItemsPanel = new JPanel();
        menuItemsPanel.setLayout(new BoxLayout(menuItemsPanel, BoxLayout.Y_AXIS));
        menuItemsPanel.setOpaque(false);
        menuItemsPanel.setBorder(new EmptyBorder(20, 15, 0, 15));

        JButton btnBikes = createMenuButton("1. Danh sách Xe");
        JButton btnHistory = createMenuButton("2. Lịch sử Thuê");
        JButton btnProfile = createMenuButton("3. Hồ sơ Cá nhân");

        menuItemsPanel.add(btnBikes);
        menuItemsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuItemsPanel.add(btnHistory);
        menuItemsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        menuItemsPanel.add(btnProfile);

        sidebarPanel.add(menuItemsPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(0, 15, 20, 0));

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogout.setForeground(new Color(231, 76, 60));
        btnLogout.setBackground(new Color(44, 53, 63));
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setHorizontalAlignment(SwingConstants.LEFT);
        btnLogout.setPreferredSize(new Dimension(150, 40));

        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogout.setForeground(new Color(255, 100, 100)); }
            public void mouseExited(MouseEvent e) { btnLogout.setForeground(new Color(231, 76, 60)); }
        });
        btnLogout.addActionListener(e -> handleLogout());

        bottomPanel.add(btnLogout, BorderLayout.WEST);
        sidebarPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(sidebarPanel, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        mainContentPanel.add(new BikeBrowsingPanel(this), "BIKE_BROWSING");
        mainContentPanel.add(new RentalHistoryPanel(), "RENTAL_HISTORY");
        mainContentPanel.add(new CustomerProfilePanel(), "CUSTOMER_PROFILE");

        add(mainContentPanel, BorderLayout.CENTER);

        checkProfileStatus();

        btnBikes.addActionListener(e -> {
            if (!isProfileComplete) { requireProfile(); return; }
            showCard("BIKE_BROWSING"); setActiveButton(btnBikes);
        });

        btnHistory.addActionListener(e -> {
            if (!isProfileComplete) { requireProfile(); return; }
            mainContentPanel.add(new RentalHistoryPanel(), "RENTAL_HISTORY_REFRESH");
            showCard("RENTAL_HISTORY_REFRESH");
            setActiveButton(btnHistory);
        });

        btnProfile.addActionListener(e -> {
            mainContentPanel.add(new CustomerProfilePanel(), "PROFILE_REFRESH");
            showCard("PROFILE_REFRESH");
            setActiveButton(btnProfile);
        });
    }

    // Hàm tạo nút Menu chuẩn Admin UI
    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(44, 53, 63));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); // Xóa nền mặc định
        btn.setOpaque(true); // Cho phép tô màu nền thủ công
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover Effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!btn.getForeground().equals(new Color(52, 152, 219))) {
                    btn.setBackground(new Color(55, 65, 75)); // Sáng lên nhẹ khi đưa chuột qua
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!btn.getForeground().equals(new Color(52, 152, 219))) {
                    btn.setBackground(new Color(44, 53, 63)); // Trả về màu cũ
                }
            }
        });

        menuButtons.add(btn);
        return btn;
    }

    private void checkProfileStatus() {
        int userId = SessionUser.getCurrentUser().getUserId();
        dto.KhachHangDTO profile = new bus.KhachHangBUS().layThongTinTheoUserId(userId);

        if (profile == null) {
            isProfileComplete = false;
            // Ép chuyển sang trang Profile luôn
            showCard("CUSTOMER_PROFILE");
            // Delay nhẹ popup để UI kịp load xong
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Chào mừng bạn mới!\nVui lòng cập nhật Họ tên và CCCD để kích hoạt tính năng Thuê Xe.", "Yêu cầu hoàn tất hồ sơ", JOptionPane.INFORMATION_MESSAGE);
            });
            // Tìm nút Profile trong danh sách menuButtons để tô màu xanh cho nó
            if (menuButtons.size() >= 3) setActiveButton(menuButtons.get(2));
        } else {
            isProfileComplete = true;
            // Chọn mặc định trang đầu tiên (Danh sách xe)
            if (menuButtons.size() >= 1) setActiveButton(menuButtons.get(0));
            showCard("BIKE_BROWSING");
        }
    }

    // Hàm set Active
    private void setActiveButton(JButton activeButton) {
        for (JButton btn : menuButtons) {
            btn.setBackground(new Color(44, 53, 63));
            btn.setForeground(Color.WHITE);
        }
        activeButton.setBackground(new Color(44, 53, 63));
        activeButton.setForeground(new Color(52, 152, 219)); // Chữ màu xanh dương khi đang chọn
    }

    public void showCard(String cardName) {
        cardLayout.show(mainContentPanel, cardName);
    }

    public void showRentalRequest(XeMayDTO selectedBike) {
        mainContentPanel.add(new RentalRequestPanel(this, selectedBike), "RENTAL_REQUEST");
        showCard("RENTAL_REQUEST");
        for (JButton btn : menuButtons) {
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(44, 53, 63));
        }
    }

    private void handleLogout() {
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            SessionUser.logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void requireProfile() {
        JOptionPane.showMessageDialog(this, "Bạn phải hoàn thiện Hồ sơ cá nhân trước khi sử dụng tính năng này!", "Hệ thống", JOptionPane.WARNING_MESSAGE);
        showCard("CUSTOMER_PROFILE");
        if (menuButtons.size() >= 3) setActiveButton(menuButtons.get(2));
    }

    public void unlockFeatures() {
        isProfileComplete = true;
    }
}