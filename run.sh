#!/bin/bash

# Clean and package the application
mvn clean package

# Run the application with JavaFX modules
java --module-path lib/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/iae.jar 