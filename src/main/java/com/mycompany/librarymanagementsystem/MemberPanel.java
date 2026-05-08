package library.gui;

import library.db.MemberDAO;
import library.model.Member;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

/**
 * Panel Manajemen Member (Anggota Perpustakaan)
 * Komponen: JTextField, JComboBox, JTextArea, JSpinner (Date), JTable, JButton, JCheckBox, JLabel
 */
public class MemberPanel extends JPanel {

    private MemberDAO memberDAO = new MemberDAO();

    // Form components
    private JTextField txtName;         // 1. JTextField
    private JTextField txtEmail;        // 2. JTextField
    private JTextField txtPhone;        // 3. JTextField
    private JTextArea  txtAddress;      // 4. JTextArea
    private JComboBox<String> cmbStatus;// 5. JComboBox
    private JTextField txtSearch;       // 6. JTextField
    private JSpinner   spnRegDate;      // 7. JSpinner (date)
    private JCheckBox  chkActiveOnly;   // 8. JCheckBox
    private JLabel     lblStatus;       // 9. JLabel

    private JTable table;
    private DefaultTableModel tableModel;
    private int selectedMemberId = -1;

    public MemberPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 252));

        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createStatusPanel(), BorderLayout.SOUTH);

        loadMembers();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel formTitle = new JLabel("👤 Member Information");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        formTitle.setForeground(new Color(155, 89, 182));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(formTitle, gbc);
        gbc.gridwidth = 1;

        // Name - Komponen 1
        addFormRow(panel, gbc, "Full Name *:", txtName = new JTextField(18), 1);
        // Email - Komponen 2
        addFormRow(panel, gbc, "Email *:", txtEmail = new JTextField(18), 2);
        // Phone - Komponen 3
        addFormRow(panel, gbc, "Phone:", txtPhone = new JTextField(18), 3);

        // Address - Komponen 4: JTextArea
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        panel.add(new JLabel("Address:"), gbc);
        txtAddress = new JTextArea(3, 18);
        txtAddress.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        gbc.gridx = 1;
        panel.add(new JScrollPane(txtAddress), gbc);

        // Register Date - Komponen 5: JSpinner (Date)
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Register Date:"), gbc);
        SpinnerDateModel dateModel = new SpinnerDateModel();
        spnRegDate = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnRegDate, "yyyy-MM-dd");
        spnRegDate.setEditor(dateEditor);
        spnRegDate.setValue(new Date());
        gbc.gridx = 1;
        panel.add(spnRegDate, gbc);

        // Status - Komponen 6: JComboBox
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Status:"), gbc);
        cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
        gbc.gridx = 1;
        panel.add(cmbStatus, gbc);

        // Separator
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        btnPanel.setOpaque(false);

        JButton btnAdd    = createBtn("➕ Register", new Color(155, 89, 182));
        JButton btnUpdate = createBtn("✏️ Update",   new Color(52, 152, 219));
        JButton btnDelete = createBtn("🗑️ Delete",   new Color(231, 76, 60));
        JButton btnClear  = createBtn("🔄 Clear",    new Color(127, 140, 141));

        btnAdd.addActionListener(e -> addMember());
        btnUpdate.addActionListener(e -> updateMember());
        btnDelete.addActionListener(e -> deleteMember());
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        gbc.gridy = 8;
        panel.add(btnPanel, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        // Search + filter
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        topPanel.setOpaque(false);

        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton btnSearch  = createBtn("🔍 Search",  new Color(155, 89, 182));
        JButton btnRefresh = createBtn("🔄 Refresh", new Color(127, 140, 141));

        // Komponen 7: JCheckBox
        chkActiveOnly = new JCheckBox("Active Only");
        chkActiveOnly.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkActiveOnly.setOpaque(false);
        chkActiveOnly.addActionListener(e -> loadMembers());

        btnSearch.addActionListener(e -> searchMembers());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadMembers(); });
        txtSearch.addActionListener(e -> searchMembers());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(chkActiveOnly);
        rightPanel.add(btnSearch);
        rightPanel.add(btnRefresh);

        topPanel.add(new JLabel("  👤 Member List"), BorderLayout.WEST);
        topPanel.add(txtSearch, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Full Name", "Email", "Phone", "Register Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(155, 89, 182));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(235, 210, 255));
        table.setGridColor(new Color(220, 220, 220));

        // Color Active/Inactive
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, value, selected, focused, row, col);
                if (!selected) {
                    String status = tableModel.getValueAt(row, 5).toString();
                    if ("Inactive".equals(status)) setBackground(new Color(255, 240, 240));
                    else setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 245, 255));
                }
                return this;
            }
        });

        int[] widths = {40, 160, 170, 110, 120, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) fillFormFromTable();
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

    // ===== CRUD =====

    private void addMember() {
        if (!validateForm()) return;
        Member m = getMemberFromForm();
        if (memberDAO.addMember(m)) {
            showStatus("✅ Member registered successfully!", new Color(155, 89, 182));
            loadMembers();
            clearForm();
        } else {
            showStatus("❌ Failed. Email might already exist.", Color.RED);
        }
    }

    private void updateMember() {
        if (selectedMemberId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateForm()) return;
        Member m = getMemberFromForm();
        m.setMemberId(selectedMemberId);
        if (memberDAO.updateMember(m)) {
            showStatus("✅ Member updated successfully!", new Color(52, 152, 219));
            loadMembers();
            clearForm();
        } else {
            showStatus("❌ Failed to update member.", Color.RED);
        }
    }

    private void deleteMember() {
        if (selectedMemberId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this member?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (memberDAO.deleteMember(selectedMemberId)) {
                showStatus("✅ Member deleted.", new Color(231, 76, 60));
                loadMembers();
                clearForm();
            } else {
                showStatus("❌ Cannot delete. Member may have active loans.", Color.RED);
            }
        }
    }

    private void searchMembers() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) { loadMembers(); return; }
        populateTable(memberDAO.searchMembers(keyword));
        showStatus("🔍 Search results for: " + keyword, new Color(155, 89, 182));
    }

    private void loadMembers() {
        List<Member> members = chkActiveOnly.isSelected()
            ? memberDAO.getActiveMembers()
            : memberDAO.getAllMembers();
        populateTable(members);
        showStatus("👤 " + tableModel.getRowCount() + " members loaded.", new Color(100, 100, 100));
    }

    private void populateTable(List<Member> members) {
        tableModel.setRowCount(0);
        for (Member m : members) {
            tableModel.addRow(new Object[]{
                m.getMemberId(), m.getFullName(), m.getEmail(), m.getPhone(),
                m.getRegisterDate(), m.getStatus()
            });
        }
    }

    private void fillFormFromTable() {
        int row = table.getSelectedRow();
        selectedMemberId = (int) tableModel.getValueAt(row, 0);
        txtName.setText((String) tableModel.getValueAt(row, 1));
        txtEmail.setText((String) tableModel.getValueAt(row, 2));
        txtPhone.setText((String) tableModel.getValueAt(row, 3));
        cmbStatus.setSelectedItem(tableModel.getValueAt(row, 5).toString());
    }

    private Member getMemberFromForm() {
        Member m = new Member();
        m.setFullName(txtName.getText().trim());
        m.setEmail(txtEmail.getText().trim());
        m.setPhone(txtPhone.getText().trim());
        m.setAddress(txtAddress.getText().trim());
        m.setRegisterDate((Date) spnRegDate.getValue());
        m.setStatus((String) cmbStatus.getSelectedItem());
        return m;
    }

    private boolean validateForm() {
        if (txtName.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full Name and Email are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!txtEmail.getText().contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtName.setText(""); txtEmail.setText(""); txtPhone.setText("");
        txtAddress.setText(""); cmbStatus.setSelectedIndex(0);
        spnRegDate.setValue(new Date());
        selectedMemberId = -1;
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
