package com.iae.controller;

import com.iae.model.Configuration;
import com.iae.service.ConfigurationService;
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

public class ConfigurationsController implements Initializable {
    
    @FXML
    private ListView<Configuration> configListView;
    
    @FXML
    private Button newConfigButton;
    
    @FXML
    private Button editConfigButton;
    
    @FXML
    private Button deleteConfigButton;
    
    @FXML
    private Button closeButton;
    
    private ConfigurationService configurationService;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       
        newConfigButton.setOnAction(event -> openNewConfigDialog());
        editConfigButton.setOnAction(event -> editSelectedConfig());
        deleteConfigButton.setOnAction(event -> deleteSelectedConfig());
        closeButton.setOnAction(event -> ((Stage) closeButton.getScene().getWindow()).close());
        
        //button states
        configListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean hasSelection = newValue != null;
            editConfigButton.setDisable(!hasSelection);
            deleteConfigButton.setDisable(!hasSelection);
        });
        
        //double-click to edit configuration
        configListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && configListView.getSelectionModel().getSelectedItem() != null) {
                editSelectedConfig();
            }
        });
    }
    
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        refreshConfigurations();
    }
    
    private void refreshConfigurations() {
        configListView.getItems().clear();
        configListView.getItems().addAll(configurationService.getConfigurations());
    }
    
    private void openNewConfigDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/config_dialog.fxml"));
            Parent root = loader.load();
            
            ConfigDialogController controller = loader.getController();
            controller.setConfigurationService(configurationService);
            
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(configListView.getScene().getWindow());
            stage.setTitle("New Configuration");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            refreshConfigurations();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening configuration dialog", e.getMessage());
        }
    }
    
    private void editSelectedConfig() {
        Configuration selectedConfig = configListView.getSelectionModel().getSelectedItem();
        if (selectedConfig == null) {
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/config_dialog.fxml"));
            Parent root = loader.load();
            
            ConfigDialogController controller = loader.getController();
            controller.setConfigurationService(configurationService);
            controller.loadConfiguration(selectedConfig);
            
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(configListView.getScene().getWindow());
            stage.setTitle("Edit Configuration");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            refreshConfigurations();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error opening configuration dialog", e.getMessage());
        }
    }
    
    private void deleteSelectedConfig() {
        Configuration selectedConfig = configListView.getSelectionModel().getSelectedItem();
        if (selectedConfig == null) {
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Configuration");
        alert.setHeaderText("Delete Configuration");
        alert.setContentText("Are you sure you want to delete the configuration: " + selectedConfig.getName() + "?");
        
        alert.showAndWait()
                .filter(response -> response == ButtonType.OK)
                .ifPresent(response -> {
                    configurationService.removeConfiguration(selectedConfig);
                    refreshConfigurations();
                });
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 