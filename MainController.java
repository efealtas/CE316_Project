package com.iae.controller;

import com.iae.model.Configuration;
import com.iae.model.Project;
import com.iae.service.ConfigurationService;
import com.iae.service.ProjectService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    
    @FXML
    private ListView<Project> projectListView;
    
    @FXML
    private Button newProjectButton;
    
    @FXML
    private Button editProjectButton;
    
    @FXML
    private Button deleteProjectButton;
    
    @FXML
    private Button manageConfigsButton;
    
    @FXML
    private Button runProjectButton;
    
    private Stage primaryStage;
    private ProjectService projectService;
    private ConfigurationService configurationService;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        projectService = new ProjectService();
        configurationService = new ConfigurationService();
        
        refreshProjects();
        
        //  button actions
        newProjectButton.setOnAction(event -> openNewProjectDialog());
        editProjectButton.setOnAction(event -> editSelectedProject());
        deleteProjectButton.setOnAction(event -> deleteSelectedProject());
        manageConfigsButton.setOnAction(event -> openConfigurationsDialog());
        runProjectButton.setOnAction(event -> runSelectedProject());
        
        //button states
        projectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean hasSelection = newValue != null;
            editProjectButton.setDisable(!hasSelection);
            deleteProjectButton.setDisable(!hasSelection);
            runProjectButton.setDisable(!hasSelection);
        });
        
        //double-click to edit project
        projectListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && projectListView.getSelectionModel().getSelectedItem() != null) {
                editSelectedProject();
            }
        });
    }
    
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    private void refreshProjects() {
        projectListView.getItems().clear();
        projectListView.getItems().addAll(projectService.getProjects());
    }
    
    private void openNewProjectDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/project_dialog.fxml"));
            Parent root = loader.load();
            
            ProjectDialogController controller = loader.getController();
            controller.setConfigurationService(configurationService);
            controller.setProjectService(projectService);
            
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            stage.setTitle("New Project");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            refreshProjects();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening project dialog", e.getMessage());
        }
    }
    
    private void editSelectedProject() {
        Project selectedProject = projectListView.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/project_dialog.fxml"));
            Parent root = loader.load();
            
            ProjectDialogController controller = loader.getController();
            controller.setConfigurationService(configurationService);
            controller.setProjectService(projectService);
            controller.loadProject(selectedProject);
            
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            stage.setTitle("Edit Project");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            refreshProjects();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening project dialog", e.getMessage());
        }
    }
    
    private void deleteSelectedProject() {
        Project selectedProject = projectListView.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Project");
        alert.setHeaderText("Delete Project");
        alert.setContentText("Are you sure you want to delete the project: " + selectedProject.getName() + "?");
        
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    projectService.removeProject(selectedProject);
                    refreshProjects();
                });
    }
    
    private void openConfigurationsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/configurations.fxml"));
            Parent root = loader.load();
            
            ConfigurationsController controller = loader.getController();
            controller.setConfigurationService(configurationService);
            
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            stage.setTitle("Manage Configurations");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening configurations dialog", e.getMessage());
        }
    }
    
    private void runSelectedProject() {
        Project selectedProject = projectListView.getSelectionModel().getSelectedItem();
        if (selectedProject == null) {
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/run_project.fxml"));
            Parent root = loader.load();
            
            RunProjectController controller = loader.getController();
            controller.setProject(selectedProject);
            controller.setProjectService(projectService);
            
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryStage);
            stage.setTitle("Run Project: " + selectedProject.getName());
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Refresh project to save results
            refreshProjects();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening run project dialog", e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 