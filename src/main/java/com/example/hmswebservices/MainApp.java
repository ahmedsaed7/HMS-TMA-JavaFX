package com.example.hmswebservices;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp {
    public static class mainApp extends Application {

        @Override
        public void start(Stage primaryStage) {
            new DrawUI().create(primaryStage);
        }

        public static void main(String[] args) {
            launch(args);
        }
    }
}
