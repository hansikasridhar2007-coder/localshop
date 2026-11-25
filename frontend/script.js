
// Java Backend Address
const API = "http://localhost:8080";

// Products and Cart
let products = [];
let cart = [];

// Send Data to Java
async function postData(endpoint, data) {
    const response = await fetch(API + endpoint, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams(data)
    });

    const result = await response.json();

    if (!response.ok) {
        throw new Error(result.message || "Request failed");
    }

    return result;
}

// Get Products from Java
async function loadProducts() {
    const response = await fetch(API + "/products");

    if (!response.ok) {
        throw new Error("Could not load products");
    }

    products = await response.json();
}

// Page Navigation
async function showPage(page) {
    document.getElementById("homePage").classList.add("hidden");
    document.getElementById("customerPage").classList.add("hidden");
    document.getElementById("ownerPage").classList.add("hidden");

    document.getElementById(page + "Page").classList.remove("hidden");

    if (page === "customer") {
        await loadProducts();
        renderProducts();
        renderCart();
    }

    if (page === "owner") {
        await loadProducts();
        renderOwner();
    }

    window.scrollTo(0, 0);
}

function goHome() {
    showPage("home");
}

// Escape Product Names
function escapeHTML(value) {
    return String(value).replace(/[&<>"']/g, function (char) {
        return {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        }[char];
    });
}

// Display Customer Products
function renderProducts() {
    let search = document.getElementById("searchInput")
        .value.toLowerCase();

    let list = document.getElementById("productList");
    let filtered = products.filter(p =>
        p.name.toLowerCase().includes(search)
    );

    list.innerHTML = "";

    if (filtered.length === 0) {
        list.innerHTML = "<p>No products found!</p>";
        return;
    }

    filtered.forEach(p => {
        list.innerHTML += `
            <div class="product-card">
                <div class="product-icon">📦</div>
                <h3>${escapeHTML(p.name)}</h3>
                <p>Product ID: ${p.id}</p>
                <p>Price: Rs. ${p.price.toFixed(2)}</p>
                <p>Available: ${p.quantity}</p>
                <button onclick="addToCart(${p.id})"
                    ${p.quantity === 0 ? "disabled" : ""}>
                    ${p.quantity === 0 ? "Out of Stock" : "Add to Cart"}
                </button>
            </div>
        `;
    });
}

// Add to Cart
function addToCart(id) {
    let p = products.find(item => item.id === id);

    if (!p || p.quantity <= 0) {
        alert("Product is not available!");
        return;
    }

    let quantity = Number(prompt("Enter quantity:"));

    if (!Number.isInteger(quantity) || quantity <= 0) {
        alert("Enter a valid quantity!");
        return;
    }

    let existing = cart.find(item => item.id === id);
    let alreadyAdded = existing ? existing.quantity : 0;

    if (alreadyAdded + quantity > p.quantity) {
        alert("Sorry! Not enough stock.");
        return;
    }

    if (existing) {
        existing.quantity += quantity;
    } else {
        cart.push({ id: id, quantity: quantity });
    }

    alert("Product added to cart!");
    renderCart();
}

// Display Cart
function renderCart() {
    let list = document.getElementById("cartList");
    let total = 0;

    list.innerHTML = "";

    if (cart.length === 0) {
        list.innerHTML = "<p>Your cart is empty!</p>";
    }

    cart.forEach(item => {
        let p = products.find(product => product.id === item.id);

        if (!p) return;

        let subtotal = p.price * item.quantity;
        total += subtotal;

        list.innerHTML += `
            <div class="cart-item">
                <div>
                    <strong>${escapeHTML(p.name)}</strong>
                    <p>Quantity: ${item.quantity}</p>
                    <p>Subtotal: Rs. ${subtotal.toFixed(2)}</p>
                </div>
                <button onclick="removeFromCart(${p.id})">
                    Remove
                </button>
            </div>
        `;
    });

    document.getElementById("cartTotal").innerText =
        "Total: Rs. " + total.toFixed(2);
}

// Remove from Cart
function removeFromCart(id) {
    cart = cart.filter(item => item.id !== id);
    renderCart();
}

// Checkout
async function checkout() {
    if (cart.length === 0) {
        alert("Your cart is empty!");
        return;
    }

    try {
        let items = cart.map(item =>
            item.id + ":" + item.quantity
        ).join(",");

        let result = await postData("/checkout", {
            items: items
        });

        alert(result.bill);

        cart = [];

        await loadProducts();
        renderProducts();
        renderCart();

    } catch (error) {
        alert(error.message);
        await loadProducts();
        renderProducts();
        renderCart();
    }
}

// Add Product - Owner
document.getElementById("productForm").addEventListener(
    "submit", async function (event) {

        event.preventDefault();

        let name = document.getElementById("productName").value.trim();
        let price = Number(document.getElementById("productPrice").value);
        let quantity = Number(
            document.getElementById("productQuantity").value
        );

        if (!name || !Number.isFinite(price) || price <= 0 ||
            !Number.isInteger(quantity) || quantity < 0) {
            alert("Enter valid product details!");
            return;
        }

        try {
            let result = await postData("/products", {
                name: name,
                price: price,
                quantity: quantity
            });

            alert(result.message);

            document.getElementById("productForm").reset();

            await loadProducts();
            renderOwner();

        } catch (error) {
            alert(error.message);
        }
    }
);

// Owner Dashboard
function renderOwner() {
    let list = document.getElementById("ownerProductList");

    list.innerHTML = "";

    document.getElementById("totalProducts").innerText =
        products.length;

    let stock = products.reduce((sum, p) => sum + p.quantity, 0);

    document.getElementById("totalStock").innerText = stock;

    if (products.length === 0) {
        list.innerHTML =
            '<tr><td colspan="5">No products available!</td></tr>';
        return;
    }

    products.forEach(p => {
        list.innerHTML += `
            <tr>
                <td>${p.id}</td>
                <td>${escapeHTML(p.name)}</td>
                <td>Rs. ${p.price.toFixed(2)}</td>
                <td>${p.quantity}</td>
                <td>
                    <div class="action-buttons">
                        <button class="edit-btn"
                            onclick="updateProduct(${p.id})">
                            Edit
                        </button>

                        <button class="delete-btn"
                            onclick="deleteProduct(${p.id})">
                            Delete
                        </button>
                    </div>
                </td>
            </tr>
        `;
    });
}

// Update Product
async function updateProduct(id) {
    let p = products.find(item => item.id === id);

    if (!p) return;

    let price = prompt("Enter new price:", p.price);

    if (price === null) return;

    let quantity = prompt("Enter new quantity:", p.quantity);

    if (quantity === null) return;

    price = Number(price);
    quantity = Number(quantity);

    if (!Number.isFinite(price) || price <= 0 ||
        !Number.isInteger(quantity) || quantity < 0) {
        alert("Invalid price or quantity!");
        return;
    }

    try {
        let result = await postData("/update", {
            id: id,
            price: price,
            quantity: quantity
        });

        alert(result.message);

        await loadProducts();
        renderOwner();

    } catch (error) {
        alert(error.message);
    }
}

// Delete Product
async function deleteProduct(id) {
    if (!confirm("Are you sure you want to delete this product?")) {
        return;
    }

    try {
        let result = await postData("/delete", {
            id: id
        });

        cart = cart.filter(item => item.id !== id);

        alert(result.message);

        await loadProducts();
        renderOwner();
        renderCart();

    } catch (error) {
        alert(error.message);
    }
}
