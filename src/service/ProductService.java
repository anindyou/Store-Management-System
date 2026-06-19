package service;

import model.Product;
import repository.ProductRepository;

import java.util.List;

public class ProductService {
    // Service membutuhkan Repository untuk mengakses database
    private ProductRepository productRepository = new ProductRepository();

    public void registerNewProduct(Product product) {
        Product existingProduct = productRepository.getProductByName(product.getName());
        // Jika product sudah ada, maka kita tidak bisa menambahkannya ke database
        if (existingProduct != null) {
            System.out.println("Service: Gagal menambahkan! Produk dengan nama '"
                    + product.getName() + "' sudah terdaftar dengan ID " + existingProduct.getId() + ".");
        } else {
            // 3. Jika belum ada, baru aman untuk disimpan ke database lewat repository
            productRepository.addProduct(product);
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.getAllProducts();

        // Logika bisnis tambahan: memberi tahu manajer jika data di toko masih kosong
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

        // Update stock di objek Java
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

}