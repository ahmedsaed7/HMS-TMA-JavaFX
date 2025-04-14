package com.example.hmswebservices;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class LabDashboard {
    private Stage primaryStage;
    private String username;
    private ObservableList<JSONObject> patientData;
    private static final String JSON_FILE_PATH = "Patients.json";

    public LabDashboard(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.patientData = FXCollections.observableArrayList();
        loadPatientDataFromFile();
    }

    private void loadPatientDataFromFile() {
        try {
            // Read the JSON file
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE_PATH)));
            JSONArray jsonArray = new JSONArray(content);
            // Convert all entries to a consistent format
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject patient = jsonArray.getJSONObject(i);
                // Create a standardized JSON object
                JSONObject standardizedPatient = new JSONObject();
                // Handle different field names in the JSON
                String patientId = patient.optString("patient_Id", patient.optString("patientId", ""));
                standardizedPatient.put("patient_Id", patientId);
                standardizedPatient.put("name", patient.optString("name", ""));
                standardizedPatient.put("age", patient.optInt("age", 0));
                // Handle gender (convert Arabic to English if needed)
                String gender = patient.optString("gender", "");
                if (gender.equals("ذكر")) gender = "Male";
                if (gender.equals("أنثى")) gender = "Female";
                standardizedPatient.put("gender", gender);
                // Handle lab results with different field names
                JSONObject labResults = new JSONObject();
                if (patient.has("lab_results")) {
                    JSONObject existingResults = patient.getJSONObject("lab_results");
                    labResults.put("blood_test", existingResults.optString("blood_test", ""));
                    labResults.put("x_ray", existingResults.optString("x_ray", ""));
                } else if (patient.has("labResults")) {
                    JSONObject existingResults = patient.getJSONObject("labResults");
                    labResults.put("blood_test", existingResults.optString("bloodTest", ""));
                    labResults.put("x_ray", existingResults.optString("xRay", ""));
                }
                standardizedPatient.put("lab_results", labResults);
                patientData.add(standardizedPatient);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "File Error",
                    "Could not load patient data: " + e.getMessage());
            // Initialize with empty list if file can't be read
            patientData = FXCollections.observableArrayList();
        }
    }

    private void savePatientDataToFile() {
        try (FileWriter file = new FileWriter(JSON_FILE_PATH)) {
            JSONArray jsonArray = new JSONArray();
            for (JSONObject patient : patientData) {
                jsonArray.put(patient);
            }
            file.write(jsonArray.toString(4)); // 4 spaces for indentation
            file.flush();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "File Error",
                    "Could not save patient data: " + e.getMessage());
        }
    }

    public void open() {
        VBox labPane = new VBox(20);
        labPane.setPadding(new Insets(20));
        labPane.setAlignment(Pos.CENTER);
        labPane.setStyle("-fx-background-color: #eaf2f8;");
        Label welcomeLabel = new Label("Welcome Lab Technician " + username);
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #2980b9;");

        // Search components
        HBox searchBox = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search by Patient ID or Name");
        searchField.setPrefWidth(300);
        Button searchButton = new Button("Search");
        searchBox.setAlignment(Pos.CENTER);
        searchBox.getChildren().addAll(searchField, searchButton);

        // Table to display patient data
        TableView<JSONObject> patientTable = new TableView<>();
        patientTable.setEditable(false);
        TableColumn<JSONObject, String> idColumn = new TableColumn<>("Patient ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getString("patient_Id")));
        TableColumn<JSONObject, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getString("name")));
        TableColumn<JSONObject, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getInt("age")).asObject());
        TableColumn<JSONObject, String> genderColumn = new TableColumn<>("Gender");
        genderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getString("gender")));
        TableColumn<JSONObject, String> bloodTestColumn = new TableColumn<>("Blood Test");
        bloodTestColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJSONObject("lab_results").getString("blood_test")));
        TableColumn<JSONObject, String> xRayColumn = new TableColumn<>("X-Ray");
        xRayColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJSONObject("lab_results").getString("x_ray")));
        patientTable.getColumns().addAll(idColumn, nameColumn, ageColumn, genderColumn, bloodTestColumn, xRayColumn);
        patientTable.setItems(patientData);

        // Action buttons
        HBox buttonBox = new HBox(20);
        Button addButton = new Button("Add New Patient");
        Button editButton = new Button("Edit Results");
        Button reloadButton = new Button("Reload Data"); // New Reload Data button
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(addButton, editButton, reloadButton); // Add reloadButton to the layout

        // Search functionality
        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText().toLowerCase();
            if (searchTerm.isEmpty()) {
                patientTable.setItems(patientData);
                return;
            }
            ObservableList<JSONObject> filteredData = FXCollections.observableArrayList();
            for (JSONObject patient : patientData) {
                String patientId = patient.getString("patient_Id").toLowerCase();
                String name = patient.getString("name").toLowerCase();
                if (patientId.contains(searchTerm) || name.contains(searchTerm)) {
                    filteredData.add(patient);
                }
            }
            if (filteredData.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Patient Not Found", null,
                        "No patient found with ID or name: " + searchTerm);
            } else {
                patientTable.setItems(filteredData);
            }
        });

        // Add new patient functionality (unchanged)
        addButton.setOnAction(e -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add New Patient");
            dialog.setHeaderText("Enter patient details");
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            TextField idField = new TextField();
            idField.setPromptText("Patient ID (Required)");
            TextField nameField = new TextField();
            nameField.setPromptText("Name (Required)");
            TextField ageField = new TextField();
            ageField.setPromptText("Age (Required)");
            ComboBox<String> genderCombo = new ComboBox<>();
            genderCombo.getItems().addAll("Male", "Female", "Other");
            TextField bloodTestField = new TextField();
            bloodTestField.setPromptText("Blood Test Results");
            TextField xrayField = new TextField();
            xrayField.setPromptText("X-Ray Results");

            grid.add(new Label("Patient ID:"), 0, 0);
            grid.add(idField, 1, 0);
            grid.add(new Label("Name:"), 0, 1);
            grid.add(nameField, 1, 1);
            grid.add(new Label("Age:"), 0, 2);
            grid.add(ageField, 1, 2);
            grid.add(new Label("Gender:"), 0, 3);
            grid.add(genderCombo, 1, 3);
            grid.add(new Label("Blood Test:"), 0, 4);
            grid.add(bloodTestField, 1, 4);
            grid.add(new Label("X-Ray:"), 0, 5);
            grid.add(xrayField, 1, 5);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        String patientId = idField.getText().trim();
                        String name = nameField.getText().trim();
                        String ageText = ageField.getText().trim();
                        String gender = genderCombo.getValue();

                        // Validate required fields
                        if (patientId.isEmpty() || name.isEmpty() || ageText.isEmpty()) {
                            showAlert(Alert.AlertType.WARNING, "Missing Fields", null,
                                    "Patient ID, Name, and Age are required fields.");
                            return;
                        }

                        int age = Integer.parseInt(ageText);

                        // Check if patient ID already exists
                        boolean idExists = patientData.stream()
                                .anyMatch(patient -> patient.getString("patient_Id").equals(patientId));

                        if (idExists) {
                            showAlert(Alert.AlertType.ERROR, "Duplicate ID", null,
                                    "A patient with ID " + patientId + " already exists.");
                            return;
                        }

                        // Create new patient object
                        JSONObject newPatient = new JSONObject();
                        newPatient.put("patient_Id", patientId);
                        newPatient.put("name", name);
                        newPatient.put("age", age);
                        newPatient.put("gender", gender);

                        JSONObject labResults = new JSONObject();
                        labResults.put("blood_test", bloodTestField.getText());
                        labResults.put("x_ray", xrayField.getText());
                        newPatient.put("lab_results", labResults);

                        // Add to patient data and save to file
                        patientData.add(newPatient);
                        patientTable.setItems(patientData); // Sync TableView with updated data
                        savePatientDataToFile();
                        showAlert(Alert.AlertType.INFORMATION, "Success", null, "New patient added successfully.");
                    } catch (NumberFormatException ex) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Input", null,
                                "Please enter a valid number for Age.");
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Error", null,
                                "Error adding patient: " + ex.getMessage());
                    }
                }
            });
        });

        // Edit functionality (unchanged)
        editButton.setOnAction(e -> {
            JSONObject selectedPatient = patientTable.getSelectionModel().getSelectedItem();
            if (selectedPatient == null) {
                showAlert(Alert.AlertType.WARNING, "Warning", null, "Please select a patient to edit.");
                return;
            }

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edit Patient Results");
            dialog.setHeaderText("Editing: " + selectedPatient.getString("name"));

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));

            TextField bloodTestField = new TextField(selectedPatient.getJSONObject("lab_results").getString("blood_test"));
            TextField xrayField = new TextField(selectedPatient.getJSONObject("lab_results").getString("x_ray"));

            grid.add(new Label("Blood Test Results:"), 0, 0);
            grid.add(bloodTestField, 1, 0);
            grid.add(new Label("X-Ray Results:"), 0, 1);
            grid.add(xrayField, 1, 1);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        JSONObject labResults = selectedPatient.getJSONObject("lab_results");
                        labResults.put("blood_test", bloodTestField.getText());
                        labResults.put("x_ray", xrayField.getText());
                        savePatientDataToFile(); // Save to file
                        patientTable.refresh(); // Refresh TableView to reflect changes
                        showAlert(Alert.AlertType.INFORMATION, "Success", null, "Patient data updated successfully.");
                    } catch (Exception ex) {
                        showAlert(Alert.AlertType.ERROR, "Error", null, "Error updating patient: " + ex.getMessage());
                    }
                }
            });
        });

        // Reload Data functionality
        reloadButton.setOnAction(e -> {
            try {
                // Clear existing data
                patientData.clear();
                // Reload data from file
                loadPatientDataFromFile();
                // Update the TableView
                patientTable.setItems(patientData);
                showAlert(Alert.AlertType.INFORMATION, "Success", null, "Data reloaded successfully from file.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", null, "Error reloading data: " + ex.getMessage());
            }
        });

        // Initial display of all data
        labPane.getChildren().addAll(welcomeLabel, searchBox, patientTable, buttonBox);
        createLogoutButton(labPane);

        Scene labScene = new Scene(labPane, 900, 700);
        primaryStage.setScene(labScene);
        primaryStage.setTitle("HMS - Lab Technician Dashboard");
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void createLogoutButton(VBox parent) {
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> {
            primaryStage.close();
            DrawUI drawUI = new DrawUI();
            try {
                Stage newStage = new Stage();
                drawUI.create(newStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        parent.getChildren().add(logoutButton);
    }
}