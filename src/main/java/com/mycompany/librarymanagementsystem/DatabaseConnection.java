package library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Singleton class untuk koneksi database MySQL
 * Library Management System
 */
public class DatabaseConnection {

    // ======= SESUAIKAN KONFIGURASI INI =======
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "library_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // kosong jika tidak pakai password di Laragon
    // ==========================================

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
        "?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";

    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "MySQL JDBC Driver tidak ditemukan!\nPastikan mysql-connector-java sudah ditambahkan ke project.",
                "Driver Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal koneksi ke database!\n" + e.getMessage() +
                "\n\nPastikan:\n- Laragon/MySQL sudah berjalan\n- Database 'library_db' sudah dibuat\n- Username/Password sesuai",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
