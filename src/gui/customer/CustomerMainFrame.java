package gui.customer;

import com.formdev.flatlaf.FlatClientProperties;
import dto.XeMayDTO;
import gui.common.LoginFrame;
import utils.SessionUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomerMainFrame extends JFrame {

    private JPanel sidebarPanel;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    private BikeBrowsingPanel bikeBrowsingPanel;
    private RentalRequestPanel rentalRequestPanel;
    private RentalHistoryPanel rentalHistoryPanel;

    // Màu sắc chủ đạo (Giữ nguyên style của Admin)
    private final Color SIDEBAR_BG = new Color(33, 43, 54);
    private final Color SIDEBAR_HOVER = new Color(43, 55, 70);
    private final Color TEXT_COLOR = Color.WHITE;

    public CustomerMainFrame() {
        setTitle("Khách Hàng - Hệ Thống Thuê Xe Máy");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initComponents();
    }

    private void initComponents() {
        // 1. SIDEBAR
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBackground(SIDEBAR_BG);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Tên Khách hàng đang đăng nhập
        String userName = "Khách Hàng";
        if (SessionUser.getCurrentUser() != null) {
            // Giả sử TaiKhoanDTO có hàm getUsername(), bạn có thể đổi thành tên thực tế
            userName = SessionUser.getCurrentUser().getUsername();
        }

        JLabel lblTitle = new JLabel("Xin chào, " + userName);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_COLOR);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Menu
        JButton btnBrowse = createMenuButton("1. Xem danh sách xe");
        JButton btnHistory = createMenuButton("2. Lịch sử thuê xe");
        JButton btnLogout = createMenuButton("Đăng xuất");
        btnLogout.setForeground(new Color(255, 82, 82));

        // Thêm vào Sidebar
        sidebarPanel.add(lblTitle);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(btnBrowse);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(btnHistory);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(btnLogout);

        // 2. MAIN CONTENT (CardLayout)
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(245, 247, 250));

        // Khởi tạo các Panel con (Truyền instance của Frame này vào để có thể chuyển trang)
        bikeBrowsingPanel = new BikeBrowsingPanel(this);
        rentalRequestPanel = new RentalRequestPanel(this);
        rentalHistoryPanel = new RentalHistoryPanel();

        // Add vào CardLayout
        mainContentPanel.add(bikeBrowsingPanel, "Browse");
        mainContentPanel.add(rentalRequestPanel, "Request");
        mainContentPanel.add(rentalHistoryPanel, "History");

        // ACTION CHO MENU
        btnBrowse.addActionListener(e -> {
            bikeBrowsingPanel.loadData();
            cardLayout.show(mainContentPanel, "Browse");
        });

        btnHistory.addActionListener(e -> {
            rentalHistoryPanel.loadData();
            cardLayout.show(mainContentPanel, "History");
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                SessionUser.logout();
                this.dispose();
                new LoginFrame().setVisible(true);
            }
        });

        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }

    // Hàm public để BikeBrowsingPanel có thể gọi khi khách click "Thuê xe"
    public void showRentalRequest(XeMayDTO selectedBike) {
        rentalRequestPanel.setVehicleData(selectedBike);
        cardLayout.show(mainContentPanel, "Request");
    }

    // Hàm public để quay về lịch sử sau khi thuê thành công
    public void showHistory() {
        rentalHistoryPanel.loadData();
        cardLayout.show(mainContentPanel, "History");
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(SIDEBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(230, 45));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10");

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(SIDEBAR_HOVER); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(SIDEBAR_BG); }
        });
        return btn;
    }
}