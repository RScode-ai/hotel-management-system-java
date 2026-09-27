package com.hotel.config;

import com.hotel.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new DatabaseException("db.properties not found in classpath.");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new DatabaseException("Failed to load db.properties", e);
        }
    }

    private DatabaseConnection() {}

    public static Connection getConnection() {
        try {
            String url = PROPERTIES.getProperty("db.url");
            String username = PROPERTIES.getProperty("db.username");
            String password = PROPERTIES.getProperty("db.password");
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new DatabaseException("Could not connect to the database. Is MySQL running?", e);
        }
    }
}
