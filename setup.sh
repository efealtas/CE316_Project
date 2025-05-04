#!/bin/bash

# Create lib directory if it doesn't exist
mkdir -p lib

# Download JavaFX SDK if not present
if [ ! -d "lib/javafx-sdk" ]; then
    echo "Downloading JavaFX SDK..."
    curl -L https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_osx-x64_bin-sdk.zip -o javafx.zip
    unzip javafx.zip -d lib/
    mv lib/javafx-sdk-21.0.2 lib/javafx-sdk
    rm javafx.zip
fi

# Compile the application
echo "Compiling application..."
javac --module-path lib/javafx-sdk/lib \
      --add-modules javafx.controls,javafx.fxml \
      -d target/classes \
      src/main/java/com/iae/IAEApplication.java \
      src/main/java/com/iae/IAELauncher.java \
      src/main/java/com/iae/controller/*.java \
      src/main/java/com/iae/model/*.java \
      src/main/java/com/iae/service/*.java

# Create manifest
echo "Creating manifest..."
mkdir -p target/META-INF
echo "Main-Class: com.iae.IAELauncher" > target/META-INF/MANIFEST.MF

# Create JAR
echo "Creating JAR..."
jar cfm target/iae.jar target/META-INF/MANIFEST.MF -C target/classes .

echo "Setup complete! Run the application using: ./run.sh" 