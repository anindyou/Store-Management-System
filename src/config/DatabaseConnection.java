package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Alamat database MySQL kita di localhost dengan nama database toko_db
    private static final String URL = "jdbc:mysql://localhost:3306/toko_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Default XAMPP biasanya kosong

    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Membuka koneksi menggunakan DriverManager bawaan Java
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Gagal terhubung ke database!");
            e.printStackTrace();
        }
        return connection;
    }
}