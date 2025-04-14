package com.example.hmswebservices;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PatientDashboard {
    private Stage primaryStage;
    private String patientId;
    private static final String JSON_FILE_PATH = "Patients.json";

    public PatientDashboard(Stage primaryStage, String patientId) {
        this.primaryStage = primaryStage;
        this.patientId = patientId;
    }

    public void open() {
        VBox rootPane = new VBox(20);
        rootPane.setPadding(new Insets(20));
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setStyle("-fx-background-color: #f9f9f9;");

        Label titleLabel = new Label("Patient Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #3498db;");

        // Load patient data from file
        JSONObject patient = loadPatientDataById(patientId);

        if (patient == null) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Patient with ID " + patientId + " not found.");
            return;
        }

        try {
            // Create tables for patient information and lab results
            TableView<PatientData> patientTable = createPatientTable(patient);
            TableView<LabResult> labResultsTable = createLabResultsTable(patient.getJSONObject("labResults"));

            // Back Button
            Button backButton = new Button("Back to Main Menu");
            backButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            backButton.setOnAction(e -> {
                primaryStage.close();
                DrawUI drawUI = new DrawUI();
                try {
                    Stage newStage = new Stage();
                    drawUI.create(newStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Add components to the root pane
            rootPane.getChildren().addAll(
                    titleLabel,
                    new Label("Patient Information:"),
                    patientTable,
                    new Label("Lab Results:"),
                    labResultsTable,
                    backButton
            );

            // Set the scene
            Scene scene = new Scene(rootPane, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Patient Dashboard");
            primaryStage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Data Error",
                    "Error displaying patient data: " + e.getMessage());
        }
    }

    private TableView<PatientData> createPatientTable(JSONObject patient) {
        TableView<PatientData> table = new TableView<>();

        // Create columns
        TableColumn<PatientData, String> fieldCol = new TableColumn<>("Field");
        fieldCol.setCellValueFactory(new PropertyValueFactory<>("field"));

        TableColumn<PatientData, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

        table.getColumns().addAll(fieldCol, valueCol);

        // Add data
        table.getItems().addAll(
                new PatientData("Name", patient.getString("name")),
                new PatientData("Patient ID", patient.getString("patientId")),
                new PatientData("Age", String.valueOf(patient.getInt("age"))),
                new PatientData("Gender", patient.getString("gender"))
        );

        // Style the table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(150);
        table.setStyle("-fx-font-size: 14px;");

        return table;
    }

    private TableView<LabResult> createLabResultsTable(JSONObject labResults) {
        TableView<LabResult> table = new TableView<>();

        // Create columns
        TableColumn<LabResult, String> testCol = new TableColumn<>("Test");
        testCol.setCellValueFactory(new PropertyValueFactory<>("test"));

        TableColumn<LabResult, String> resultCol = new TableColumn<>("Result");
        resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));

        table.getColumns().addAll(testCol, resultCol);

        // Add data
        table.getItems().addAll(
                new LabResult("Blood Test", labResults.getString("bloodTest")),
                new LabResult("X-Ray", labResults.getString("xRay"))
        );

        // Style the table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(100);
        table.setStyle("-fx-font-size: 14px;");

        return table;
    }

    private JSONObject loadPatientDataById(String patientId) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject patient = jsonArray.getJSONObject(i);
                if (patient.getString("patientId").equals(patientId)) {
                    return patient;
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "File Error",
                    "Could not load patient data: " + e.getMessage());
        }
        return null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Helper classes for table data
    public static class PatientData {
        private final String field;
        private final String value;

        public PatientData(String field, String value) {
            this.field = field;
            this.value = value;
        }

        public String getField() { return field; }
        public String getValue() { return value; }
    }

    public static class LabResult {
        private final String test;
        private final String result;

        public LabResult(String test, String result) {
            this.test = test;
            this.result = result;
        }

        public String getTest() { return test; }
        public String getResult() { return result; }
    }
}