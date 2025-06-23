package org.example.javafxproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.example.javafxproject.Product.Fruit;
import org.example.javafxproject.Product.ProductDAO;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Optional;

public class FruitsMarketController implements Initializable {

    @FXML private TextField quantityKgTextField;
    @FXML private TextField searchTextField;
    @FXML private FlowPane fruitGrid;

    @FXML private Label selectedFruitNameLabel;
    @FXML private Label selectedFruitPriceLabel;
    @FXML private ImageView selectedFruitImageView;

    @FXML private Label orderNumberLabel;

    // Cart Sidebar FXML Components
    @FXML private VBox cartSidebar;
    @FXML private Label cartItemCountLabel;
    @FXML private Button cartToggleBtn;
    @FXML private VBox cartItemsContainer;
    @FXML private VBox emptyCartMessage;
    @FXML private VBox orderSummary;
    @FXML private Button placeOrderBtn;
    @FXML private Label itemsTotalLabel;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;

    // Delivery Option Buttons
    @FXML private Button deliveryBtn;
    @FXML private Button dineInBtn;
    @FXML private Button takeAwayBtn;
    @FXML private Button closeCartBtn;

    private ProductDAO productDAO;
    private List<Fruit> allFruits;
    private Fruit currentSelectedFruit;

    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private DecimalFormat priceFormat = new DecimalFormat("$ #0.00");
    private static int orderCounter = 3241;
    private String selectedDeliveryOption = "Delivery"; // Default

    // Inner class to represent an item in the shopping cart
    public static class CartItem {
        private Fruit fruit;
        private int quantity;

        public CartItem(Fruit fruit, int quantity) {
            this.fruit = fruit;
            this.quantity = quantity;
        }

        public Fruit getFruit() { return fruit; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = Math.max(0, quantity); }
        public double getTotalPrice() { return fruit.getPrice() * quantity; }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        productDAO = new ProductDAO();
        loadFruitsFromDatabase();
        updateOrderNumber(); // Set initial order number

        // Set initial delivery option
        setInitialDeliverySelection();

        // Listen for changes in the cartItems list to update UI
        cartItems.addListener((javafx.collections.ListChangeListener<CartItem>) change -> {
            updateCartUI();
            updateCartTotals();
        });

        // Initialize quantity text field with 1
        if (quantityKgTextField != null) {
            quantityKgTextField.setText("1");
        } else {
            System.err.println("quantityKgTextField is null");
        }
    }

    private void loadFruitsFromDatabase() {
        try {
            allFruits = productDAO.getAllProducts();
            populateFruitGrid(allFruits);
            if (!allFruits.isEmpty()) {
                selectFruit(allFruits.get(0)); // Select the first fruit by default
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load products: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void populateFruitGrid(List<Fruit> fruitsToDisplay) {
        if (fruitGrid == null) {
            System.err.println("fruitGrid is null");
            return;
        }
        fruitGrid.getChildren().clear();
        for (Fruit fruit : fruitsToDisplay) {
            VBox fruitCard = createFruitCard(fruit);
            fruitGrid.getChildren().add(fruitCard);
            // Ensure the initial selection matches the first fruit loaded if any
            if (currentSelectedFruit == null && !fruitsToDisplay.isEmpty()) {
                selectFruit(fruitsToDisplay.get(0));
            }
        }
    }

    private VBox createFruitCard(Fruit fruit) {
        VBox card = new VBox();
        card.setPrefSize(150, 180);
        card.setStyle("-fx-background-color: WHITE; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");
        card.setAlignment(Pos.CENTER);
        card.setSpacing(5);

        Label nameLabel = new Label(fruit.getName());
        nameLabel.setFont(new Font("System Bold", 14));
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);

        Label priceLabel = new Label(priceFormat.format(fruit.getPrice()));
        priceLabel.setFont(new Font(12));

        ImageView imageView = new ImageView();
        try {
            String imageUrl = fruit.getImageUrl();
            Image image;

            if (imageUrl != null && imageUrl.startsWith("http")) {
                image = new Image(imageUrl, true);
            } else if (imageUrl != null && !imageUrl.isEmpty()) {
                image = new Image(getClass().getResourceAsStream("/images/" + imageUrl));
            } else {
                image = new Image(getClass().getResourceAsStream("/images/placeholder.png"));
            }

            imageView.setImage(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);

        } catch (Exception e) {
            System.err.println("Error loading image for " + fruit.getName() + ": " + fruit.getImageUrl());
            e.printStackTrace();
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            } catch (Exception ex) {
                System.err.println("Placeholder image missing. Check /src/main/resources/images/placeholder.png");
            }
        }

        card.getChildren().addAll(imageView, nameLabel, priceLabel);
        card.setOnMouseClicked(event -> selectFruit(fruit));
        return card;
    }

    private void selectFruit(Fruit fruit) {
        currentSelectedFruit = fruit;
        if (selectedFruitNameLabel != null) {
            selectedFruitNameLabel.setText(fruit.getName());
        } else {
            System.err.println("selectedFruitNameLabel is null");
        }
        if (selectedFruitPriceLabel != null) {
            selectedFruitPriceLabel.setText(priceFormat.format(fruit.getPrice()));
        } else {
            System.err.println("selectedFruitPriceLabel is null");
        }

        Image image = null;
        try {
            String imagePath = fruit.getImageUrl();
            if (imagePath != null && imagePath.startsWith("http")) {
                image = new Image(imagePath, true);
            } else if (imagePath != null && !imagePath.isEmpty()) {
                image = new Image(getClass().getResource("/images/" + imagePath).toExternalForm());
            } else {
                image = new Image(getClass().getResource("/images/placeholder.png").toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Error loading image for " + fruit.getName() + ": " + fruit.getImageUrl());
            try {
                image = new Image(getClass().getResource("/images/placeholder.png").toExternalForm());
            } catch (Exception ex) {
                System.err.println("Fallback placeholder not found. Check /src/main/resources/images/placeholder.png");
            }
        }

        if (selectedFruitImageView != null) {
            selectedFruitImageView.setImage(image);
        } else {
            System.err.println("selectedFruitImageView is null");
        }
    }

    @FXML
    private void addToCart() {
        if (currentSelectedFruit == null) {
            showAlert("Selection Error", "Please select a fruit first.", Alert.AlertType.WARNING);
            return;
        }

        if (quantityKgTextField == null) {
            showAlert("Configuration Error", "Quantity input field is not initialized.", Alert.AlertType.ERROR);
            return;
        }

        String quantityStr = quantityKgTextField.getText();
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Quantity", "Please enter a valid positive number for quantity.", Alert.AlertType.ERROR);
            return;
        }

        // Check if the fruit is already in the cart
        Optional<CartItem> existingCartItem = cartItems.stream()
                .filter(item -> item.getFruit().getId() == currentSelectedFruit.getId())
                .findFirst();

        if (existingCartItem.isPresent()) {
            // Update quantity if fruit already exists
            existingCartItem.get().setQuantity(existingCartItem.get().getQuantity() + quantity);
        } else {
            // Add new cart item
            cartItems.add(new CartItem(currentSelectedFruit, quantity));
        }

        // The listener on cartItems will handle UI updates
        showAlert("Success", currentSelectedFruit.getName() + " added to cart!\nQuantity: " +
                quantity + "\nTotal Price: " + priceFormat.format(currentSelectedFruit.getPrice() * quantity), Alert.AlertType.INFORMATION);
    }

    @FXML
    private void search() {
        if (searchTextField == null) {
            System.err.println("searchTextField is null");
            return;
        }
        String searchText = searchTextField.getText().toLowerCase();
        if (allFruits != null) {
            List<Fruit> filteredFruits = new ArrayList<>();
            for (Fruit fruit : allFruits) {
                if (fruit.getName().toLowerCase().contains(searchText)) {
                    filteredFruits.add(fruit);
                }
            }
            populateFruitGrid(filteredFruits);
        }
    }

    // --- Cart Sidebar Logic ---

    @FXML
    private void toggleCart() {
        if (cartSidebar == null) {
            System.err.println("cartSidebar is null");
            return;
        }
        boolean isVisible = cartSidebar.isVisible();
        cartSidebar.setVisible(!isVisible);
        cartSidebar.setManaged(!isVisible); // This ensures it takes up space only when visible
        updateCartUI(); // Update UI when cart visibility changes
    }

    private void updateCartUI() {
        if (cartItemsContainer == null || emptyCartMessage == null || orderSummary == null || placeOrderBtn == null || cartItemCountLabel == null) {
            System.err.println("One or more cart UI components are null");
            return;
        }
        cartItemsContainer.getChildren().clear();
        if (cartItems.isEmpty()) {
            emptyCartMessage.setVisible(true);
            emptyCartMessage.setManaged(true);
            orderSummary.setVisible(false);
            orderSummary.setManaged(false);
            placeOrderBtn.setVisible(false);
            placeOrderBtn.setManaged(false);
            cartItemCountLabel.setVisible(false); // Hide count if cart is empty
        } else {
            emptyCartMessage.setVisible(false);
            emptyCartMessage.setManaged(false);
            orderSummary.setVisible(true);
            orderSummary.setManaged(true);
            placeOrderBtn.setVisible(true);
            placeOrderBtn.setManaged(true);
            cartItemCountLabel.setVisible(true); // Show count if items are present

            for (CartItem item : cartItems) {
                cartItemsContainer.getChildren().add(createCartItemDisplay(item));
            }
        }
        cartItemCountLabel.setText(String.valueOf(getTotalItemsInCart()));
    }

    private HBox createCartItemDisplay(CartItem item) {
        HBox itemBox = new HBox(10);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        itemBox.setStyle("-fx-padding: 10 20; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0;");

        ImageView imageView = new ImageView();
        try {
            String imageUrl = item.getFruit().getImageUrl();
            Image image;
            if (imageUrl != null && imageUrl.startsWith("http")) {
                image = new Image(imageUrl, true);
            } else if (imageUrl != null && !imageUrl.isEmpty()) {
                image = new Image(getClass().getResourceAsStream("/images/" + imageUrl));
            } else {
                image = new Image(getClass().getResourceAsStream("/images/placeholder.png"));
            }
            imageView.setImage(image);
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            System.err.println("Error loading cart item image for " + item.getFruit().getName());
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            } catch (Exception ex) {
                System.err.println("Placeholder image missing for cart item.");
            }
        }

        VBox infoBox = new VBox(2);
        Label nameLabel = new Label(item.getFruit().getName());
        nameLabel.setFont(new Font("System Bold", 14));
        Label priceLabel = new Label(priceFormat.format(item.getFruit().getPrice()));
        priceLabel.setFont(new Font(12));
        infoBox.getChildren().addAll(nameLabel, priceLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox quantityControls = new HBox(5);
        quantityControls.setAlignment(Pos.CENTER);
        Button decreaseBtn = new Button("-");
        decreaseBtn.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5; -fx-min-width: 25; -fx-min-height: 25; -fx-font-size: 14;");
        Label quantityLabel = new Label(String.valueOf(item.getQuantity()));
        quantityLabel.setFont(new Font("System Bold", 14));
        Button increaseBtn = new Button("+");
        increaseBtn.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5; -fx-min-width: 25; -fx-min-height: 25; -fx-font-size: 14;");

        decreaseBtn.setOnAction(e -> {
            item.setQuantity(item.getQuantity() - 1);
            if (item.getQuantity() <= 0) {
                cartItems.remove(item); // Remove if quantity drops to 0
            } else {
                quantityLabel.setText(String.valueOf(item.getQuantity()));
            }
            updateCartTotals();
            updateCartUI(); // Rebuild UI to reflect changes
        });

        increaseBtn.setOnAction(e -> {
            item.setQuantity(item.getQuantity() + 1);
            quantityLabel.setText(String.valueOf(item.getQuantity()));
            updateCartTotals();
        });

        quantityControls.getChildren().addAll(decreaseBtn, quantityLabel, increaseBtn);

        Button removeBtn = new Button("✕");
        removeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #999999; -fx-font-size: 16; -fx-font-weight: bold;");
        removeBtn.setOnAction(e -> {
            cartItems.remove(item);
            updateCartTotals();
            updateCartUI(); // Rebuild UI after removal
        });

        itemBox.getChildren().addAll(imageView, infoBox, spacer, quantityControls, removeBtn);
        return itemBox;
    }

    private void updateCartTotals() {
        if (itemsTotalLabel == null || discountLabel == null || totalLabel == null) {
            System.err.println("One or more total labels are null");
            return;
        }
        double itemsTotal = cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
        double discount = (itemsTotal > 0) ? 3.00 : 0.00; // Apply discount only if there are items
        double total = itemsTotal - discount;

        itemsTotalLabel.setText(priceFormat.format(itemsTotal));
        discountLabel.setText("- " + priceFormat.format(discount));
        totalLabel.setText(priceFormat.format(total));
    }

    private int getTotalItemsInCart() {
        return cartItems.stream().mapToInt(CartItem::getQuantity).sum();
    }

    // Delivery Option Handlers
    @FXML
    private void selectDelivery() {
        selectedDeliveryOption = "Delivery";
        selectDeliveryOption(deliveryBtn);
    }

    @FXML
    private void selectDineIn() {
        selectedDeliveryOption = "Dine in";
        selectDeliveryOption(dineInBtn);
    }

    @FXML
    private void selectTakeAway() {
        selectedDeliveryOption = "Take away";
        selectDeliveryOption(takeAwayBtn);
    }

    private void selectDeliveryOption(Button selectedButton) {
        if (deliveryBtn == null || dineInBtn == null || takeAwayBtn == null) {
            System.err.println("One or more delivery option buttons are null");
            return;
        }
        // Reset all buttons to unselected state
        String unselectedStyle = "-fx-background-color: transparent; -fx-text-fill: #666666; " +
                "-fx-background-radius: 20; -fx-padding: 8 16;";
        String selectedStyle = "-fx-background-color: #333333; -fx-text-fill: white; " +
                "-fx-background-radius: 20; -fx-padding: 8 16;";

        deliveryBtn.setStyle(unselectedStyle);
        dineInBtn.setStyle(unselectedStyle);
        takeAwayBtn.setStyle(unselectedStyle);

        // Set selected button style
        selectedButton.setStyle(selectedStyle);
    }

    private void setInitialDeliverySelection() {
        selectDeliveryOption(deliveryBtn);
    }

    private void updateOrderNumber() {
        if (orderNumberLabel != null) {
            orderNumberLabel.setText("Order #" + orderCounter);
        } else {
            System.err.println("orderNumberLabel is null");
        }
    }

    @FXML
    private void placeOrder() {
        if (cartItems.isEmpty()) {
            showAlert("Empty Cart", "Please add some items to your cart before placing an order.", Alert.AlertType.WARNING);
            return;
        }

        // Generate order summary
        StringBuilder orderSummaryText = new StringBuilder();
        orderSummaryText.append("Order Summary:\n");
        orderSummaryText.append("Order #").append(orderCounter).append("\n");
        orderSummaryText.append("Delivery Option: ").append(selectedDeliveryOption).append("\n\n");

        orderSummaryText.append("Items:\n");
        cartItems.forEach(item ->
                orderSummaryText.append("- ").append(item.getFruit().getName())
                        .append(" x").append(item.getQuantity())
                        .append(" = ").append(priceFormat.format(item.getTotalPrice())).append("\n")
        );

        double itemsTotal = cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
        double discount = (itemsTotal > 0) ? 3.00 : 0.00; // Only apply discount if there are items
        double total = itemsTotal - discount;

        orderSummaryText.append("\nSubtotal: ").append(priceFormat.format(itemsTotal)).append("\n");
        orderSummaryText.append("Discount: -").append(priceFormat.format(discount)).append("\n");
        orderSummaryText.append("Total: ").append(priceFormat.format(total));

        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Placed Successfully!");
        alert.setHeaderText("Thank you for your order!");
        alert.setContentText(orderSummaryText.toString());
        alert.showAndWait();

        // Reset cart after successful order
        resetCart();
    }

    private void resetCart() {
        orderCounter++;
        updateOrderNumber();
        cartItems.clear(); // This will trigger the cartItems listener and update UI
        setInitialDeliverySelection(); // Reset delivery option to default
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
