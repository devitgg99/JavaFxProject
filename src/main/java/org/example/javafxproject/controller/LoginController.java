package org.example.javafxproject.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        // Basic email format validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showAlert("Error", "Invalid email format");
            return;
        }

        // Here you would typically add authentication logic
        // For demo purposes, we'll just show a success message
        showAlert("Success", "Login successful for " + email);

        // Clear fields after successful login
        emailField.setText("");
        passwordField.setText("");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}