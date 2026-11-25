
import java.util.ArrayList;

public class Store {

    private ArrayList<product> products = new ArrayList<>();

    // Add Product
    public void addproduct(product p) {
        products.add(p);
        System.out.println("Product added successfully!");
    }

    // View Products
    public void showproducts() {
        if (products.isEmpty()) {
            System.out.println("No products available!");
            return;
        }

        for (product p : products) {
            p.display();
        }
    }

    // Search Product
    public product searchproduct(int id) {
        for (product p : products) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    // Delete Product
    public void deleteproduct(int id) {
        product p = searchproduct(id);

        if (p == null) {
            System.out.println("Product not found!");
            return;
        }

        products.remove(p);
        System.out.println("Product deleted successfully!");
    }

    // Get All Products
    public ArrayList<product> getProducts() {
        return products;
    }
}
