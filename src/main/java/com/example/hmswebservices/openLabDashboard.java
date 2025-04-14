//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//
//public static void openLabDashboard(Stage primaryStage, String username) {
//    VBox labPane = new VBox(20);
//    labPane.setPadding(new Insets(20));
//    labPane.setAlignment(Pos.CENTER);
//    labPane.setStyle("-fx-background-color: #eaf2f8;");
//
//    Label welcomeLabel = new Label("Welcome Lab Technician " + username);
//    welcomeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #2980b9;");
//
//    // Search components
//    HBox searchBox = new HBox(10);
//    TextField searchField = new TextField();
//    searchField.setPromptText("Search by Patient ID or Name");
//    searchField.setPrefWidth(300);
//    Button searchButton = new Button("Search");
//    searchBox.setAlignment(Pos.CENTER);
//    searchBox.getChildren().addAll(searchField, searchButton);
//
//    // Results display
//    TextArea resultsArea = new TextArea();
//    resultsArea.setEditable(false);
//    resultsArea.setPrefRowCount(10);
//    resultsArea.setStyle("-fx-font-family: monospace;");
//
//    // Sample JSON data (in a real app, this would be loaded from a file)
//    String jsonData = "[{\"patient_Id\":\"1001\",\"name\":\"Ahmed Ali\",\"age\":35,\"gender\":\"Male\",\"lab_results\":{\"blood_test\":\"Normal\",\"x_ray\":\"No issues found\"}},{\"patient_Id\":\"1002\",\"name\":\"Fatima Mohammed\",\"age\":28,\"gender\":\"Female\",\"lab_results\":{\"blood_test\":\"High sugar level\",\"x_ray\":\"Fracture detected\"}}]";
//
//
//    // Action buttons
//    HBox buttonBox = new HBox(20);
//    Button addButton = new Button("Add New Patient");
//    Button editButton = new Button("Edit Results");
//    buttonBox.setAlignment(Pos.CENTER);
//    buttonBox.getChildren().addAll(addButton, editButton);
//
//    // Search functionality
//    searchButton.setOnAction(e -> {
//        String searchTerm = searchField.getText().toLowerCase();
//        if (searchTerm.isEmpty()) {
//            resultsArea.setText(jsonData); // Show all data if search is empty
//            return;
//        }
//
//        try {
//            JSONArray patients = new JSONArray(jsonData);
//            StringBuilder foundPatients = new StringBuilder();
//
//            for (int i = 0; i < patients.length(); i++) {
//                JSONObject patient = patients.getJSONObject(i);
//                String patientId = patient.getString("patient_Id");
//                String name = patient.getString("name").toLowerCase();
//
//                if (patientId.contains(searchTerm) || name.contains(searchTerm)) {
//                    foundPatients.append(patient.toString(4)).append("\n\n");
//                }
//            }
//
//            if (foundPatients.length() == 0) {
//                resultsArea.setText("No patients found matching: " + searchTerm);
//            } else {
//                resultsArea.setText(foundPatients.toString());
//            }
//        } catch (Exception ex) {
//            resultsArea.setText("Error processing data: " + ex.getMessage());
//        }
//    });
//
//    // Add new patient functionality
//    addButton.setOnAction(e -> {
//        // Create a dialog to add new patient
//        Dialog<ButtonType> dialog = new Dialog<>();
//        dialog.setTitle("Add New Patient");
//        dialog.setHeaderText("Enter patient details");
//
//        // Create form fields
//        GridPane grid = new GridPane();
//        grid.setHgap(10);
//        grid.setVgap(10);
//        grid.setPadding(new Insets(20));
//
//        TextField idField = new TextField();
//        idField.setPromptText("Patient ID");
//        TextField nameField = new TextField();
//        nameField.setPromptText("Name");
//        TextField ageField = new TextField();
//        ageField.setPromptText("Age");
//        ComboBox<String> genderCombo = new ComboBox<>();
//        genderCombo.getItems().addAll("Male", "Female", "Other");
//        TextField bloodTestField = new TextField();
//        bloodTestField.setPromptText("Blood Test Results");
//        TextField xrayField = new TextField();
//        xrayField.setPromptText("X-Ray Results");
//
//        grid.add(new Label("Patient ID:"), 0, 0);
//        grid.add(idField, 1, 0);
//        grid.add(new Label("Name:"), 0, 1);
//        grid.add(nameField, 1, 1);
//        grid.add(new Label("Age:"), 0, 2);
//        grid.add(ageField, 1, 2);
//        grid.add(new Label("Gender:"), 0, 3);
//        grid.add(genderCombo, 1, 3);
//        grid.add(new Label("Blood Test:"), 0, 4);
//        grid.add(bloodTestField, 1, 4);
//        grid.add(new Label("X-Ray:"), 0, 5);
//        grid.add(xrayField, 1, 5);
//
//        dialog.getDialogPane().setContent(grid);
//        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
//
//        // Process the result
//        dialog.showAndWait().ifPresent(response -> {
//            if (response == ButtonType.OK) {
//                try {
//                    JSONObject newPatient = new JSONObject();
//                    newPatient.put("patient_Id", idField.getText());
//                    newPatient.put("name", nameField.getText());
//                    newPatient.put("age", Integer.parseInt(ageField.getText()));
//                    newPatient.put("gender", genderCombo.getValue());
//
//                    JSONObject labResults = new JSONObject();
//                    labResults.put("blood_test", bloodTestField.getText());
//                    labResults.put("x_ray", xrayField.getText());
//
//                    newPatient.put("lab_results", labResults);
//
//                    // In a real app, you would save this to your JSON file
//                    resultsArea.setText("New patient added:\n" + newPatient.toString(4));
//                } catch (Exception ex) {
//                    resultsArea.setText("Error adding patient: " + ex.getMessage());
//                }
//            }
//        });
//    });
//
//    // Edit functionality
//    editButton.setOnAction(e -> {
//        String selectedText = resultsArea.getSelectedText();
//        if (selectedText == null || selectedText.isEmpty()) {
//            showAlert(Alert.AlertType.WARNING, "Warning", "No Selection",
//                    "Please select a patient's data to edit");
//            return;
//        }
//
//        try {
//            JSONObject patient = new JSONObject(selectedText);
//
//            // Create edit dialog
//            Dialog<ButtonType> dialog = new Dialog<>();
//            dialog.setTitle("Edit Patient Results");
//            dialog.setHeaderText("Editing: " + patient.getString("name"));
//
//            GridPane grid = new GridPane();
//            grid.setHgap(10);
//            grid.setVgap(10);
//            grid.setPadding(new Insets(20));
//
//            TextField bloodTestField = new TextField(
//                    patient.getJSONObject("lab_results").getString("blood_test"));
//            TextField xrayField = new TextField(
//                    patient.getJSONObject("lab_results").getString("x_ray"));
//
//            grid.add(new Label("Blood Test Results:"), 0, 0);
//            grid.add(bloodTestField, 1, 0);
//            grid.add(new Label("X-Ray Results:"), 0, 1);
//            grid.add(xrayField, 1, 1);
//
//            dialog.getDialogPane().setContent(grid);
//            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
//
//            dialog.showAndWait().ifPresent(response -> {
//                if (response == ButtonType.OK) {
//                    try {
//                        JSONObject labResults = patient.getJSONObject("lab_results");
//                        labResults.put("blood_test", bloodTestField.getText());
//                        labResults.put("x_ray", xrayField.getText());
//
//                        // In a real app, you would update the JSON file
//                        resultsArea.setText("Updated patient data:\n" + patient.toString(4));
//                    } catch (Exception ex) {
//                        resultsArea.setText("Error updating patient: " + ex.getMessage());
//                    }
//                }
//            });
//        } catch (Exception ex) {
//            resultsArea.setText("Error parsing selected data: " + ex.getMessage());
//        }
//    });
//
//    // Initial display of all data
//    resultsArea.setText(jsonData);
//
//    labPane.getChildren().addAll(
//            welcomeLabel,
//            searchBox,
//            resultsArea,
//            buttonBox
//    );
//    createLogoutButton(labPane);
//
//    Scene labScene = new Scene(labPane, 900, 700);
//    primaryStage.setScene(labScene);
//    primaryStage.setTitle("HMS - Lab Technician Dashboard");
//}
//
//
//
