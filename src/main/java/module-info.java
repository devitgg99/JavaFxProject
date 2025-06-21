module org.example.javafxproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.jconsole;
    requires java.sql;


    opens org.example.javafxproject to javafx.fxml;
    exports org.example.javafxproject;
    exports org.example.javafxproject.controller;
    opens org.example.javafxproject.controller to javafx.fxml;
}