package library.gui;

import library.db.BookDAO;
import library.model.Book;
import library.model.Category;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Panel Manajemen Buku
 * Komponen digunakan: JTextField, JComboBox, JSpinner, JTextArea,
 *                     JTable, JButton, JLabel, JScrollPane, JPanel
 */
public class BookPanel extends JPanel {

    private BookDAO bookDAO = new BookDAO();

    // ===== FORM COMPONENTS (7+ komponen) =====
    private JTextField txtTitle;          // 1. JTextField
    private JTextField txtAuthor;         // 2. JTextField
    private JTextField txtIsbn;           // 3. JTextField
    private JTextField txtPublisher;      // 4. JTextField
    private JSpinner spnYear;             // 5. JSpinner
    private JSpinner spnStock;            // 6. JSpinner
    private JComboBox<Category> cmbCategory; // 7. JComboBox
    private JTextField txtSearch;         // 8. JTextField (search)
    private JLabel lblStatus;             // 9. JLabel (status)

    // ===== TABLE =====
    private JTable table;
    private DefaultTableModel tableModel;

    private int selectedBookId = -1;

    public BookPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 252));

        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);

        loadBooks();
        loadCategories();
    }

    // ===== FORM PANEL (KIRI) =====
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(310, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel formTitle = new JLabel("📖 Book Information");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        formTitle.setForeground(new Color(25, 55, 109));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(formTitle, gbc);
        gbc.gridwidth = 1;

        // Title - Komponen 1
        addFormRow(panel, gbc, "Title *:", txtTitle = new JTextField(20), 1);
        // Author - Komponen 2
        addFormRow(panel, gbc, "Author *:", txtAuthor = new JTextField(20), 2);
        // ISBN - Komponen 3
        addFormRow(panel, gbc, "ISBN *:", txtIsbn = new JTextField(20), 3);
        // Publisher - Komponen 4
        addFormRow(panel, gbc, "Publisher:", txtPublisher = new JTextField(20), 4);

        // Year - Komponen 5: JSpinner
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        panel.add(new JLabel("Year:"), gbc);
        spnYear = new JSpinner(new SpinnerNumberModel(2024, 1800, 2100, 1));
        spnYear.setEditor(new JSpinner.NumberEditor(spnYear, "#"));
        gbc.gridx = 1;
        panel.add(spnYear, gbc);

        // Stock - Komponen 6: JSpinner
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Stock *:"), gbc);
        spnStock = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        gbc.gridx = 1;
        panel.add(spnStock, gbc);

        // Category - Komponen 7: JComboBox
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Category:"), gbc);
        cmbCategory = new JComboBox<>();
        gbc.gridx = 1;
        panel.add(cmbCategory, gbc);

        // Separator
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.setOpaque(false);

        JButton btnAdd    = createBtn("➕ Add",    new Color(39, 174, 96));
        JButton btnUpdate = createBtn("✏️ Update",  new Color(52, 152, 219));
        JButton btnDelete = createBtn("🗑️ Delete",  new Color(231, 76, 60));
        JButton btnClear  = createBtn("🔄 Clear",   new Color(127, 140, 141));

        btnAdd.addActionListener(e -> addBook());
        btnUpdate.addActionListener(e -> updateBook());
        btnDelete.addActionListener(e -> deleteBook());
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        gbc.gridy = 9;
        panel.add(btnPanel, gbc);

        return panel;
    }

    // ===== TABLE PANEL (KANAN) =====
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        // Search bar - Komponen 8
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setOpaque(false);
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.putClientProperty("JTextField.placeholderText", "Search by title, author, or ISBN...");
        JButton btnSearch = createBtn("🔍 Search", new Color(25, 55, 109));
        JButton btnRefresh = createBtn("🔄 Refresh", new Color(127, 140, 141));
        btnSearch.addActionListener(e -> searchBooks());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadBooks(); });
        txtSearch.addActionListener(e -> searchBooks());

        JPanel searchRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchRight.setOpaque(false);
        searchRight.add(btnSearch);
        searchRight.add(btnRefresh);

        searchPanel.add(new JLabel("  📖 Book List"), BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(searchRight, BorderLayout.EAST);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Title", "Author", "ISBN", "Publisher", "Year", "Total", "Available", "Category"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(25, 55, 109));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(210, 230, 255));
        table.setGridColor(new Color(220, 220, 220));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Color rows berdasarkan available stock
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focused, row, col);
                if (!selected) {
                    int available = Integer.parseInt(tableModel.getValueAt(row, 7).toString());
                    if (available == 0) setBackground(new Color(255, 220, 220));
                    else setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 255));
                }
                return this;
            }
        });

        // Set kolom width
        int[] widths = {40, 180, 130, 110, 120, 55, 55, 70, 100};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromTable();
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        lblStatus = new JLabel("Ready");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setForeground(new Color(100, 100, 100));
        panel.add(lblStatus);
        return panel;
    }

    // ===== CRUD OPERATIONS =====

    private void addBook() {
        if (!validateForm()) return;
        Book book = getBookFromForm();
        if (bookDAO.addBook(book)) {
            showStatus("✅ Book added successfully!", new Color(39, 174, 96));
            loadBooks();
            clearForm();
        } else {
            showStatus("❌ Failed to add book. Check ISBN duplicate.", Color.RED);
        }
    }

    private void updateBook() {
        if (selectedBookId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateForm()) return;
        Book book = getBookFromForm();
        book.setBookId(selectedBookId);
        if (bookDAO.updateBook(book)) {
            showStatus("✅ Book updated successfully!", new Color(52, 152, 219));
            loadBooks();
            clearForm();
        } else {
            showStatus("❌ Failed to update book.", Color.RED);
        }
    }

    private void deleteBook() {
        if (selectedBookId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this book? This action cannot be undone.", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (bookDAO.deleteBook(selectedBookId)) {
                showStatus("✅ Book deleted.", new Color(231, 76, 60));
                loadBooks();
                clearForm();
            } else {
                showStatus("❌ Cannot delete. Book may have active loans.", Color.RED);
            }
        }
    }

    private void searchBooks() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadBooks(); return; }
        List<Book> books = bookDAO.searchBooks(keyword);
        populateTable(books);
        showStatus("🔍 Found " + books.size() + " result(s) for: " + keyword, new Color(25, 55, 109));
    }

    // ===== HELPERS =====

    private void loadBooks() {
        populateTable(bookDAO.getAllBooks());
        showStatus("📖 " + tableModel.getRowCount() + " books loaded.", new Color(100, 100, 100));
    }

    private void populateTable(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                b.getBookId(), b.getTitle(), b.getAuthor(), b.getIsbn(),
                b.getPublisher(), b.getYearPublished(),
                b.getTotalStock(), b.getAvailableStock(),
                b.getCategoryName() != null ? b.getCategoryName() : "-"
            });
        }
    }

    private void loadCategories() {
        cmbCategory.removeAllItems();
        Category blank = new Category(0, "-- Select Category --", "");
        cmbCategory.addItem(blank);
        for (Category c : bookDAO.getAllCategories()) {
            cmbCategory.addItem(c);
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        selectedBookId = (int) tableModel.getValueAt(row, 0);
        txtTitle.setText((String) tableModel.getValueAt(row, 1));
        txtAuthor.setText((String) tableModel.getValueAt(row, 2));
        txtIsbn.setText((String) tableModel.getValueAt(row, 3));
        txtPublisher.setText((String) tableModel.getValueAt(row, 4));
        spnYear.setValue(tableModel.getValueAt(row, 5));
        spnStock.setValue(tableModel.getValueAt(row, 6));
    }

    private Book getBookFromForm() {
        Book book = new Book();
        book.setTitle(txtTitle.getText().trim());
        book.setAuthor(txtAuthor.getText().trim());
        book.setIsbn(txtIsbn.getText().trim());
        book.setPublisher(txtPublisher.getText().trim());
        book.setYearPublished((int) spnYear.getValue());
        book.setTotalStock((int) spnStock.getValue());
        Category cat = (Category) cmbCategory.getSelectedItem();
        book.setCategoryId(cat != null ? cat.getCategoryId() : 0);
        return book;
    }

    private boolean validateForm() {
        if (txtTitle.getText().trim().isEmpty() || txtAuthor.getText().trim().isEmpty() || txtIsbn.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title, Author, and ISBN are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtTitle.setText(""); txtAuthor.setText(""); txtIsbn.setText("");
        txtPublisher.setText(""); spnYear.setValue(2024); spnStock.setValue(1);
        cmbCategory.setSelectedIndex(0);
        selectedBookId = -1;
        table.clearSelection();
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
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
