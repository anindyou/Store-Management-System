package view;

import model.Product;
import service.ProductService;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final ProductService productService;
    private final Scanner scan;
    private final String username = "birumerah";
    private final String password = "19juta";

    public AdminMenu(ProductService productService, Scanner scan) {
        this.productService = productService;
        this.scan = scan;
    }

    public void doLoginAndShow() {
        System.out.println("\n--- LOGIN ADMIN ---");
        System.out.print("Username: ");
        String username = scan.next();
        System.out.print("Password: ");
        String password = scan.next();

        if (username.equals("birumerah") && password.equals("19juta")) {
            System.out.println("Login Berhasil! Selamat bekerja, Admin.");
            showMenu();
        } else {
            System.out.println("Username atau Password salah! Akses ditolak.");
        }
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n============ Manajemen Admin Koperasi ============");

            System.out.println("1. Lihat Stok Produk");
            System.out.println("2. Tambah Produk");
            System.out.println("3. Restock Produk");
            System.out.println("4. Kembali ke Menu Utama");
            System.out.print("Pilih opsi: ");

            int pilihan = scan.nextInt();
            switch (pilihan){
                case 1->{
                    printProductList();
                }
                case 2 -> {
                    addProduct();
                }
                case 3 ->{
                    reorderProduct();
                }
                case 4 -> {
                    return;
                }
                default ->{
                    System.out.println("Pilihan tidak valid!");

                }
            }
        }
    }

    private void reorderProduct() {
        System.out.println("\n--- REORDER PRODUK LAMA ---");
        System.out.print("\nMasukkan ID barang yang ingin di-restock: ");
        int id = scan.nextInt();
        System.out.print("Masukkan jumlah stock tambahan: ");
        int jumlah = scan.nextInt();

        productService.reorderProduct(id, jumlah);
    }

    private void addProduct(){
        System.out.println("\n--- TAMBAH PRODUK BARU ---");
        scan.nextLine();

        System.out.print("Masukkan Nama Produk: ");
        String name = scan.nextLine();
        System.out.print("Masukkan Harga Produk: ");
        double price = scan.nextDouble();
        System.out.print("Masukkan Stok Awal: ");
        int stock = scan.nextInt();
        System.out.print("Masukkan Batas Batas Minimum Stok (Threshold): ");
        int threshold = scan.nextInt();

        // Jalankan service dan tangkap status return boolean-nya
        Product product = new Product(0, name, price, stock, threshold);
        productService.registerNewProduct(product);
    }

    private void printProductList() {
        System.out.println("\n=============== Daftar Stock Barang ===============");
        List<Product> allProduct = productService.getAllProducts();
        for (Product p : allProduct) {
            String status = (p.getStock() <= p.getStockThreshold()) ? "REORDER" : "AMAN";
            System.out.printf("%-4d | %-15s | Rp%-8.0f | %-6d | %-6s\n",
                    p.getId(), p.getName(), p.getPrice(), p.getStock(), status);
        }
        System.out.println("--------------------------------------------------\n");
    }
}