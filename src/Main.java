import service.ProductService;
import view.AdminMenu;
import view.CustomerMenu;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ProductService productService = new ProductService();

        AdminMenu adminMenu = new AdminMenu(productService, scan);
        CustomerMenu customerMenu = new CustomerMenu(productService, scan);

        while (true) {
            System.out.println("\n==============================================");
            System.out.println("  Selamat Datang di Koperasi Desa Biru Merah       ");
            System.out.println("==============================================");
            System.out.println("1. Masuk sebagai Customer (Guest Mode)");
            System.out.println("2. Masuk sebagai Admin");
            System.out.println("3. Keluar");
            System.out.print("Pilih akses Anda (1-3): ");

            int roleChoice = scan.nextInt();

            if (roleChoice == 3) {
                break;
            }

            switch (roleChoice) {
                case 1 -> {
                    customerMenu.showMenu();
                }
                case 2 -> {
                    adminMenu.doLoginAndShow();
                }
                default -> {
                    System.out.println("Pilihan Invalid. Silakan coba lagi.");
                }
            }
        }

        System.out.println("\nBerhasil keluar. Terima kasih!");
        scan.close();
    }
}