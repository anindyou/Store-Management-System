package view;

import model.CartItem;
import model.Customer;
import model.Product;
import service.ProductService;
import java.util.List;
import java.util.Scanner;

public class CustomerMenu {
    private final ProductService productService;
    private final Scanner scan;

    public CustomerMenu(ProductService productService, Scanner scan) {
        this.productService = productService;
        this.scan = scan;
    }

    public void showMenu() {
        scan.nextLine();
        System.out.println("\n===== Menu Pembelian di Koperasi Desa Biru Merah =====");

        System.out.print("Masukkan Nama Anda untuk mulai belanja: ");
        String customerName = scan.nextLine();

        Customer customer = new Customer(customerName);

        while (true) {
            System.out.println("\n--- Halo " + customer.getName() + ", Selamat Berbelanja! ---");
            System.out.println("1. Lihat Daftar Barang & Tambah ke Keranjang");
            System.out.println("2. Lihat Isi Keranjang Saat Ini");
            System.out.println("3. Checkout & Bayar (Akumulasi Harga)");
            System.out.println("4. Batal & Kembali ke Menu Utama");
            System.out.print("Pilih opsi: ");

            int pilihan = scan.nextInt();

            switch (pilihan) {
                case 1 -> addToCart(customer);
                case 2 -> viewCart(customer);
                case 3 -> {
                    checkOut(customer);
                    return;
                }
                case 4 -> {
                    System.out.println("Belanja dibatalkan. Keranjang dikosongkan.");
                    return;
                }
                default -> System.out.println("Pilihan tidak valid!");
            }
        }
    }

    private void printProductList() {
        System.out.println("\n=============== Daftar Stock Barang ===============");
        List<Product> semuaProduk = productService.getAllProducts();
        for (Product p : semuaProduk) {
            String status = (p.getStock() <= p.getStockThreshold()) ? "REORDER" : "AMAN";
            System.out.printf("%-4d | %-15s | Rp%-8.0f | %-6d | %-6s\n",
                    p.getId(), p.getName(), p.getPrice(), p.getStock(), status);
        }
        System.out.println("--------------------------------------------------\n");
    }

    private void addToCart(Customer customer) {
        printProductList();
        System.out.print("Masukkan ID barang yang ingin dibeli: ");
        int inputId = scan.nextInt();

        // Cari dulu apakah produknya eksis di DB (kita bisa manfaatkan list yang ada)
        Product productChoice = null;
        for (Product p : productService.getAllProducts()) {
            if (p.getId() == inputId) {
                productChoice = p;
                break;
            }
        }

        if (productChoice == null) {
            System.out.println("Barang dengan ID tersebut tidak ditemukan!");
            return;
        }

        System.out.print("Masukkan jumlah yang ingin dibeli: ");
        int jumlah = scan.nextInt();

        if (jumlah <= 0) {
            System.out.println("Jumlah pembelian harus lebih dari 0!");
            return;
        }

        if (productChoice.getStock() < jumlah) {
            System.out.println("Gagal! Stok tidak mencukupi. Sisa stok toko: " + productChoice.getStock());
            return;
        }

        customer.addToCart(productChoice, jumlah);
        System.out.println("✓ " + productChoice.getName() + " (x" + jumlah + ") berhasil dimasukkan ke keranjang.");
    }

    private void viewCart(Customer customer) {
        System.out.println("\n================ ISI KERANJANG " + customer.getName().toUpperCase() + " ================");
        List<CartItem> cart = customer.getCart();

        if (cart.isEmpty()) {
            System.out.println("[ Keranjang Belanja Masih Kosong ]");
            return;
        }

        for (CartItem item : cart) {
            System.out.printf("- %-15s x%-3d | Subtotal: Rp%.0f\n",
                    item.getProduct().getName(), item.getQuantity(), item.getSubtotal());
        }
        System.out.println("-------------------------------------------------");
        System.out.printf("TOTAL AKUMULASI HARGA: Rp%.0f\n", customer.getTotalCartPrice());
        System.out.println("=================================================");
    }

    private void checkOut(Customer customer) {
        if (customer.getCart().isEmpty()) {
            System.out.println("Keranjangmu kosong, tidak ada yang bisa di-checkout!");
            return;
        }

        System.out.println("\n--- Memproses Checkout... ---");

        System.out.println("\n================= STRUK BELANJA =================");
        System.out.println("Pelanggan: " + customer.getName());
        System.out.println("-------------------------------------------------");
        for (CartItem item : customer.getCart()) {
            System.out.printf("%-15s x%-3d : Rp%.0f\n",
                    item.getProduct().getName(), item.getQuantity(), item.getSubtotal());

            productService.purchaseProduct(item.getProduct().getId(), item.getQuantity());
        }
        System.out.println("-------------------------------------------------");
        System.out.printf("TOTAL HARGA: Rp%.2f\n", customer.getTotalCartPrice());
        System.out.println("=================================================");
        System.out.println("Terima kasih sudah berbelanja!");
    }
}