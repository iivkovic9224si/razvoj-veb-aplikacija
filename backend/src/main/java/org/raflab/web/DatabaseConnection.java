package org.raflab.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/raf_novosti?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private static Connection connection = null;

    private DatabaseConnection() {

    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Registracija drajvera (opciono u novijim verzijama, ali sigurno za RAF)
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Uspesno povezivanje sa MySQL bazom!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver nije pronadjen: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Greska prilikom povezivanja na bazu: " + e.getMessage());
        }
        return connection;
    }
}