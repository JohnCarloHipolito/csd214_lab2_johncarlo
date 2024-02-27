package com.triosstudent.csd214_lab2_johncarlo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TodoApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TodoApplication.class.getResource("todo-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("CSD214 Lab 2 John Carlo");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}