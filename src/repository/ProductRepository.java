package repository;

import config.DatabaseConnection;
import model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    public void addProduct(Product product){
        String sql = "INSERT INTO products (name, price, stock, stock_threshold) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getStock());
            stmt.setInt(4, product.getStockThreshold());

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("Repository: Produk '" + product.getName() + "' berhasil disimpan ke database!");
            }

        } catch (SQLException e) {
            System.out.println("Repository: Gagal menambah produk!");
            e.printStackTrace();
        }
    }

    public Product getProductByName(String name){
        String sql = "SELECT * FROM products WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, name);

            // executeQuery() digunakan khusus untuk perintah SELECT yang mengembalikan data (ResultSet)
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Jika data ditemukan, bungkus data dari database ke dalam objek Product
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("stock"),
                            rs.getInt("stock_threshold")
                    );
                }
            }

        } catch (SQLException e){
            System.out.println("Repository: Produk tidak ditemukan!");
            e.printStackTrace();
        }
        return null; // Jika product tidak ditemukan
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) { // executeQuery() digunakan untuk SELECT

            // rs.next() akan terus bernilai true selama masih ada baris data berikutnya dari database
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("stock_threshold")
                );
                // Masukkan objek produk ke dalam list
                productList.add(product);
            }

        } catch (SQLException e) {
            System.out.println("Repository: Gagal mengambil semua produk!");
            e.printStackTrace();
        }
        return productList;
    }


    // Method untuk mencari produk berdasarkan ID
    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("stock"),
                            rs.getInt("stock_threshold")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Repository: Gagal mencari produk berdasarkan ID!");
            e.printStackTrace();
        }
        return null;
    }

    // Method untuk mengupdate data produk (terutama stok)
    public void updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, price = ?, stock = ?, stock_threshold = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getStock());
            stmt.setInt(4, product.getStockThreshold());
            stmt.setInt(5, product.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Repository: Gagal mengupdate produk!");
            e.printStackTrace();
        }
    }
}
