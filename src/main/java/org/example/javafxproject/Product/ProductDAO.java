// application/ProductDAO.java
package org.example.javafxproject.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.example.javafxproject.Utility.DBUtil;
public class ProductDAO {

    public List<Fruit> getAllProducts() throws SQLException {
        List<Fruit> products = new ArrayList<>();
        String query = "SELECT product_id, product_name, quantity, price, image_url FROM products";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String name = rs.getString("product_name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                String imageUrl = rs.getString("image_url");
                products.add(new Fruit(productId, name, quantity, price, imageUrl));
            }
        }
        return products;
    }

    public Fruit getProductById(int productId) throws SQLException {
        String query = "SELECT product_id, product_name, quantity, price, image_url FROM products WHERE product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("product_name");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price");
                    String imageUrl = rs.getString("image_url");
                    return new Fruit(productId, name, quantity, price, imageUrl);
                }
            }
        }
        return null; // Product not found
    }

    public void addProduct(Fruit product) throws SQLException {
        String query = "INSERT INTO products (product_name, quantity, price, image_url) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, product.getName());
            pstmt.setInt(2, product.getQuantity());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setString(4, product.getImageUrl());
            pstmt.executeUpdate();
        }
    }

    public void updateProduct(Fruit product) throws SQLException {
        String query = "UPDATE products SET product_name = ?, quantity = ?, price = ?, image_url = ? WHERE product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, product.getName());
            pstmt.setInt(2, product.getQuantity());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setString(4, product.getImageUrl());
            pstmt.setInt(5, product.getProductId());
            pstmt.executeUpdate();
        }
    }

    public void deleteProduct(int productId) throws SQLException {
        String query = "DELETE FROM products WHERE product_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            pstmt.executeUpdate();
        }
    }
}