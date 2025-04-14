package com.example.hmswebservices;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class DrawUI {
    private Stage primaryStage;
    private Scene loginScene;

    public void create(Stage primaryStage) {
        this.primaryStage = primaryStage;

        VBox pane = new VBox(20);
        pane.setPadding(new Insets(20));
        pane.setAlignment(Pos.CENTER);
        pane.setStyle("-fx-background-color: #f7f9fc;");

        // Text Fields and Labels
        Label UserNameLabel = new Label("Enter UserName:");
        UserNameLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");

        TextField UserNameField = new TextField();
        UserNameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 5px;");

        Label PasswordLabel = new Label("Enter Password:");
        PasswordLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");

        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 5px;");


        // Buttons
        Button LoginButton = new Button("Login");
        HBox buttonBox = new HBox(30, LoginButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Button actions
        LoginButton.setOnAction(event -> {
            String username = UserNameField.getText().trim();
            String password = passwordField.getText().trim();
            username = username.toLowerCase();

            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Empty Fields", "Please enter both username and password");
            }
            else if(password.length() < 4 || password.length() > 16) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Password", "Password must be between 4-16 characters");
            }
            else if (username.length() < 3 || username.length() > 16) {
                showAlert(Alert.AlertType.ERROR, "Error", "Invalid Username", "Username must be between 3-16 characters");
            }
            else {
                // Special case for lab technician
                if (username.equals("admin") && password.equals("admin123456789")) {
                    LabDashboard labDashboard = new LabDashboard(primaryStage, username);
                    labDashboard.open();
                }
                // Normal cases
                else {
                    String patientId = authenticateAndGetPatientId(username, password);

                    if (patientId != null) {
                        PatientDashboard patientDashboard = new PatientDashboard(primaryStage, patientId);
                        patientDashboard.open();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid Credentials",
                                "Invalid username or password");
                    }
                }
            }
        });

        // Add all nodes to the pane
        pane.getChildren().addAll(
                UserNameLabel,
                UserNameField,
                PasswordLabel,
                passwordField,
                buttonBox
        );

        // Store the login scene for logout functionality
        loginScene = new Scene(pane, 600, 600);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("HMS-WebServices Login");
        primaryStage.show();
    }
    private String authenticateAndGetPatientId(String username, String password) {
        try {
            // Read the patients data file
            File patientsFile = new File("Patients.json");
            if (!patientsFile.exists()) {
                showAlert(Alert.AlertType.ERROR, "Error", "System Error",
                        "Patients data file not found");
                return null;
            }

            String content = new String(Files.readAllBytes(patientsFile.toPath()));
            JSONArray patientsArray = new JSONArray(content);

            // Find matching credentials
            for (int i = 0; i < patientsArray.length(); i++) {
                JSONObject patient = patientsArray.getJSONObject(i);
                String storedUsername = patient.getString("username");
                String storedPassword = patient.getString("password");
                String patientId = patient.getString("patientId");

                if (storedUsername.equalsIgnoreCase(username) && storedPassword.equals(password)) {
                    return patientId;  // Return the patient ID if credentials match
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "System Error",
                    "Could not read patient data: " + e.getMessage());
        }
        return null;  // Return null if no match found
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    private void createLogoutButton(VBox pane) {
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> primaryStage.setScene(loginScene));

        HBox buttonBox = new HBox(logoutButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(20));

        pane.getChildren().add(buttonBox);
    }

//    private void openLabDashboard(Stage primaryStage, String username) {
//        VBox labPane = new VBox(20);
//        labPane.setPadding(new Insets(20));
//        labPane.setAlignment(Pos.CENTER);
//        labPane.setStyle("-fx-background-color: #eaf2f8;");
//
//        Label welcomeLabel = new Label("Welcome Lab Technician " + username);
//        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #2980b9;");
//
//        // Add lab-specific components here
//        Label functionsLabel = new Label("Lab Functions:");
//        functionsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #2c3e50;");
//
//
//
//        Button viewTestsButton = new Button("Add");
//        Button uploadResultsButton = new Button("Edit");
//
//        VBox labFunctions = new VBox(10, functionsLabel, viewTestsButton, uploadResultsButton);
//        labFunctions.setAlignment(Pos.CENTER);
//
//        labPane.getChildren().addAll(welcomeLabel, labFunctions);
//        createLogoutButton(labPane);
//
//        Scene labScene = new Scene(labPane, 800, 600);
//        primaryStage.setScene(labScene);
//        primaryStage.setTitle("HMS - Lab Technician Dashboard");
//    }

    private void openPatientDashboard(Stage primaryStage, String username) {
        VBox patientPane = new VBox(20);
        patientPane.setPadding(new Insets(20));
        patientPane.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome Patient " + username);
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #2c3e50;");

        // Add patient-specific components here
        Label functionsLabel = new Label("Patient Functions:");
        functionsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #2c3e50;");

        Button viewRecordsButton = new Button("View Medical Records");
        Button bookAppointmentButton = new Button("Book Appointment");

        VBox patientFunctions = new VBox(10, functionsLabel, viewRecordsButton, bookAppointmentButton);
        patientFunctions.setAlignment(Pos.CENTER);

        patientPane.getChildren().addAll(welcomeLabel, patientFunctions);
        createLogoutButton(patientPane);

        Scene patientScene = new Scene(patientPane, 800, 600);
        primaryStage.setScene(patientScene);
        primaryStage.setTitle("HMS - Patient Portal");
    }
}



