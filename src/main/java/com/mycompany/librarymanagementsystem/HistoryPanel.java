package library.gui;

import library.db.IssuedBookDAO;
import library.model.IssuedBook;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel History - Riwayat seluruh peminjaman buku
 */
public class HistoryPanel extends JPanel {

    private IssuedBookDAO issuedDAO = new IssuedBookDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilterStatus; // Komponen JComboBox

    public HistoryPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 252));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);

        loadHistory();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel title = new JLabel("📜 Borrowing History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(new Color(25, 55, 109));
        panel.add(title);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        panel.add(new JLabel("Search:"));
        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(txtSearch);

        panel.add(new JLabel("Status:"));
        cmbFilterStatus = new JComboBox<>(new String[]{"All", "Issued", "Returned", "Overdue"});
        cmbFilterStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(cmbFilterStatus);

        JButton btnFilter = createBtn("🔍 Filter", new Color(25, 55, 109));
        btnFilter.addActionListener(e -> filterHistory());
        panel.add(btnFilter);

        JButton btnRefresh = createBtn("🔄 Refresh", new Color(127, 140, 141));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); cmbFilterStatus.setSelectedIndex(0); loadHistory(); });
        panel.add(btnRefresh);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] cols = {"Issue ID", "Book Title", "Member Name", "Date Issued", "Due Date", "Date Returned", "Fine (Rp)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(25, 55, 109));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220));

        // Color berdasarkan status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                if (!sel) {
                    String status = tableModel.getValueAt(row, 7).toString();
                    switch (status) {
                        case "Returned": setBackground(new Color(230, 255, 235)); break;
                        case "Overdue":  setBackground(new Color(255, 220, 220)); break;
                        default:         setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 255));
                    }
                }
                // Align fine column to right
                if (col == 6) setHorizontalAlignment(RIGHT);
                else setHorizontalAlignment(LEFT);
                return this;
            }
        });

        int[] widths = {70, 200, 150, 110, 110, 120, 90, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        lblStatus = new JLabel("Ready");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(new Color(100, 100, 100));
        left.add(lblStatus);

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        legend.setOpaque(false);
        legend.add(createLegendItem("Returned", new Color(230, 255, 235)));
        legend.add(createLegendItem("Active", new Color(248, 250, 255)));
        legend.add(createLegendItem("Overdue", new Color(255, 220, 220)));

        p.add(left, BorderLayout.WEST);
        p.add(legend, BorderLayout.EAST);
        return p;
    }

    private JPanel createLegendItem(String label, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        item.setOpaque(false);
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        colorBox.setPreferredSize(new Dimension(15, 15));
        item.add(colorBox);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        item.add(lbl);
        return item;
    }

    private void loadHistory() {
        populateTable(issuedDAO.getAllIssuedBooks());
    }

    private void filterHistory() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        String statusFilter = (String) cmbFilterStatus.getSelectedItem();
        List<IssuedBook> all = issuedDAO.getAllIssuedBooks();

        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int count = 0;
        for (IssuedBook ib : all) {
            boolean matchKeyword = keyword.isEmpty()
                || ib.getBookTitle().toLowerCase().contains(keyword)
                || ib.getMemberName().toLowerCase().contains(keyword);
            boolean matchStatus = "All".equals(statusFilter) || statusFilter.equals(ib.getStatus());

            if (matchKeyword && matchStatus) {
                tableModel.addRow(new Object[]{
                    ib.getIssueId(),
                    ib.getBookTitle(),
                    ib.getMemberName(),
                    ib.getDateIssued() != null ? sdf.format(ib.getDateIssued()) : "",
                    ib.getDueDate() != null ? sdf.format(ib.getDueDate()) : "",
                    ib.getDateReturned() != null ? sdf.format(ib.getDateReturned()) : "-",
                    ib.getFine() > 0 ? String.format("%,.0f", ib.getFine()) : "0",
                    ib.getStatus()
                });
                count++;
            }
        }
        showStatus("📜 " + count + " record(s) found.", new Color(25, 55, 109));
    }

    private void populateTable(List<IssuedBook> list) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (IssuedBook ib : list) {
            tableModel.addRow(new Object[]{
                ib.getIssueId(),
                ib.getBookTitle(),
                ib.getMemberName(),
                ib.getDateIssued() != null ? sdf.format(ib.getDateIssued()) : "",
                ib.getDueDate() != null ? sdf.format(ib.getDueDate()) : "",
                ib.getDateReturned() != null ? sdf.format(ib.getDateReturned()) : "-",
                ib.getFine() > 0 ? String.format("%,.0f", ib.getFine()) : "0",
                ib.getStatus()
            });
        }
        showStatus("📜 " + tableModel.getRowCount() + " total records.", new Color(100, 100, 100));
    }

    private JButton createBtn(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showStatus(String msg, Color color) {
        lblStatus.setText(msg);
        lblStatus.setForeground(color);
    }
}
