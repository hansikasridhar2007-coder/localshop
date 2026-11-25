
import java.util.Scanner;

public class LocalShop {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Store store = new Store();

        // Sample Products
        store.addproduct(new product(1, "Notebook", 50, 10));
        store.addproduct(new product(2, "Pen", 10, 20));
        store.addproduct(new product(3, "School Bag", 500, 5));

        // Welcome Page
        System.out.println("\n================================");
        System.out.println("       WELCOME TO LOCALSHOP");
        System.out.println("   Your Local Shopping Platform");
        System.out.println("================================");

        System.out.println("\n1. Shop Owner");
        System.out.println("2. Customer");
        System.out.println("3. Exit");

        System.out.print("\nSelect your role: ");
        int role = sc.nextInt();
        sc.nextLine();

        if (role == 3) {
            System.out.println("Thank you for visiting LocalShop!");
            sc.close();
            return;
        }

        System.out.print("Enter your name: ");
        String name = sc.nextLine();

        // Shop Owner Section
        if (role == 1) {

            ShopOwner owner = new ShopOwner(name, store);

            boolean running = true;

            while (running) {

                owner.dashboard();

                System.out.print("\nEnter choice: ");
                int choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        owner.addproduct(sc);
                        break;

                    case 2:
                        store.showproducts();
                        break;

                    case 3:
                        owner.updatePrice(sc);
                        break;

                    case 4:
                        owner.updateQuantity(sc);
                        break;

                    case 5:
                        owner.deleteproduct(sc);
                        break;

                    case 6:
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }
            }
        }

        // Customer Section
        else if (role == 2) {

            Customer customer = new Customer(name, store);

            boolean running = true;

            while (running) {

                customer.dashboard();

                System.out.print("\nEnter choice: ");
                int choice = sc.nextInt();

                switch (choice) {

                    case 1:
                        store.showproducts();
                        break;

                    case 2:
                        customer.searchproduct(sc);
                        break;

                    case 3:
                        customer.addtocart(sc);
                        break;

                    case 4:
                        customer.checkout();
                        break;

                    case 5:
                        running = false;
                        break;

                    case 6:
                        customer.viewcart();
                        break;

                    case 7:
                        customer.removefromcart(sc);
                        break;

                    default:
                        System.out.println("Invalid choice!");
                }
            }
        }

        else {
            System.out.println("Invalid role!");
        }

        System.out.println("\nThank you for using LocalShop!");

        sc.close();
    }
}
