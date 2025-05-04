package com.iae.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Represents a programming language configuration for compiling and running code

public class Configuration implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String compilerPath;
    private List<String> compilerArguments;
    private String runCommand;
    private boolean isInterpreted;

    public Configuration() {
        this.compilerArguments = new ArrayList<>();
    }

    public Configuration(String name, String compilerPath, List<String> compilerArguments, 
                         String runCommand, boolean isInterpreted) {
        this.name = name;
        this.compilerPath = compilerPath;
        this.compilerArguments = compilerArguments;
        this.runCommand = runCommand;
        this.isInterpreted = isInterpreted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompilerPath() {
        return compilerPath;
    }

    public void setCompilerPath(String compilerPath) {
        this.compilerPath = compilerPath;
    }

    public List<String> getCompilerArguments() {
        return compilerArguments;
    }

    public void setCompilerArguments(List<String> compilerArguments) {
        this.compilerArguments = compilerArguments;
    }

    public String getRunCommand() {
        return runCommand;
    }

    public void setRunCommand(String runCommand) {
        this.runCommand = runCommand;
    }

    public boolean isInterpreted() {
        return isInterpreted;
    }

    public void setInterpreted(boolean interpreted) {
        isInterpreted = interpreted;
    }

    @Override
    public String toString() {
        return name;
    }
} 