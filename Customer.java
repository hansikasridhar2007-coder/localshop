
import java.util.ArrayList;
import java.util.Scanner;

public class Customer extends user {

    private Store store;
    private ArrayList<product> cart = new ArrayList<>();

    // Constructor
    public Customer(String name, Store store) {
        super(name);
        this.store = store;
    }

    // Customer Dashboard
    @Override
    public void dashboard() {
        System.out.println("\n===== CUSTOMER DASHBOARD =====");
        System.out.println("Welcome " + name);
        System.out.println("1. View Products");
        System.out.println("2. Search Product");
        System.out.println("3. Add to Cart");
        System.out.println("4. Checkout");
        System.out.println("5. Exit");
        System.out.println("6. View Cart");
        System.out.println("7. Remove Product from Cart");
    }

    // Search Product
    public void searchproduct(Scanner sc) {
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();

        product p = store.searchproduct(id);

        if (p != null) {
            p.display();
        } else {
            System.out.println("Product not found!");
        }
    }

    // Add Product to Cart
    public void addtocart(Scanner sc) {
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();

        product p = store.searchproduct(id);

        if (p == null) {
            System.out.println("Product not found!");
            return;
        }

        System.out.println("Available Quantity: " + p.getQuantity());

        System.out.print("Enter Quantity: ");
        int quantity = sc.nextInt();

        if (quantity <= 0) {
            System.out.println("Invalid quantity!");
            return;
        }

        int alreadyAdded = 0;

        for (product item : cart) {
            if (item.getId() == id) {
                alreadyAdded++;
            }
        }

        if (alreadyAdded + quantity > p.getQuantity()) {
            System.out.println("Sorry! Not enough stock.");
            return;
        }

        for (int i = 0; i < quantity; i++) {
            cart.add(p);
        }

        System.out.println(quantity + " items added to cart!");
    }

    // View Cart
    public void viewcart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty!");
            return;
        }

        double total = 0;

        System.out.println("\n========== YOUR CART ==========");

        for (int i = 0; i < cart.size(); i++) {
            product p = cart.get(i);

            boolean counted = false;

            for (int j = 0; j < i; j++) {
                if (cart.get(j).getId() == p.getId()) {
                    counted = true;
                    break;
                }
            }

            if (!counted) {
                int quantity = 0;

                for (product item : cart) {
                    if (item.getId() == p.getId()) {
                        quantity++;
                    }
                }

                double subtotal = quantity * p.getPrice();
                total += subtotal;

                System.out.println("Product: " + p.getName());
                System.out.println("Quantity: " + quantity);
                System.out.println("Price: Rs." + p.getPrice());
                System.out.println("Subtotal: Rs." + subtotal);
                System.out.println("-------------------------------");
            }
        }

        System.out.println("Total Items: " + cart.size());
        System.out.println("Total Amount: Rs." + total);
    }

    // Remove Product from Cart
    public void removefromcart(Scanner sc) {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty!");
            return;
        }

        System.out.print("Enter Product ID to remove: ");
        int id = sc.nextInt();

        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getId() == id) {
                cart.remove(i);
                System.out.println("Product removed from cart!");
                return;
            }
        }

        System.out.println("Product not found in cart!");
    }

    // Checkout
    public void checkout() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty!");
            return;
        }

        double total = 0;

        System.out.println("\n========== YOUR BILL ==========");
        System.out.println("Customer: " + name);
        System.out.println("-------------------------------");

        for (int i = 0; i < cart.size(); i++) {
            product p = cart.get(i);

            boolean counted = false;

            for (int j = 0; j < i; j++) {
                if (cart.get(j).getId() == p.getId()) {
                    counted = true;
                    break;
                }
            }

            if (!counted) {
                int quantity = 0;

                for (product item : cart) {
                    if (item.getId() == p.getId()) {
                        quantity++;
                    }
                }

                double subtotal = quantity * p.getPrice();
                total += subtotal;

                System.out.println("Product: " + p.getName());
                System.out.println("Quantity: " + quantity);
                System.out.println("Price: Rs." + p.getPrice());
                System.out.println("Subtotal: Rs." + subtotal);
                System.out.println("-------------------------------");
            }
        }

        // Reduce stock
        for (product p : cart) {
            p.setQuantity(p.getQuantity() - 1);
        }

        System.out.println("Grand Total: Rs." + total);
        System.out.println("Order placed successfully!");
        System.out.println("===============================");

        cart.clear();
    }
}
