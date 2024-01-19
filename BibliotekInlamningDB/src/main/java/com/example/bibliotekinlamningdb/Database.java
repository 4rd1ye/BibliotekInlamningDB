package com.example.bibliotekinlamningdb;

import java.sql.Connection;
import java.sql.DriverManager;


public class Database {
    public Connection databaseLink;
    public Connection getConnection() {
        String database = "library";
        String username = "root";
        String password = "21032021";
        String url = "jdbc:mysql://localhost/" + database;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return databaseLink;
    }
}