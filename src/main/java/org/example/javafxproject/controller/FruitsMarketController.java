package org.example.javafxproject.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;

import java.net.URL;
import java.util.*;
import java.io.InputStream;

public class FruitsMarketController implements Initializable {

    @FXML private ComboBox<String> quantityCombo1;
    @FXML private ComboBox<String> quantityCombo2;
    @FXML private Button addToCartBtn;
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private GridPane productsGrid;
    @FXML private ImageView featuredImage;
    @FXML private Label featuredNameLabel;
    @FXML private Label featuredPriceLabel;

    // Product data structure
    private static class Product {
        String name;
        double price;
        String imagePath;

        Product(String name, double price, String imagePath) {
            this.name = name;
            this.price = price;
            this.imagePath = imagePath;
        }
    }

    private List<Product> allProducts;
    private List<Product> cart = new ArrayList<>();
    private Product currentFeaturedProduct;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initializing Fruits Market Controller...");
        initializeProducts();
        setupQuantityComboBoxes();
        displayProducts(allProducts);

        // Set initial featured product
        if (!allProducts.isEmpty()) {
            setFeaturedProduct(allProducts.get(0));
        }

        // Check all images on startup
        checkAllImages();
    }

    private void initializeProducts() {
        allProducts = Arrays.asList(
                new Product("Banana", 2.99, "/image/banana.webp"),
                new Product("Coconut", 3.99, "/image/coconut.webp"),
                new Product("Peach", 1.50, "/image/peach.webp"),
                new Product("Grapes", 0.99, "/image/grapes.webp"),
                new Product("Watermelon", 4.99, "/image/watermelon.webp"),
                new Product("Orange", 2.99, "/image/org.webp"),
                new Product("Strawberry", 0.99, "/image/strawberry.webp"),
                new Product("Mango", 0.99, "/image/mango.webp"),
                new Product("Cherry", 0.99, "/image/chery.webp")
        );
        System.out.println("Initialized " + allProducts.size() + " products");
    }

    private void setupQuantityComboBoxes() {
        // Setup quantity options
        List<String> quantities = Arrays.asList("1", "2", "3", "4", "5");
        quantityCombo1.getItems().addAll(quantities);
        quantityCombo2.getItems().addAll(quantities);
        quantityCombo1.setValue("1");
        quantityCombo2.setValue("1");

        System.out.println("Quantity combo boxes initialized");
    }

    private void displayProducts(List<Product> products) {
        productsGrid.getChildren().clear();

        int row = 0;
        int col = 0;

        System.out.println("Displaying " + products.size() + " products");

        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productsGrid.add(productCard, col, row);

            col++;
            if (col >= 3) { // 3 columns
                col = 0;
                row++;
            }
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 15; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(150);
        card.setPrefHeight(180);

        // Product name and price
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(10);

        Label nameLabel = new Label(product.name);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label priceLabel = new Label(String.format("$%.2f", product.price));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        priceLabel.setStyle("-fx-text-fill: #666666;");

        headerBox.getChildren().addAll(nameLabel, priceLabel);

        // Product image
        ImageView imageView = new ImageView();
        imageView.setFitHeight(80);
        imageView.setFitWidth(80);
        imageView.setPreserveRatio(true);

        // Try to load image, use placeholder if not found
        loadProductImage(imageView, product);

        // Add click handler for product selection
        card.setOnMouseClicked(event -> selectProduct(product));
        card.setStyle(card.getStyle() + "-fx-cursor: hand;");

        // Add hover effect
        card.setOnMouseEntered(event -> {
            card.setStyle(card.getStyle().replace("rgba(0,0,0,0.1)", "rgba(0,0,0,0.2)"));
        });

        card.setOnMouseExited(event -> {
            card.setStyle(card.getStyle().replace("rgba(0,0,0,0.2)", "rgba(0,0,0,0.1)"));
        });

        card.getChildren().addAll(headerBox, imageView);

        return card;
    }

    private void loadProductImage(ImageView imageView, Product product) {
        try {
            InputStream imageStream = getClass().getResourceAsStream(product.imagePath);
            if (imageStream != null) {
                Image image = new Image(imageStream);
                if (!image.isError()) {
                    imageView.setImage(image);
                    System.out.println("✓ Successfully loaded image: " + product.imagePath);
                } else {
                    System.out.println("✗ Image error for: " + product.imagePath);
                    createPlaceholderImage(imageView, product.name);
                }
                imageStream.close();
            } else {
                System.out.println("✗ Image stream is null for: " + product.imagePath);
                createPlaceholderImage(imageView, product.name);
            }
        } catch (Exception e) {
            System.out.println("✗ Error loading image " + product.imagePath + ": " + e.getMessage());
            createPlaceholderImage(imageView, product.name);
        }
    }

    private void createPlaceholderImage(ImageView imageView, String productName) {
        // Create a colorful placeholder based on product name
        int colorHash = Math.abs(productName.hashCode()) % 6;
        String[] colors = {"#FFB3BA", "#FFDFBA", "#FFFFBA", "#BAFFC9", "#BAE1FF", "#D4BAFF"};
        imageView.setStyle("-fx-background-color: " + colors[colorHash] +
                "; -fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-border-radius: 5;");

        // Create a simple text-based image as fallback
        try {
            // This creates a simple colored rectangle
            imageView.setImage(null);
        } catch (Exception e) {
            System.out.println("Could not create placeholder for: " + productName);
        }
    }

    private void setFeaturedProduct(Product product) {
        currentFeaturedProduct = product;

        if (featuredNameLabel != null) {
            featuredNameLabel.setText(product.name);
        }

        if (featuredPriceLabel != null) {
            featuredPriceLabel.setText(String.format("$%.2f", product.price));
        }

        if (featuredImage != null) {
            loadProductImage(featuredImage, product);
        }

        System.out.println("Featured product set to: " + product.name);
    }

    private void selectProduct(Product product) {
        setFeaturedProduct(product);
        System.out.println("Selected product: " + product.name + " - $" + product.price);
    }

    @FXML
    private void addToCart(ActionEvent event) {
        if (currentFeaturedProduct == null) {
            showAlert("Error", "No product selected!");
            return;
        }

        String quantity1 = quantityCombo1.getValue();
        String quantity2 = quantityCombo2.getValue();

        int totalQuantity = 0;

        if (quantity1 != null && !quantity1.isEmpty()) {
            try {
                totalQuantity += Integer.parseInt(quantity1);
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity in combo1: " + quantity1);
            }
        }

        if (quantity2 != null && !quantity2.isEmpty() && !quantity2.equals("1")) {
            try {
                totalQuantity += Integer.parseInt(quantity2);
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity in combo2: " + quantity2);
            }
        }

        if (totalQuantity > 0) {
            addProductToCart(currentFeaturedProduct, totalQuantity);
            showAlert("Success", "Added " + currentFeaturedProduct.name + " x" + totalQuantity + " to cart!\n" +
                    "Cart total: $" + String.format("%.2f", getCartTotal()));
        } else {
            showAlert("Error", "Please select a valid quantity!");
        }
    }

    @FXML
    private void searchProducts(ActionEvent event) {
        String searchTerm = searchField.getText().toLowerCase().trim();

        if (searchTerm.isEmpty()) {
            displayProducts(allProducts);
            System.out.println("Showing all products");
        } else {
            List<Product> filteredProducts = new ArrayList<>();
            for (Product product : allProducts) {
                if (product.name.toLowerCase().contains(searchTerm)) {
                    filteredProducts.add(product);
                }
            }
            displayProducts(filteredProducts);
            System.out.println("Found " + filteredProducts.size() + " products matching: " + searchTerm);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void checkAllImages() {
        System.out.println("\n=== Checking Image Availability ===");
        for (Product product : allProducts) {
            checkImageExists(product.imagePath);
        }

        // Check icon images
        checkImageExists("/image/app.webp");
        checkImageExists("/image/chery.webp");
        checkImageExists("/image/org.webp");
        System.out.println("=== Image Check Complete ===\n");
    }

    private void checkImageExists(String imagePath) {
        try {
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                System.out.println("✓ Found: " + imagePath);
                stream.close();
            } else {
                System.out.println("✗ Missing: " + imagePath);
            }
        } catch (Exception e) {
            System.out.println("✗ Error checking: " + imagePath + " - " + e.getMessage());
        }
    }

    // Cart management methods
    public void addProductToCart(Product product, int quantity) {
        for (int i = 0; i < quantity; i++) {
            cart.add(product);
        }
        System.out.println("Cart size: " + cart.size() + " items");
    }

    public double getCartTotal() {
        return cart.stream().mapToDouble(p -> p.price).sum();
    }

    public void clearCart() {
        cart.clear();
        System.out.println("Cart cleared");
    }

    public List<Product> getCartItems() {
        return new ArrayList<>(cart);
    }

    public int getCartItemCount() {
        return cart.size();
    }

    // Get current featured product
    public Product getCurrentFeaturedProduct() {
        return currentFeaturedProduct;
    }

    // Get all products
    public List<Product> getAllProducts() {
        return new ArrayList<>(allProducts);
    }
}