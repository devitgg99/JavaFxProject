package org.example.javafxproject.Product;

public class Fruit {
    private final String name;
    private final double price;
    private final String imageUrl;

    public Fruit(String name, double price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imageUrl;
    }
}