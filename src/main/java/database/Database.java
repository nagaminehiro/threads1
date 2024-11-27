package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import model.Client;
import model.Products;

public class Database implements AutoCloseable {

    private final Connection connection;
    private final PreparedStatement stmtProduct;
    private final PreparedStatement stmtClient;

    public Database(String url, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
        this.stmtProduct = connection.prepareStatement("INSERT INTO products (id, name, description) VALUES (?, ?, ?)");
        this.stmtClient = connection.prepareStatement("INSERT INTO clients (id, name, age) VALUES (?, ?, ?)");
    }

    public void addProducts(Products product) throws SQLException {
        stmtProduct.setObject(1, product.getId());
        stmtProduct.setString(2, product.getName());
        stmtProduct.setString(3, product.getDescription());
        stmtProduct.executeUpdate();
    }

    public  void addClients(Client client) throws SQLException {
        stmtClient.setObject(1, client.getId());
        stmtClient.setString(2, client.getName());
        stmtClient.setInt(3, client.getAge());
        stmtClient.executeUpdate();
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    public void truncateTables() {
        try (PreparedStatement stmt = connection.prepareStatement("TRUNCATE TABLE products, clients")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}