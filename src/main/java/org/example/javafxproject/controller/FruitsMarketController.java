package org.example.javafxproject.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.javafxproject.Product.Fruit;

import java.util.ArrayList;
import java.util.List;

public class FruitsMarketController {

    public VBox selectedFruitPanel;
    // FXML Elements
    @FXML
    private TextField searchField;
    @FXML
    private FlowPane fruitGrid;
    @FXML
    private ListView<String> cartListView;
    @FXML
    private Label cartCountLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private ImageView selectedFruitImage;
    @FXML
    private Label selectedFruitName;
    @FXML
    private Label selectedFruitPrice;
    @FXML
    private ComboBox<String> quantityComboBox;
    @FXML
    private ComboBox<String> alternativeQuantityComboBox;
    @FXML
    private Button addToCartButton;

    // Data Structures
    private List<Fruit> fruits = new ArrayList<>();
    private ObservableList<String> cartItems = FXCollections.observableArrayList();
    private double totalCost = 0.0;

    // Initialization
    @FXML
    public void initialize() {
        // Sample data for fruits
        fruits.add(new Fruit("Kiwi", 2.99, "/org/example/javafxproject/image/kiwi_large.png"));
        fruits.add(new Fruit("Coconut", 3.99, "/org/example/javafxproject/image/coconut.png"));
        fruits.add(new Fruit("Peach", 1.50, "/org/example/javafxproject/image/peach.png"));
        fruits.add(new Fruit("Grapes", 0.99, "/org/example/javafxproject/image/grapes.png"));
        fruits.add(new Fruit("Watermelon", 4.99, "/org/example/javafxproject/image/watermelon.png"));
        fruits.add(new Fruit("Orange", 2.99, "/org/example/javafxproject/image/orange.png"));

        // Populate the fruit grid
        populateFruitGrid();

        // Initialize cart list view
        cartListView.setItems(cartItems);

        // Initialize quantity comboboxes
        quantityComboBox.getItems().addAll("1", "2", "3");
        alternativeQuantityComboBox.getItems().addAll("1", "2", "3");

        // Update cart summary initially
        updateCartSummary();
    }

    // Method to populate the fruit grid
    private void populateFruitGrid() {
        fruitGrid.getChildren().clear();
        for (Fruit fruit : fruits) {
            HBox fruitCard = createFruitCard(fruit);
            fruitGrid.getChildren().add(fruitCard);
        }
    }

    // Method to create a fruit card
    private HBox createFruitCard(Fruit fruit) {
        HBox card = new HBox(10);
        card.setPrefWidth(200);
        card.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");

        ImageView fruitImage = new ImageView(new Image(getClass().getResource(fruit.getImagePath()).toExternalForm()));
        fruitImage.setFitWidth(100);
        fruitImage.setPreserveRatio(true);

        VBox fruitInfo = new VBox(5);
        fruitInfo.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label fruitName = new Label(fruit.getName());
        fruitName.setStyle("-fx-font-weight: bold;");

        Label fruitPrice = new Label("$" + fruit.getPrice());
        fruitPrice.setStyle("-fx-font-weight: bold;");

        fruitInfo.getChildren().addAll(fruitName, fruitPrice);

        card.getChildren().addAll(fruitImage, fruitInfo);

        // Add event handler for selecting a fruit
        card.setOnMouseClicked(event -> selectFruit(fruit));

        return card;
    }

    // Method to handle fruit selection
    private void selectFruit(Fruit fruit) {
        selectedFruitImage.setImage(new Image(getClass().getResource(fruit.getImagePath()).toExternalForm()));
        selectedFruitName.setText(fruit.getName());
        selectedFruitPrice.setText("$" + fruit.getPrice());
        quantityComboBox.setValue("1"); // Reset quantity
        alternativeQuantityComboBox.setValue("1"); // Reset alternative quantity
        addToCartButton.setDisable(false);
    }

    // Method to add fruit to cart
    @FXML
    private void addToCart() {
        String fruitName = selectedFruitName.getText();
        double price = Double.parseDouble(selectedFruitPrice.getText().substring(1)); // Remove the '$'
        String quantityStr = quantityComboBox.getValue();
        int quantity = Integer.parseInt(quantityStr);

        String cartItem = fruitName + " x " + quantity + " kg";
        cartItems.add(cartItem);

        // Update total cost
        totalCost += price * quantity;
        totalLabel.setText("$" + totalCost);

        // Update cart count
        cartCountLabel.setText(String.valueOf(cartItems.size()));

        // Clear selection
        clearSelection();
    }

    // Method to clear cart
    @FXML
    private void clearCart() {
        cartItems.clear();
        totalCost = 0.0;
        updateCartSummary();
    }

    // Method to checkout
    @FXML
    private void checkout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Checkout");
        alert.setHeaderText(null);
        alert.setContentText("Proceeding to checkout with total: $" + totalCost);
        alert.showAndWait();
    }

    // Method to search for fruits
    @FXML
    private void searchFruits() {
        String query = searchField.getText().toLowerCase();
        List<Fruit> filteredFruits = new ArrayList<>();
        for (Fruit fruit : fruits) {
            if (fruit.getName().toLowerCase().contains(query)) {
                filteredFruits.add(fruit);
            }
        }
        populateFruitGrid(filteredFruits);
    }

    // Helper method to populate fruit grid with filtered fruits
    private void populateFruitGrid(List<Fruit> filteredFruits) {
        fruitGrid.getChildren().clear();
        for (Fruit fruit : filteredFruits) {
            HBox fruitCard = createFruitCard(fruit);
            fruitGrid.getChildren().add(fruitCard);
        }
    }

    // Method to update cart summary
    private void updateCartSummary() {
        cartCountLabel.setText(String.valueOf(cartItems.size()));
        totalLabel.setText("$" + totalCost);
    }

    // Method to clear fruit selection
    private void clearSelection() {
        selectedFruitImage.setImage(null);
        selectedFruitName.setText("");
        selectedFruitPrice.setText("");
        quantityComboBox.setValue(null);
        alternativeQuantityComboBox.setValue(null);
        addToCartButton.setDisable(true);
    }
}