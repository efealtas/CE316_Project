package com.iae.service;

import com.iae.model.Configuration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing configurations
 */
public class ConfigurationService {
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".iae" + File.separator + "configurations";
    private List<Configuration> configurations;

    public ConfigurationService() {
        configurations = new ArrayList<>();
        loadConfigurations();
    }

    public List<Configuration> getConfigurations() {
        return configurations;
    }

    public void addConfiguration(Configuration configuration) {
        configurations.add(configuration);
        saveConfigurations();
    }

    public void updateConfiguration(Configuration configuration) {
        saveConfigurations();
    }

    public void removeConfiguration(Configuration configuration) {
        configurations.remove(configuration);
        saveConfigurations();
    }

    public Configuration getConfigurationByName(String name) {
        return configurations.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void loadConfigurations() {
        Path configDir = Paths.get(CONFIG_DIR);
        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            Files.list(configDir)
                    .filter(path -> path.toString().endsWith(".cfg"))
                    .forEach(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(
                                new FileInputStream(path.toFile()))) {
                            Configuration configuration = (Configuration) ois.readObject();
                            configurations.add(configuration);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveConfigurations() {
        Path configDir = Paths.get(CONFIG_DIR);
        if (!Files.exists(configDir)) {
            try {
                Files.createDirectories(configDir);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // Clear directory first
        try {
            Files.list(configDir)
                    .filter(path -> path.toString().endsWith(".cfg"))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            // Save all configurations
            for (Configuration configuration : configurations) {
                String filename = configuration.getName().replaceAll("[^a-zA-Z0-9]", "_") + ".cfg";
                Path filePath = configDir.resolve(filename);
                
                try (ObjectOutputStream oos = new ObjectOutputStream(
                        new FileOutputStream(filePath.toFile()))) {
                    oos.writeObject(configuration);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 