package com.example.bibliotekinlamningdb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LibraryApplication extends Application {
    private Database database;

    @Override
    public void start(Stage stage) throws IOException {
        database = new Database();
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 400);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

