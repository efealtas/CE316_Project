package com.iae.controller;

import com.iae.model.Configuration;
import com.iae.model.Project;
import com.iae.service.ConfigurationService;
import com.iae.service.ProjectService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ProjectDialogController implements Initializable {
    
    @FXML
    private TextField projectNameField;
    
    @FXML
    private ComboBox<Configuration> configurationComboBox;
    
    @FXML
    private TextField mainFileNameField;
    
    @FXML
    private TextArea commandLineArgsField;
    
    @FXML
    private TextArea expectedOutputField;
    
    @FXML
    private TextField submissionsDirField;
    
    @FXML
    private Button browseSubmissionsDirButton;
    
    @FXML
    private Button browseExpectedOutputButton;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Label validationLabel;
    
    private ConfigurationService configurationService;
    private ProjectService projectService;
    private Project project;
    private boolean isEditMode = false;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //button actions
        browseSubmissionsDirButton.setOnAction(event -> browseSubmissionsDir());
        browseExpectedOutputButton.setOnAction(event -> browseExpectedOutputFile());
        saveButton.setOnAction(event -> saveProject());
        cancelButton.setOnAction(event -> ((Stage) cancelButton.getScene().getWindow()).close());
        
        //field validations
        projectNameField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        mainFileNameField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        configurationComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());
        submissionsDirField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        
        validateForm();
    }
    
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        configurationComboBox.getItems().addAll(configurationService.getConfigurations());
    }
    
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }
    
    public void loadProject(Project project) {
        this.project = project;
        this.isEditMode = true;
        
        projectNameField.setText(project.getName());
        configurationComboBox.setValue(project.getConfiguration());
        mainFileNameField.setText(project.getMainFileName());
        commandLineArgsField.setText(String.join("\n", project.getCommandLineArguments()));
        expectedOutputField.setText(project.getExpectedOutput());
        
        if (project.getSubmissionsDirectory() != null) {
            submissionsDirField.setText(project.getSubmissionsDirectory().toString());
        }
        
        validateForm();
    }
    
    private void browseSubmissionsDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Submissions Directory");
        
        //if path already exists
        if (submissionsDirField.getText() != null && !submissionsDirField.getText().isEmpty()) {
            File currentDir = new File(submissionsDirField.getText());
            if (currentDir.exists() && currentDir.isDirectory()) {
                directoryChooser.setInitialDirectory(currentDir);
            }
        }
        
        File selectedDir = directoryChooser.showDialog(submissionsDirField.getScene().getWindow());
        if (selectedDir != null) {
            submissionsDirField.setText(selectedDir.getAbsolutePath());
            validateForm();
        }
    }
    
    private void browseExpectedOutputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Expected Output File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        
        File selectedFile = fileChooser.showOpenDialog(expectedOutputField.getScene().getWindow());
        if (selectedFile != null) {
            try {
                String content = new String(Files.readAllBytes(selectedFile.toPath()));
                expectedOutputField.setText(content);
            } catch (Exception e) {
                showError("Error reading file", e.getMessage());
            }
        }
    }
    
    private void saveProject() {
        if (!validateForm()) {
            showError("Validation Error", "Please fix the validation errors before saving.");
            return;
        }
        
        try {
            if (project == null) {
                project = new Project();
            }
            
            project.setName(projectNameField.getText().trim());
            project.setConfiguration(configurationComboBox.getValue());
            project.setMainFileName(mainFileNameField.getText().trim());
            
            // Parse command line arguments 

            List<String> arguments = Arrays.stream(commandLineArgsField.getText().split("\\n"))
                    .map(String::trim)
                    .filter(arg -> !arg.isEmpty())
                    .collect(Collectors.toList());
            project.setCommandLineArguments(arguments);
            
            project.setExpectedOutput(expectedOutputField.getText());
            
            if (submissionsDirField.getText() != null && !submissionsDirField.getText().isEmpty()) {
                Path submissionsPath = Paths.get(submissionsDirField.getText());
                if (!Files.exists(submissionsPath) || !Files.isDirectory(submissionsPath)) {
                    showError("Invalid Directory", "The submissions directory does not exist or is not a directory.");
                    return;
                }
                project.setSubmissionsDirectory(submissionsPath);
            }
            
            if (isEditMode) {
                projectService.updateProject(project);
            } else {
                projectService.addProject(project);
            }
            
            ((Stage) saveButton.getScene().getWindow()).close();
        } catch (Exception e) {
            showError("Error", "Failed to save project: " + e.getMessage());
        }
    }
    
    private boolean validateForm() {
        StringBuilder validationMessage = new StringBuilder();
        boolean isValid = true;
        
        // validate project name
        if (projectNameField.getText().trim().isEmpty()) {
            validationMessage.append("• Project name is required\n");
            isValid = false;
        }
        
        // validating main file name
        if (mainFileNameField.getText().trim().isEmpty()) {
            validationMessage.append("• Main file name is required\n");
            isValid = false;
        }
        
        // validate configuration
        if (configurationComboBox.getValue() == null) {
            validationMessage.append("• Configuration must be selected\n");
            isValid = false;
        }
        
        // validate submissions directory if given
        if (!submissionsDirField.getText().trim().isEmpty()) {
            Path submissionsPath = Paths.get(submissionsDirField.getText());
            if (!Files.exists(submissionsPath) || !Files.isDirectory(submissionsPath)) {
                validationMessage.append("• Submissions directory must be a valid directory\n");
                isValid = false;
            }
        }
        
        // update validation massage
        if (!isValid) {
            validationLabel.setText(validationMessage.toString());
            validationLabel.setStyle("-fx-text-fill: red;");
        } else {
            validationLabel.setText("");
        }
        
        saveButton.setDisable(!isValid);
        return isValid;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 