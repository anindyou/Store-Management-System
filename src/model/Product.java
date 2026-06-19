package model;

public class Product {
    private int id;
    private String name;
    private double price;
    private int stock;
    private int stockThreshold;

    public Product(int id, String name, double price, int stock, int stockThreshold) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.stockThreshold = stockThreshold;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public int getStockThreshold() {
        return stockThreshold;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setStockThreshold(int stockThreshold) {
        this.stockThreshold = stockThreshold;
    }
}
