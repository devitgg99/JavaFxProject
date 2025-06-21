module org.example.javafxproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.javafxproject to javafx.fxml;
    exports org.example.javafxproject;
}