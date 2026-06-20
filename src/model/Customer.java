package model;

import java.util.ArrayList;
import java.util.List;

public class Customer {
    private final String name;
    private final List<CartItem> cart;

    public Customer(String name) {
        this.name = name;
        this.cart = new ArrayList<>();
    }

    public String getName() { return name; }
    public List<CartItem> getCart() { return cart; }

    public void addToCart(Product product, int quantity) {
        for (CartItem item : cart) {
            if (item.getProduct().getId() == product.getId()) {
                item.addQuantity(quantity);
                return;
            }
        }
        cart.add(new CartItem(product, quantity));
    }

    public double getTotalCartPrice() {
        double total = 0;
        for (CartItem item : cart) {
            total += item.getSubtotal();
        }
        return total;
    }

    public void clearCart() {
        cart.clear();
    }
}