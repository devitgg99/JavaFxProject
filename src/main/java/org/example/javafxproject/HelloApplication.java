package org.example.javafxproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
<<<<<<< Updated upstream
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LoginForm.fxml"));
=======
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("dashboard-view"));
>>>>>>> Stashed changes
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Movie Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}