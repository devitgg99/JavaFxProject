package org.example.javafxproject.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.example.javafxproject.Utility.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        // Authenticate with PostgreSQL DB
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM login_users WHERE username = ? AND password = ?")) {

            stmt.setString(1, email);
            stmt.setString(2, password); // 🔐 Replace with hashed password in real apps

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                errorLabel.setVisible(false);
                System.out.println("Login successful for: " + email);

            } else {
                showError("Invalid email or password");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database connection failed");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        FadeTransition fade = new FadeTransition(Duration.millis(3000), errorLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> errorLabel.setVisible(false));
        fade.play();
    }
}
