package org.example.javafxproject.Product;

public class Fruit {
    private int productId; // Corresponds to product_id
    private String name;    // Corresponds to product_name
    private int quantity;   // Corresponds to quantity
    private double price;   // Corresponds to price
    private String imageUrl; // Corresponds to image_url

    // Constructor for loading from DB
    public Fruit(int productId, String name, int quantity, double price, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Constructor for new product (before it has a product_id from DB)
    public Fruit(String name, int quantity, double price, String imageUrl) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters
    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters (if needed for updating)
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Object getId() {
        return productId;
    }
}
