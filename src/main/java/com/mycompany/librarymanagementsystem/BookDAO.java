package library.db;

import library.model.Book;
import library.model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // ===== OVERLOADED: Default getAllBooks (Fixes Compilation Error) =====
    public List<Book> getAllBooks() {
        return getAllBooks("b.book_id", "ASC"); // Call the version with arguments
    }

    // ===== GET ALL BOOKS WITH DYNAMIC SORTING =====
    public List<Book> getAllBooks(String sortBy, String order) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT b.*, c.category_name FROM books b " +
                     "LEFT JOIN category c ON b.category_id = c.category_id " +
                     "ORDER BY " + sortBy + " " + order;
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

    // ===== ADD BOOK =====
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, publisher, year_published, total_stock, available_stock, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setString(4, book.getPublisher());
            ps.setInt(5, book.getYearPublished());
            ps.setInt(6, book.getTotalStock());
            ps.setInt(7, book.getTotalStock());
            ps.setInt(8, book.getCategoryId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== UPDATE BOOK =====
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title=?, author=?, isbn=?, publisher=?, year_published=?, total_stock=?, category_id=? WHERE book_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getIsbn());
            ps.setString(4, book.getPublisher());
            ps.setInt(5, book.getYearPublished());
            ps.setInt(6, book.getTotalStock());
            ps.setInt(7, book.getCategoryId());
            ps.setInt(8, book.getBookId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== DELETE BOOK =====
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== GET CATEGORIES =====
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM category ORDER BY category_name";
        try (Connection con = DatabaseConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("category_id"), rs.getString("category_name"), rs.getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== SEARCH BOOKS =====
    public List<Book> searchBooks(String keyword) {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT b.*, c.category_name FROM books b LEFT JOIN category c ON b.category_id = c.category_id WHERE b.title LIKE ? OR b.author LIKE ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Book mapResultSet(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setPublisher(rs.getString("publisher"));
        book.setYearPublished(rs.getInt("year_published"));
        book.setTotalStock(rs.getInt("total_stock"));
        book.setAvailableStock(rs.getInt("available_stock"));
        book.setCategoryId(rs.getInt("category_id"));
        book.setCategoryName(rs.getString("category_name"));
        return book;
    }
}