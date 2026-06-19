import config.DatabaseConnection;
import model.Product;
import service.ProductService;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        Scanner scan = new Scanner(System.in);

        Connection conn = DatabaseConnection.getConnection();

        ProductService productService = new ProductService();

        Product barang1 = new Product(0, "Kopi Susu", 15000, 50, 10);
        Product barang2 = new Product(0, "Coklat Bar", 10000, 100, 10);
        Product barang3 = new Product(0, "Roti Isi", 8000, 30, 5);

        productService.registerNewProduct(barang1);
        productService.registerNewProduct(barang2);
        productService.registerNewProduct(barang3);

        System.out.println("\nSelamat Datang di Koperasi Desa Biru Merah!");

        main.printProduct(productService);

        while (true){
            System.out.print("Masukkan ID barang yang ingin dibeli (Ketik -1 untuk membatalkan): ");
            int inputId = scan.nextInt();
            if (inputId == -1) {
                break;
            }
            System.out.print("Masukkan jumlah barang yang ingin dibeli (Ketik -1 untuk membatalkan): ");
            int inputAmount = scan.nextInt();
            if (inputAmount == -1) {
                break;
            }

            productService.purchaseProduct(inputId, inputAmount);
        }

        main.printProduct(productService);

        productService.reorderProduct(3, 40);

        main.printProduct(productService);

        System.out.println("Terima kasih sudah berbelanja di Koperasi Desa Biru Merah!");

    }

    public void printProduct(ProductService productService){
        System.out.println("\nDaftar stok barang");
        List<Product> semuaProduk = productService.getAllProducts();
        for (Product p : semuaProduk) {
            String status = (p.getStock() <= p.getStockThreshold()) ? "REORDER" : "AMAN";

            System.out.printf("%-4d | %-15s | Rp%-8.0f | %-6d | %-6s\n",
                    p.getId(), p.getName(), p.getPrice(), p.getStock(), status);
        }
        System.out.println();
    }
}