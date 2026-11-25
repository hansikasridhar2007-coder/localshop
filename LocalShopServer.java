
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LocalShopServer {

    private static Store store = new Store();

    public static void main(String[] args) throws IOException {

        // Sample Products
        store.addproduct(new product(1, "Notebook", 50, 10));
        store.addproduct(new product(2, "Pen", 10, 20));
        store.addproduct(new product(3, "School Bag", 500, 5));

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0
        );

        server.createContext("/", LocalShopServer::handleRequest);

        server.start();

        System.out.println("================================");
        System.out.println("LOCALSHOP JAVA SERVER STARTED");
        System.out.println("Server: http://localhost:8080");
        System.out.println("================================");
    }

    // Handle Requests
    private static void handleRequest(HttpExchange exchange)
            throws IOException {

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Methods", "GET, POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
            "Access-Control-Allow-Headers", "Content-Type"
        );

        if (exchange.getRequestMethod().equals("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if (path.equals("/products") && method.equals("GET")) {
                getProducts(exchange);
            }

            else if (path.equals("/products") && method.equals("POST")) {
                addProduct(exchange);
            }

            else if (path.equals("/update") && method.equals("POST")) {
                updateProduct(exchange);
            }

            else if (path.equals("/delete") && method.equals("POST")) {
                deleteProduct(exchange);
            }

            else if (path.equals("/checkout") && method.equals("POST")) {
                checkout(exchange);
            }

            else {
                sendJson(exchange, 404,
                    "{\"success\":false,\"message\":\"Invalid request\"}");
            }

        } catch (Exception e) {
            sendJson(exchange, 400,
                "{\"success\":false,\"message\":\"Invalid input\"}");
        }
    }

    // Read Form Data
    private static Map<String, String> readData(HttpExchange exchange)
            throws IOException {

        String body = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        Map<String, String> data = new HashMap<>();

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            if (parts.length == 2) {
                data.put(
                    URLDecoder.decode(parts[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                );
            }
        }

        return data;
    }

    // Get Products
    private static void getProducts(HttpExchange exchange)
            throws IOException {

        StringBuilder json = new StringBuilder("[");

        ArrayList<product> list = store.getProducts();

        for (int i = 0; i < list.size(); i++) {

            product p = list.get(i);

            json.append("{")
                .append("\"id\":").append(p.getId()).append(",")
                .append("\"name\":\"").append(jsonEscape(p.getName())).append("\",")
                .append("\"price\":").append(p.getPrice()).append(",")
                .append("\"quantity\":").append(p.getQuantity())
                .append("}");

            if (i < list.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        sendJson(exchange, 200, json.toString());
    }

    // Add Product
    private static void addProduct(HttpExchange exchange)
            throws IOException {

        Map<String, String> data = readData(exchange);

        String name = data.get("name");
        double price = Double.parseDouble(data.get("price"));
        int quantity = Integer.parseInt(data.get("quantity"));

        if (name == null || name.trim().isEmpty()
                || !Double.isFinite(price) || price <= 0
                || quantity < 0) {
            sendJson(exchange, 400,
                "{\"success\":false,\"message\":\"Invalid product details\"}");
            return;
        }

        int id = 1;

        for (product p : store.getProducts()) {
            id = Math.max(id, p.getId() + 1);
        }

        store.addproduct(new product(id, name, price, quantity));

        sendJson(exchange, 200,
            "{\"success\":true,\"message\":\"Product added successfully\"}");
    }

    // Update Product
    private static void updateProduct(HttpExchange exchange)
            throws IOException {

        Map<String, String> data = readData(exchange);

        int id = Integer.parseInt(data.get("id"));
        double price = Double.parseDouble(data.get("price"));
        int quantity = Integer.parseInt(data.get("quantity"));

        product p = store.searchproduct(id);

        if (p == null || !Double.isFinite(price)
                || price <= 0 || quantity < 0) {
            sendJson(exchange, 400,
                "{\"success\":false,\"message\":\"Invalid product details\"}");
            return;
        }

        p.setPrice(price);
        p.setQuantity(quantity);

        sendJson(exchange, 200,
            "{\"success\":true,\"message\":\"Product updated successfully\"}");
    }

    // Delete Product
    private static void deleteProduct(HttpExchange exchange)
            throws IOException {

        Map<String, String> data = readData(exchange);

        int id = Integer.parseInt(data.get("id"));

        if (store.searchproduct(id) == null) {
            sendJson(exchange, 404,
                "{\"success\":false,\"message\":\"Product not found\"}");
            return;
        }

        store.deleteproduct(id);

        sendJson(exchange, 200,
            "{\"success\":true,\"message\":\"Product deleted successfully\"}");
    }

    // Checkout
    private static void checkout(HttpExchange exchange)
            throws IOException {

        Map<String, String> data = readData(exchange);

        String items = data.get("items");

        if (items == null || items.isEmpty()) {
            sendJson(exchange, 400,
                "{\"success\":false,\"message\":\"Cart is empty\"}");
            return;
        }

        Map<Integer, Integer> quantities = new LinkedHashMap<>();

        for (String item : items.split(",")) {

            String[] parts = item.split(":");

            if (parts.length != 2) {
                sendJson(exchange, 400,
                    "{\"success\":false,\"message\":\"Invalid cart data\"}");
                return;
            }

            int id = Integer.parseInt(parts[0]);
            int quantity = Integer.parseInt(parts[1]);

            if (quantity <= 0 || quantities.containsKey(id)) {
                sendJson(exchange, 400,
                    "{\"success\":false,\"message\":\"Invalid cart quantity\"}");
                return;
            }

            quantities.put(id, quantity);
        }

        double total = 0;
        StringBuilder bill = new StringBuilder("LOCALSHOP BILL\n\n");

        // Check stock and calculate bill
        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {

            product p = store.searchproduct(entry.getKey());
            int quantity = entry.getValue();

            if (p == null || quantity > p.getQuantity()) {
                sendJson(exchange, 400,
                    "{\"success\":false,\"message\":\"Insufficient stock\"}");
                return;
            }

            double subtotal = p.getPrice() * quantity;
            total += subtotal;

            bill.append(p.getName())
                .append(" x ").append(quantity)
                .append(" = Rs. ").append(subtotal)
                .append("\n");
        }

        // Reduce stock only after all items pass validation
        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
            product p = store.searchproduct(entry.getKey());
            p.setQuantity(p.getQuantity() - entry.getValue());
        }

        bill.append("\nGrand Total: Rs. ").append(total);
        bill.append("\nOrder placed successfully!");

        String response = "{"
            + "\"success\":true,"
            + "\"total\":" + total + ","
            + "\"bill\":\"" + jsonEscape(bill.toString()) + "\""
            + "}";

        sendJson(exchange, 200, response);
    }

    // Escape JSON Text
    private static String jsonEscape(String text) {

        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }

    // Send Response
    private static void sendJson(
            HttpExchange exchange, int status, String response)
            throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
