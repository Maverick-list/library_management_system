package library.gui;

import library.db.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main Dashboard - Tampilan utama aplikasi Library Management System
 */
public class MainDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    public MainDashboard() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("📚 Library Management System - Semester 20252");
        setSize(1100, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));

        // ===== MENU BAR =====
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenuItem miExit = new JMenuItem("Exit");
        miExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DatabaseConnection.closeConnection();
                System.exit(0);
            }
        });
        menuFile.add(miExit);
        menuBar.add(menuFile);

        JMenu menuHelp = new JMenu("Help");
        JMenuItem miAbout = new JMenuItem("About");
        miAbout.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Library Management System\nSemester 20252\nObject-Oriented & Visual Programming\n\nDeveloped with Java Swing + MySQL",
            "About", JOptionPane.INFORMATION_MESSAGE));
        menuHelp.add(miAbout);
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        // ===== HEADER PANEL =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(25, 55, 109));
        headerPanel.setPreferredSize(new Dimension(0, 70));

        JLabel titleLabel = new JLabel("  📚  Library Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel subLabel = new JLabel("Object-Oriented & Visual Programming | Semester 20252  ");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(new Color(180, 210, 255));
        headerPanel.add(subLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ===== TABBED PANE =====
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Tab 1: Dashboard
        tabbedPane.addTab("🏠 Dashboard", createDashboardPanel());
        // Tab 2: Books
        tabbedPane.addTab("📖 Books", new BookPanel());
        // Tab 3: Members
        tabbedPane.addTab("👤 Members", new MemberPanel());
        // Tab 4: Issue / Return
        tabbedPane.addTab("📋 Issue & Return", new IssueReturnPanel());
        // Tab 5: History
        tabbedPane.addTab("📜 History", new HistoryPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // ===== STATUS BAR =====
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        JLabel statusLabel = new JLabel("  ✅ Connected to database: library_db");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 252));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Welcome label
        JLabel welcome = new JLabel("Welcome to Library Management System", SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcome.setForeground(new Color(25, 55, 109));
        panel.add(welcome, BorderLayout.NORTH);

        // Stats cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        cardsPanel.add(createStatCard("📖 Total Books", getStatCount("SELECT COUNT(*) FROM books"), new Color(52, 152, 219)));
        cardsPanel.add(createStatCard("✅ Available", getStatCount("SELECT SUM(available_stock) FROM books"), new Color(39, 174, 96)));
        cardsPanel.add(createStatCard("👤 Members", getStatCount("SELECT COUNT(*) FROM member"), new Color(155, 89, 182)));
        cardsPanel.add(createStatCard("📋 Active Loans", getStatCount("SELECT COUNT(*) FROM issued_books WHERE status='Issued' OR status='Overdue'"), new Color(231, 76, 60)));
        panel.add(cardsPanel, BorderLayout.CENTER);

        // Quick actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Quick Actions"));

        String[] btnTexts = {"📖 Manage Books", "👤 Manage Members", "📋 Issue Book", "📜 View History"};
        int[] tabIndices = {1, 2, 3, 4};
        for (int i = 0; i < btnTexts.length; i++) {
            final int idx = tabIndices[i];
            JButton btn = new JButton(btnTexts[i]);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setPreferredSize(new Dimension(160, 45));
            btn.setBackground(new Color(25, 55, 109));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> tabbedPane.setSelectedIndex(idx));
            actionsPanel.add(btn);
        }
        panel.add(actionsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLbl.setForeground(Color.WHITE);

        JLabel valueLbl = new JLabel(value, SwingConstants.CENTER);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLbl.setForeground(Color.WHITE);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }

    private String getStatCount(String sql) {
        try (java.sql.Connection con = DatabaseConnection.getConnection();
             java.sql.Statement stmt = con.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (Exception e) {
            return "N/A";
        }
        return "0";
    }

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MainDashboard().setVisible(true);
        });
    }
}
