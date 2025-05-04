package com.iae.controller;

import com.iae.model.Configuration;
import com.iae.service.ConfigurationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ConfigDialogController implements Initializable {
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField compilerPathField;
    
    @FXML
    private Button browseCompilerButton;
    
    @FXML
    private TextArea compilerArgsField;
    
    @FXML
    private TextField runCommandField;
    
    @FXML
    private CheckBox interpretedCheckbox;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private ConfigurationService configurationService;
    private Configuration configuration;
    private boolean isEditMode = false;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //button actions
        browseCompilerButton.setOnAction(event -> browseCompilerPath());
        saveButton.setOnAction(event -> saveConfiguration());
        cancelButton.setOnAction(event -> ((Stage) cancelButton.getScene().getWindow()).close());
        
        //field validations
        nameField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        compilerPathField.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
        
        // not showing run command when interpreted
        interpretedCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            runCommandField.setDisable(newValue);
        });
        
        validateForm();
    }
    
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
    
    public void loadConfiguration(Configuration configuration) {
        this.configuration = configuration;
        this.isEditMode = true;
        
        nameField.setText(configuration.getName());
        compilerPathField.setText(configuration.getCompilerPath());
        compilerArgsField.setText(String.join("\n", configuration.getCompilerArguments()));
        runCommandField.setText(configuration.getRunCommand());
        interpretedCheckbox.setSelected(configuration.isInterpreted());
        runCommandField.setDisable(configuration.isInterpreted());
    }
    
    private void browseCompilerPath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Compiler/Interpreter");
        
        //if path already exists
        if (compilerPathField.getText() != null && !compilerPathField.getText().isEmpty()) {
            File currentPath = new File(compilerPathField.getText());
            if (currentPath.getParentFile() != null && currentPath.getParentFile().exists()) {
                fileChooser.setInitialDirectory(currentPath.getParentFile());
            }
        }
        
        File selectedFile = fileChooser.showOpenDialog(compilerPathField.getScene().getWindow());
        if (selectedFile != null) {
            compilerPathField.setText(selectedFile.getAbsolutePath());
        }
    }
    
    private void saveConfiguration() {
        if (!validateForm()) {
            return;
        }
        
        if (configuration == null) {
            configuration = new Configuration();
        }
        
        configuration.setName(nameField.getText().trim());
        configuration.setCompilerPath(compilerPathField.getText().trim());
        
        // parse compiler arguments
        List<String> arguments = Arrays.stream(compilerArgsField.getText().split("\\n"))
                .map(String::trim)
                .filter(arg -> !arg.isEmpty())
                .collect(Collectors.toList());
        configuration.setCompilerArguments(arguments);
        
        configuration.setRunCommand(runCommandField.getText().trim());
        configuration.setInterpreted(interpretedCheckbox.isSelected());
        
        if (isEditMode) {
            configurationService.updateConfiguration(configuration);
        } else {
            configurationService.addConfiguration(configuration);
        }
        
        ((Stage) saveButton.getScene().getWindow()).close();
    }
    
    private boolean validateForm() {
        boolean isValid = !nameField.getText().trim().isEmpty() && 
                          !compilerPathField.getText().trim().isEmpty();
        
        saveButton.setDisable(!isValid);
        return isValid;
    }
} 