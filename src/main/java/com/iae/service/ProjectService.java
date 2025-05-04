package com.iae.service;

import com.iae.model.Project;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing projects
 */
public class ProjectService {
    private static final String PROJECT_DIR = System.getProperty("user.home") + File.separator + ".iae" + File.separator + "projects";
    private List<Project> projects;

    public ProjectService() {
        System.out.println("Initializing ProjectService");
        System.out.println("Project directory: " + PROJECT_DIR);
        projects = new ArrayList<>();
        loadProjects();
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void addProject(Project project) {
        System.out.println("Adding project: " + project.getName());
        projects.add(project);
        saveProject(project);
    }

    public void updateProject(Project project) {
        System.out.println("Updating project: " + project.getName());
        saveProject(project);
    }

    public void removeProject(Project project) {
        System.out.println("Removing project: " + project.getName());
        projects.remove(project);
        deleteProject(project);
    }

    public Project getProjectByName(String name) {
        return projects.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void loadProjects() {
        System.out.println("Loading projects from: " + PROJECT_DIR);
        Path projectDir = Paths.get(PROJECT_DIR);
        if (!Files.exists(projectDir)) {
            System.out.println("Project directory does not exist, creating it");
            try {
                Files.createDirectories(projectDir);
            } catch (IOException e) {
                System.err.println("Error creating project directory: " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

        try {
            System.out.println("Scanning for project files");
            Files.list(projectDir)
                    .filter(path -> path.toString().endsWith(".proj"))
                    .forEach(path -> {
                        System.out.println("Found project file: " + path);
                        try (ObjectInputStream ois = new ObjectInputStream(
                                new FileInputStream(path.toFile()))) {
                            Project project = (Project) ois.readObject();
                            System.out.println("Loaded project: " + project.getName());
                            projects.add(project);
                        } catch (IOException | ClassNotFoundException e) {
                            System.err.println("Error loading project from " + path + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
            System.out.println("Finished loading projects. Total projects: " + projects.size());
        } catch (IOException e) {
            System.err.println("Error scanning project directory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveProject(Project project) {
        System.out.println("Saving project: " + project.getName());
        Path projectDir = Paths.get(PROJECT_DIR);
        if (!Files.exists(projectDir)) {
            System.out.println("Project directory does not exist, creating it");
            try {
                Files.createDirectories(projectDir);
            } catch (IOException e) {
                System.err.println("Error creating project directory: " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }

        String filename = project.getName().replaceAll("[^a-zA-Z0-9]", "_") + ".proj";
        Path filePath = projectDir.resolve(filename);
        System.out.println("Saving to file: " + filePath);
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(project);
            System.out.println("Project saved successfully");
        } catch (IOException e) {
            System.err.println("Error saving project: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteProject(Project project) {
        System.out.println("Deleting project file for: " + project.getName());
        Path projectDir = Paths.get(PROJECT_DIR);
        String filename = project.getName().replaceAll("[^a-zA-Z0-9]", "_") + ".proj";
        Path filePath = projectDir.resolve(filename);
        
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Project file deleted successfully");
            } else {
                System.out.println("Project file not found: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("Error deleting project file: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 