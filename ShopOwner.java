
import java.util.Scanner;

public class ShopOwner extends user {

    private Store store;

    // Constructor
    public ShopOwner(String name, Store store) {
        super(name);
        this.store = store;
    }

    // Dashboard
    @Override
    public void dashboard() {
        System.out.println("\n===== SHOP OWNER DASHBOARD =====");
        System.out.println("Welcome " + name);
        System.out.println("1. Add Product");
        System.out.println("2. View Products");
        System.out.println("3. Update Price");
        System.out.println("4. Update Product Quantity");
        System.out.println("5. Delete Product");
        System.out.println("6. Exit");
    }

    // Add Product
    public void addproduct(Scanner sc) {
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        if (store.searchproduct(id) != null) {
            System.out.println("Product ID already exists!");
            return;
        }

        System.out.print("Enter Product Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Price: ");
        double price = sc.nextDouble();

        System.out.print("Enter Quantity: ");
        int quantity = sc.nextInt();

        if (price <= 0 || quantity < 0) {
            System.out.println("Invalid price or quantity!");
            return;
        }

        product p = new product(id, name, price, quantity);
        store.addproduct(p);
    }

    // Update Price
    public void updatePrice(Scanner sc) {
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();

        product p = store.searchproduct(id);

        if (p == null) {
            System.out.println("Product not found!");
            return;
        }

        System.out.print("Enter New Price: ");
        double price = sc.nextDouble();

        if (price > 0) {
            p.setPrice(price);
            System.out.println("Price updated successfully!");
        } else {
            System.out.println("Invalid price!");
        }
    }

    // Update Quantity
    public void updateQuantity(Scanner sc) {
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();

        product p = store.searchproduct(id);

        if (p == null) {
            System.out.println("Product not found!");
            return;
        }

        System.out.println("Current Quantity: " + p.getQuantity());

        System.out.print("Enter New Quantity: ");
        int quantity = sc.nextInt();

        if (quantity >= 0) {
            p.setQuantity(quantity);
            System.out.println("Quantity updated successfully!");
        } else {
            System.out.println("Invalid quantity!");
        }
    }

    // Delete Product
    public void deleteproduct(Scanner sc) {
        System.out.print("Enter Product ID to delete: ");
        int id = sc.nextInt();

        store.deleteproduct(id);
    }
}
