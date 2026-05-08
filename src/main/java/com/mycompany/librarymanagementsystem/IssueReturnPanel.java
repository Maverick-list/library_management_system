package library.gui;

import library.db.BookDAO;
import library.db.IssuedBookDAO;
import library.db.MemberDAO;
import library.model.Book;
import library.model.IssuedBook;
import library.model.Member;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Panel Issue & Return Buku
 * Fitur: Issue buku ke member, Return buku, Lihat status aktif
 */
public class IssueReturnPanel extends JPanel {

    private IssuedBookDAO issuedDAO = new IssuedBookDAO();
    private BookDAO bookDAO = new BookDAO();
    private MemberDAO memberDAO = new MemberDAO();

    // Form components
    private JComboBox<Book>   cmbBook;      // 1. JComboBox
    private JComboBox<Member> cmbMember;    // 2. JComboBox
    private JSpinner           spnDueDate;  // 3. JSpinner
    private JTextField         txtSearchIssue; // 4. JTextField
    private JLabel             lblFine;     // 5. JLabel (info)
    private JLabel             lblAvailable;// 6. JLabel
    private JLabel             lblStatus;   // 7. JLabel

    private JTable table;
    private DefaultTableModel tableModel;

    private int selectedIssueId  = -1;
    private int selectedBookIdForReturn = -1;

    public IssueReturnPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 252));

        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);

        issuedDAO.updateOverdueStatus();
        loadActiveIssues();
        loadCombos();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(320, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel formTitle = new JLabel("📋 Issue a Book");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        formTitle.setForeground(new Color(231, 76, 60));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(formTitle, gbc);
        gbc.gridwidth = 1;

        // Book ComboBox - Komponen 1
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Select Book *:"), gbc);
        cmbBook = new JComboBox<>();
        cmbBook.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbBook.setPreferredSize(new Dimension(200, 28));
        cmbBook.addActionListener(e -> updateAvailableLabel());
        gbc.gridx = 1;
        panel.add(cmbBook, gbc);

        // Available stock label - Komponen 6
        gbc.gridx = 1; gbc.gridy = 2;
        lblAvailable = new JLabel("Available: -");
        lblAvailable.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAvailable.setForeground(new Color(39, 174, 96));
        panel.add(lblAvailable, gbc);

        // Member ComboBox - Komponen 2
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Select Member *:"), gbc);
        cmbMember = new JComboBox<>();
        cmbMember.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridx = 1;
        panel.add(cmbMember, gbc);

        // Due Date - Komponen 3: JSpinner
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Due Date *:"), gbc);
        SpinnerDateModel dm = new SpinnerDateModel();
        spnDueDate = new JSpinner(dm);
        JSpinner.DateEditor de = new JSpinner.DateEditor(spnDueDate, "yyyy-MM-dd");
        spnDueDate.setEditor(de);
        // Default due date: 7 days from now
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        spnDueDate.setValue(cal.getTime());
        gbc.gridx = 1;
        panel.add(spnDueDate, gbc);

        // Issue button
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JButton btnIssue = createBtn("📤 Issue Book", new Color(231, 76, 60));
        btnIssue.setPreferredSize(new Dimension(200, 38));
        btnIssue.addActionListener(e -> issueBook());
        panel.add(btnIssue, gbc);

        // Separator
        gbc.gridy = 6; gbc.insets = new Insets(15, 4, 5, 4);
        panel.add(new JSeparator(), gbc);

        // Return section
        JLabel returnTitle = new JLabel("📥 Return Book");
        returnTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        returnTitle.setForeground(new Color(39, 174, 96));
        gbc.gridy = 7; gbc.insets = new Insets(5, 4, 5, 4);
        panel.add(returnTitle, gbc);

        // Fine info - Komponen 5: JLabel
        gbc.gridy = 8;
        lblFine = new JLabel("Select a row to return");
        lblFine.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFine.setForeground(new Color(100, 100, 100));
        panel.add(lblFine, gbc);

        JButton btnReturn = createBtn("📥 Return Selected Book", new Color(39, 174, 96));
        btnReturn.addActionListener(e -> returnBook());
        gbc.gridy = 9;
        panel.add(btnReturn, gbc);

        JButton btnRefresh = createBtn("🔄 Refresh", new Color(127, 140, 141));
        btnRefresh.addActionListener(e -> {
            issuedDAO.updateOverdueStatus();
            loadActiveIssues();
            loadCombos();
        });
        gbc.gridy = 10;
        panel.add(btnRefresh, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        JLabel tableTitle = new JLabel("  📋 Active Book Issues (Unreturned)");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"Issue ID", "Book Title", "Member Name", "Date Issued", "Due Date", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(231, 76, 60));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 220, 215));
        table.setGridColor(new Color(220, 220, 220));

        // Warnai baris berdasarkan status
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, value, sel, foc, row, col);
                if (!sel) {
                    String status = tableModel.getValueAt(row, 5).toString();
                    if ("Overdue".equals(status)) setBackground(new Color(255, 200, 200));
                    else setBackground(row % 2 == 0 ? Color.WHITE : new Color(255, 248, 245));
                }
                return this;
            }
        });

        int[] widths = {70, 220, 160, 110, 110, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                selectedIssueId = (int) tableModel.getValueAt(row, 0);
                // Find book id from active issues list
                updateFineLabel(row);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        lblStatus = new JLabel("Ready");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(new Color(100, 100, 100));
        p.add(lblStatus);
        return p;
    }

    // ===== OPERATIONS =====

    private void issueBook() {
        Book selectedBook = (Book) cmbBook.getSelectedItem();
        Member selectedMember = (Member) cmbMember.getSelectedItem();

        if (selectedBook == null || selectedMember == null || selectedBook.getBookId() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a book and member!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedBook.getAvailableStock() <= 0) {
            JOptionPane.showMessageDialog(this, "Sorry, this book is not available!", "Not Available", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Date dueDate = (Date) spnDueDate.getValue();
        if (dueDate.before(new Date())) {
            JOptionPane.showMessageDialog(this, "Due date cannot be in the past!", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Issue \"" + selectedBook.getTitle() + "\" to " + selectedMember.getFullName() + "?\nDue: " + new SimpleDateFormat("yyyy-MM-dd").format(dueDate),
            "Confirm Issue", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (issuedDAO.issueBook(selectedBook.getBookId(), selectedMember.getMemberId(), dueDate)) {
                showStatus("✅ Book issued successfully!", new Color(39, 174, 96));
                loadActiveIssues();
                loadCombos();
            } else {
                showStatus("❌ Failed to issue book. Book may be out of stock.", Color.RED);
            }
        }
    }

    private void returnBook() {
        if (selectedIssueId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an issue record to return!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int row = table.getSelectedRow();
        String bookTitle = tableModel.getValueAt(row, 1).toString();
        String memberName = tableModel.getValueAt(row, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Return \"" + bookTitle + "\" from " + memberName + "?",
            "Confirm Return", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Get book_id from active issues
            List<IssuedBook> issues = issuedDAO.getActiveIssues();
            int bookId = -1;
            for (IssuedBook ib : issues) {
                if (ib.getIssueId() == selectedIssueId) {
                    bookId = ib.getBookId();
                    break;
                }
            }
            if (issuedDAO.returnBook(selectedIssueId, bookId)) {
                showStatus("✅ Book returned successfully!", new Color(39, 174, 96));
                loadActiveIssues();
                loadCombos();
                selectedIssueId = -1;
                lblFine.setText("Select a row to return");
            } else {
                showStatus("❌ Failed to process return.", Color.RED);
            }
        }
    }

    private void loadActiveIssues() {
        tableModel.setRowCount(0);
        List<IssuedBook> issues = issuedDAO.getActiveIssues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (IssuedBook ib : issues) {
            tableModel.addRow(new Object[]{
                ib.getIssueId(), ib.getBookTitle(), ib.getMemberName(),
                ib.getDateIssued() != null ? sdf.format(ib.getDateIssued()) : "",
                ib.getDueDate() != null ? sdf.format(ib.getDueDate()) : "",
                ib.getStatus()
            });
        }
        showStatus("📋 " + tableModel.getRowCount() + " active issue(s).", new Color(100, 100, 100));
    }

    private void loadCombos() {
        // Load books
        cmbBook.removeAllItems();
        Book blankBook = new Book(); blankBook.setTitle("-- Select Book --"); blankBook.setBookId(0);
        cmbBook.addItem(blankBook);
        for (Book b : bookDAO.getAllBooks()) {
            if (b.getAvailableStock() > 0) cmbBook.addItem(b);
        }

        // Load active members
        cmbMember.removeAllItems();
        Member blankMember = new Member(); blankMember.setFullName("-- Select Member --");
        cmbMember.addItem(blankMember);
        for (Member m : memberDAO.getActiveMembers()) {
            cmbMember.addItem(m);
        }
    }

    private void updateAvailableLabel() {
        Book b = (Book) cmbBook.getSelectedItem();
        if (b != null && b.getBookId() != 0) {
            lblAvailable.setText("Available: " + b.getAvailableStock() + " / " + b.getTotalStock());
            lblAvailable.setForeground(b.getAvailableStock() > 0 ? new Color(39, 174, 96) : Color.RED);
        } else {
            lblAvailable.setText("Available: -");
        }
    }

    private void updateFineLabel(int row) {
        // Hitung estimasi fine jika sudah overdue
        String status = tableModel.getValueAt(row, 5).toString();
        if ("Overdue".equals(status)) {
            lblFine.setText("⚠️ OVERDUE - Fine will be calculated (Rp 1.000/day)");
            lblFine.setForeground(Color.RED);
        } else {
            lblFine.setText("✅ Not overdue - No fine");
            lblFine.setForeground(new Color(39, 174, 96));
        }
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
