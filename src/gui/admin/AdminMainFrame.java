package gui.admin;

import com.formdev.flatlaf.FlatClientProperties;
import gui.common.LoginFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminMainFrame extends JFrame {

    private JPanel sidebarPanel;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    // Màu sắc chủ đạo
    private final Color SIDEBAR_BG = new Color(33, 43, 54);
    private final Color SIDEBAR_HOVER = new Color(43, 55, 70);
    private final Color TEXT_COLOR = Color.WHITE;

    public AdminMainFrame() {
        setTitle("Quản Trị Viên - Hệ Thống Thuê Xe Máy");
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

        // Logo / Title
        JLabel lblTitle = new JLabel("ADMIN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_COLOR);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setBorder(new EmptyBorder(0, 0, 30, 0));

        // Menu (Đã bổ sung Thống Kê và đánh lại số thứ tự)
        JButton btnDashboard = createMenuButton("1. Thống kê");
        JButton btnBike = createMenuButton("2. Quản lý Xe");
        JButton btnCustomer = createMenuButton("3. Quản lý Khách hàng");
        JButton btnContract = createMenuButton("4. Quản lý Hợp đồng");
        JButton btnLogout = createMenuButton("Đăng xuất");
        btnLogout.setForeground(new Color(255, 82, 82));

        // Thêm vào Sidebar
        sidebarPanel.add(lblTitle);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(btnDashboard); // Thêm Thống kê vào Menu
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(btnBike);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(btnCustomer);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(btnContract);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(btnLogout);

        // MAIN CONTENT
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(245, 247, 250));

        // Khởi tạo các Panel con (Bổ sung StatisticPanel)
        StatisticPanel statisticPanel = new StatisticPanel();
        BikeManagementPanel bikeManagementPanel = new BikeManagementPanel();
        CustomerManagementPanel customerManagementPanel = new CustomerManagementPanel();
        ContractManagementPanel contractPanel = new ContractManagementPanel();

        // Add vào CardLayout với tên định danh
        mainContentPanel.add(statisticPanel, "Dashboard"); // Add Dashboard vào đầu tiên
        mainContentPanel.add(bikeManagementPanel, "Bike");
        mainContentPanel.add(customerManagementPanel, "Customer");
        mainContentPanel.add(contractPanel, "Contract");

        // ACTION CHO MENU CHUYỂN TRANG
        btnDashboard.addActionListener(e -> cardLayout.show(mainContentPanel, "Dashboard"));

        btnBike.addActionListener(e -> cardLayout.show(mainContentPanel, "Bike"));

        btnCustomer.addActionListener(e -> {
            customerManagementPanel.loadDataFromDB(); // Kích hoạt tải lại dữ liệu mới nhất
            cardLayout.show(mainContentPanel, "Customer");
        });

        btnContract.addActionListener(e -> cardLayout.show(mainContentPanel, "Contract"));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginFrame().setVisible(true);
            }
        });

        // Add vào Frame chính
        add(sidebarPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        // Hiển thị Dashboard làm màn hình mặc định khi vừa mở form
        cardLayout.show(mainContentPanel, "Dashboard");
    }

    // Tiện ích tạo nút Menu
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
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(SIDEBAR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(SIDEBAR_BG);
            }
        });
        return btn;
    }
}