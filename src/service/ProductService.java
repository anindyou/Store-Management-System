package service;

import model.Product;
import repository.ProductRepository;
import java.sql.SQLException;
import java.util.List;
import model.Customer;
import model.CartItem;
import config.DatabaseConnection; // Sesuaikan dengan nama kelas koneksi DB-mu
import java.sql.Connection;

public class ProductService {
    private ProductRepository productRepository = new ProductRepository();

    public void registerNewProduct(Product product) {
        Product existingProduct = productRepository.getProductByName(product.getName());
        if (existingProduct != null) {
            System.out.println("Service: Gagal menambahkan! Produk dengan nama '"
                    + product.getName() + "' sudah terdaftar dengan ID " + existingProduct.getId() + ".");
        } else {
            productRepository.addProduct(product);
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.getAllProducts();

        if (products.isEmpty()) {
            System.out.println("Service: Belum ada produk yang terdaftar di database.");
        }

        return products;
    }

    public void purchaseProduct(int productId, int quantity){
        Product product = productRepository.getProductById(productId);

        if (product == null) {
            System.out.println("Service: Gagal membeli produk! Produk dengan ID " + productId + " tidak ditemukan.");
            return;
        }

        if (product.getStock() < quantity) {
            System.out.println("Service: Gagal membeli produk! Stok '" + product.getName() + "' tidak mencukupi. (Sisa stok: " + product.getStock() + ")");
            return;
        }

        // Update stock
        int newStock = product.getStock() - quantity;
        product.setStock(newStock);

        productRepository.updateProduct(product);

        double totalCost = product.getPrice() * quantity;
        System.out.println("Service: Pembelian sukses!");
        System.out.println("         " + product.getName() + " x" + quantity + " | Total: Rp" + String.format("%.0f", totalCost));

        // Cek jika setelah dibeli stoknya langsung menyentuh batas threshold
        if (newStock <= product.getStockThreshold()) {
            System.out.println("Service: [PERINGATAN] Stok " + product.getName() + " kritis! Segera reorder.");
        }
    }

    public void reorderProduct(int productId, int quantity) {
        Product product = productRepository.getProductById(productId);

        if (product == null) {
            System.out.println("Service: Gagal menambahkan stok produk! Produk dengan ID " + productId + " tidak ditemukan.");
            return;
        }

        int newStock = product.getStock() + quantity;
        product.setStock(newStock);

        productRepository.updateProduct(product);
        System.out.println("Service: Stok produk dengan ID " + productId + " berhasil ditambahkan sebanyak " + quantity);
    }

    /**
     *
     * @param customer
     * @return boolean
     *
     * Memastikan semua item di keranjang tersedia (jumlah permintaan <= stok)
     * Apabila terdapat satu saja item yang kurang, maka transaksi digagalkan.
     */
    public boolean checkoutCart(Customer customer) {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();

            //Mematikan Auto-Commit. Mulai transaksi manual
            conn.setAutoCommit(false);

            for (CartItem item : customer.getCart()) {
                int productId = item.getProduct().getId();
                int quantityRequested = item.getQuantity();

                Product liveProduct = productRepository.getProductById(productId);

                // Cek apakah stok di database mencukupi untuk item ini
                if (liveProduct == null || liveProduct.getStock() < quantityRequested) {
                    System.out.println("Service: [ERROR] Gagal checkout! Stok '"
                            + (liveProduct != null ? liveProduct.getName() : "ID " + productId)
                            + "' tidak mencukupi atau barang tidak ditemukan.");

                    System.out.println("Service: Membatalkan seluruh transaksi.");
                    conn.rollback();
                    return false;
                }

                // Jika stok aman, hitung sisa stok baru dan update di database
                int updatedStock = liveProduct.getStock() - quantityRequested;
                liveProduct.setStock(updatedStock);

                productRepository.updateProduct(liveProduct);

                System.out.println("Service: Memproses '" + liveProduct.getName() + "' x" + quantityRequested);
            }

            // Jika item tidak bermasalah, push ke database
            conn.commit();
            System.out.println("Service: Transaksi berhasil disimpan permanen ke database!");
            return true;

        } catch (SQLException e) {
            System.out.println("Service: Terjadi kesalahan database internal!");
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}