package com.iae.controller;

import com.iae.model.Project;
import com.iae.model.StudentResult;
import com.iae.service.CompilationService;
import com.iae.service.ProjectService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class RunProjectController implements Initializable {
    
    @FXML
    private Label projectNameLabel;
    
    @FXML
    private Label configurationLabel;
    
    @FXML
    private TextField submissionsDirField;
    
    @FXML
    private Button browseButton;
    
    @FXML
    private Button runButton;
    
    @FXML
    private Button closeButton;
    
    @FXML
    private TableView<StudentResult> resultsTable;
    
    @FXML
    private TableColumn<StudentResult, String> studentIdColumn;
    
    @FXML
    private TableColumn<StudentResult, Boolean> compilationSuccessColumn;
    
    @FXML
    private TableColumn<StudentResult, Boolean> executionSuccessColumn;
    
    @FXML
    private TableColumn<StudentResult, Boolean> outputMatchesColumn;
    
    @FXML
    private TableColumn<StudentResult, Boolean> passedColumn;
    
    @FXML
    private TextArea detailsArea;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Label statusLabel;
    
    private Project project;
    private ProjectService projectService;
    private CompilationService compilationService;
    private ObservableList<StudentResult> resultsList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //button actions
        browseButton.setOnAction(event -> browseSubmissionsDir());
        runButton.setOnAction(event -> runProject());
        closeButton.setOnAction(event -> ((Stage) closeButton.getScene().getWindow()).close());
        
        //table columns
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        compilationSuccessColumn.setCellValueFactory(new PropertyValueFactory<>("compilationSuccess"));
        executionSuccessColumn.setCellValueFactory(new PropertyValueFactory<>("executionSuccess"));
        outputMatchesColumn.setCellValueFactory(new PropertyValueFactory<>("outputMatches"));
        passedColumn.setCellValueFactory(new PropertyValueFactory<>("passed"));
        
        //selection listener for details
        resultsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showResultDetails(newValue);
            } else {
                detailsArea.clear();
            }
        });
        
        //compilation service
        compilationService = new CompilationService();
        
        // initialize table
        resultsTable.setItems(resultsList);
        
        // update button states based on directory field
        submissionsDirField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateSubmissionsDirectory();
        });
    }
    
    private void validateSubmissionsDirectory() {
        String dirPath = submissionsDirField.getText();
        if (dirPath == null || dirPath.trim().isEmpty()) {
            runButton.setDisable(true);
            return;
        }
        
        Path path = Paths.get(dirPath);
        boolean isValid = Files.exists(path) && Files.isDirectory(path);
        runButton.setDisable(!isValid);
        
        if (!isValid) {
            statusLabel.setText("Please select a valid submissions directory");
        } else {
            statusLabel.setText("");
        }
    }
    
    public void setProject(Project project) {
        this.project = project;
        
        projectNameLabel.setText(project.getName());
        configurationLabel.setText(project.getConfiguration().getName());
        
        if (project.getSubmissionsDirectory() != null) {
            submissionsDirField.setText(project.getSubmissionsDirectory().toString());
            validateSubmissionsDirectory();
        }
        
        // load existing results if available
        if (project.getResults() != null && !project.getResults().isEmpty()) {
            resultsList.addAll(project.getResults());
        }
    }
    
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }
    
    private void browseSubmissionsDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Submissions Directory");
        
        // set initial directory if path already exists
        if (submissionsDirField.getText() != null && !submissionsDirField.getText().isEmpty()) {
            File currentDir = new File(submissionsDirField.getText());
            if (currentDir.exists() && currentDir.isDirectory()) {
                directoryChooser.setInitialDirectory(currentDir);
            }
        }
        
        File selectedDir = directoryChooser.showDialog(submissionsDirField.getScene().getWindow());
        if (selectedDir != null) {
            submissionsDirField.setText(selectedDir.getAbsolutePath());
            validateSubmissionsDirectory();
        }
    }
    
    private void runProject() {
        if (submissionsDirField.getText() == null || submissionsDirField.getText().trim().isEmpty()) {
            showError("Error", "Please select a submissions directory");
            return;
        }
        
        Path submissionsPath = Paths.get(submissionsDirField.getText());
        if (!Files.exists(submissionsPath) || !Files.isDirectory(submissionsPath)) {
            showError("Error", "Invalid submissions directory");
            return;
        }
        
        // update project with submissions directory
        project.setSubmissionsDirectory(submissionsPath);
        
        // clearing previous results
        resultsList.clear();
        project.getResults().clear();
        
        // updating UI state
        runButton.setDisable(true);
        browseButton.setDisable(true);
        progressBar.setVisible(true);
        statusLabel.setText("Processing submissions...");
        
        // run compilation  in background
        CompletableFuture.supplyAsync(() -> compilationService.processStudentSubmissions(project))
                .thenAccept(results -> Platform.runLater(() -> {
                    // update UI with results
                    resultsList.addAll(results);
                    project.setResults(results);
                    
                    //save project with results
                    projectService.updateProject(project);
                    
                    //update UI state
                    runButton.setDisable(false);
                    browseButton.setDisable(false);
                    progressBar.setVisible(false);
                    statusLabel.setText("Processing complete. " + results.size() + " submissions processed.");
                }))
                .exceptionally(throwable -> {
                    Platform.runLater(() -> {
                        showError("Error", "Failed to process submissions: " + throwable.getMessage());
                        runButton.setDisable(false);
                        browseButton.setDisable(false);
                        progressBar.setVisible(false);
                        statusLabel.setText("Processing failed");
                    });
                    return null;
                });
    }
    
    private void showResultDetails(StudentResult result) {
        StringBuilder details = new StringBuilder();
        
        details.append("Student ID: ").append(result.getStudentId()).append("\n\n");
        
        if (result.getErrorMessage() != null && !result.getErrorMessage().isEmpty()) {
            details.append("Error: ").append(result.getErrorMessage()).append("\n\n");
        }
        
        details.append("Compilation Success: ").append(result.isCompilationSuccess()).append("\n");
        if (result.getCompilationOutput() != null && !result.getCompilationOutput().isEmpty()) {
            details.append("Compilation Output:\n").append(result.getCompilationOutput()).append("\n\n");
        }
        
        details.append("Execution Success: ").append(result.isExecutionSuccess()).append("\n");
        if (result.getExecutionOutput() != null) {
            details.append("Execution Output:\n").append(result.getExecutionOutput()).append("\n\n");
        }
        
        details.append("Output Matches Expected: ").append(result.isOutputMatches()).append("\n");
        details.append("Overall Status: ").append(result.isPassed() ? "PASSED" : "FAILED").append("\n");
        
        detailsArea.setText(details.toString());
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 