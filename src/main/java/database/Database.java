package database;

import model.Client;
import model.Products;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class Database {
    private final Connection connection;

    public Database(String url, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
    }

    public synchronized void addProducts(Products product) throws SQLException {
        String sql = "INSERT INTO products (id, name, description) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, UUID.randomUUID());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.executeUpdate();
        }
    }

    public synchronized void addClients(Client client) throws SQLException {
        String sql = "INSERT INTO clients (id, name, age) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, UUID.randomUUID());
            stmt.setString(2, client.getName());
            stmt.setInt(3, client.getAge());
            stmt.executeUpdate();
        }
    }

    public void close() throws SQLException {
        connection.close();
    }
}