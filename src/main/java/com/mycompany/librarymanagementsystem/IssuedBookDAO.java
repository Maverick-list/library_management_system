package library.db;

import library.model.IssuedBook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class IssuedBookDAO {

    // ===== ISSUE A BOOK =====
    public boolean issueBook(int bookId, int memberId, Date dueDate) {
        Connection con = DatabaseConnection.getConnection();
        try {
            con.setAutoCommit(false);

            // 1. Insert issued record
            String insertSql = "INSERT INTO issued_books (book_id, member_id, date_issued, due_date, status) VALUES (?, ?, CURDATE(), ?, 'Issued')";
            PreparedStatement ps = con.prepareStatement(insertSql);
            ps.setInt(1, bookId);
            ps.setInt(2, memberId);
            ps.setDate(3, new java.sql.Date(dueDate.getTime()));
            ps.executeUpdate();

            // 2. Decrease available stock
            String updateSql = "UPDATE books SET available_stock = available_stock - 1 WHERE book_id=? AND available_stock > 0";
            PreparedStatement ps2 = con.prepareStatement(updateSql);
            ps2.setInt(1, bookId);
            int updated = ps2.executeUpdate();

            if (updated == 0) {
                con.rollback();
                return false;
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ===== RETURN A BOOK =====
    public boolean returnBook(int issueId, int bookId) {
        Connection con = DatabaseConnection.getConnection();
        try {
            con.setAutoCommit(false);

            // Calculate fine (Rp 1000 per day overdue)
            String fineSql = "SELECT DATEDIFF(CURDATE(), due_date) AS days_overdue FROM issued_books WHERE issue_id=?";
            PreparedStatement ps0 = con.prepareStatement(fineSql);
            ps0.setInt(1, issueId);
            ResultSet rs = ps0.executeQuery();
            double fine = 0;
            if (rs.next()) {
                int daysOverdue = rs.getInt("days_overdue");
                if (daysOverdue > 0) fine = daysOverdue * 1000.0;
            }

            // 1. Update issued_books
            String updateIssue = "UPDATE issued_books SET date_returned=CURDATE(), fine=?, status='Returned' WHERE issue_id=?";
            PreparedStatement ps = con.prepareStatement(updateIssue);
            ps.setDouble(1, fine);
            ps.setInt(2, issueId);
            ps.executeUpdate();

            // 2. Increase available stock
            String updateBook = "UPDATE books SET available_stock = available_stock + 1 WHERE book_id=?";
            PreparedStatement ps2 = con.prepareStatement(updateBook);
            ps2.setInt(1, bookId);
            ps2.executeUpdate();

            con.commit();
            return true;
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // ===== GET ALL ISSUED BOOKS =====
    public List<IssuedBook> getAllIssuedBooks() {
        List<IssuedBook> list = new ArrayList<>();
        String sql = "SELECT ib.*, b.title AS book_title, m.full_name AS member_name " +
                     "FROM issued_books ib " +
                     "JOIN books b ON ib.book_id = b.book_id " +
                     "JOIN member m ON ib.member_id = m.member_id " +
                     "ORDER BY ib.issue_id DESC";
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== GET ACTIVE ISSUES (not returned) =====
    public List<IssuedBook> getActiveIssues() {
        List<IssuedBook> list = new ArrayList<>();
        String sql = "SELECT ib.*, b.title AS book_title, m.full_name AS member_name " +
                     "FROM issued_books ib " +
                     "JOIN books b ON ib.book_id = b.book_id " +
                     "JOIN member m ON ib.member_id = m.member_id " +
                     "WHERE ib.status='Issued' OR ib.status='Overdue' " +
                     "ORDER BY ib.due_date ASC";
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== UPDATE OVERDUE STATUS =====
    public void updateOverdueStatus() {
        String sql = "UPDATE issued_books SET status='Overdue' WHERE due_date < CURDATE() AND status='Issued'";
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private IssuedBook mapResultSet(ResultSet rs) throws SQLException {
        IssuedBook ib = new IssuedBook();
        ib.setIssueId(rs.getInt("issue_id"));
        ib.setBookId(rs.getInt("book_id"));
        ib.setMemberId(rs.getInt("member_id"));
        ib.setDateIssued(rs.getDate("date_issued"));
        ib.setDueDate(rs.getDate("due_date"));
        ib.setDateReturned(rs.getDate("date_returned"));
        ib.setFine(rs.getDouble("fine"));
        ib.setStatus(rs.getString("status"));
        ib.setBookTitle(rs.getString("book_title"));
        ib.setMemberName(rs.getString("member_name"));
        return ib;
    }
}
