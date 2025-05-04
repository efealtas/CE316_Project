package com.iae.model;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


 //Represents a project containing assignment configuration and results
 
public class Project implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private Configuration configuration;
    private String mainFileName;
    private List<String> commandLineArguments;
    private String expectedOutput;
    private String submissionsDirectoryPath;
    private List<StudentResult> results;

    public Project() {
        this.commandLineArguments = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    public Project(String name, Configuration configuration, String mainFileName) {
        this.name = name;
        this.configuration = configuration;
        this.mainFileName = mainFileName;
        this.commandLineArguments = new ArrayList<>();
        this.results = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getMainFileName() {
        return mainFileName;
    }

    public void setMainFileName(String mainFileName) {
        this.mainFileName = mainFileName;
    }

    public List<String> getCommandLineArguments() {
        return commandLineArguments;
    }

    public void setCommandLineArguments(List<String> commandLineArguments) {
        this.commandLineArguments = commandLineArguments;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public void setExpectedOutput(String expectedOutput) {
        this.expectedOutput = expectedOutput;
    }

    public Path getSubmissionsDirectory() {
        return submissionsDirectoryPath != null ? Paths.get(submissionsDirectoryPath) : null;
    }

    public void setSubmissionsDirectory(Path submissionsDirectory) {
        this.submissionsDirectoryPath = submissionsDirectory != null ? submissionsDirectory.toString() : null;
    }

    public List<StudentResult> getResults() {
        return results;
    }

    public void setResults(List<StudentResult> results) {
        this.results = results;
    }

    public void addResult(StudentResult result) {
        this.results.add(result);
    }

    @Override
    public String toString() {
        return name;
    }
} 