package com.iae.model;

import java.io.Serializable;


//   Represents the result of compiling and running a student's assignment

public class StudentResult implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Fields
    private String studentId;
    private Project project;
    
    // result fields
    private boolean compilationSuccess;
    private String compilationOutput;
    private boolean executionSuccess;
    private String executionOutput;
    private boolean outputMatches;
    private String errorMessage;

    public StudentResult(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public boolean isCompilationSuccess() {
        return compilationSuccess;
    }

    public void setCompilationSuccess(boolean compilationSuccess) {
        this.compilationSuccess = compilationSuccess;
    }

    public String getCompilationOutput() {
        return compilationOutput;
    }

    public void setCompilationOutput(String compilationOutput) {
        this.compilationOutput = compilationOutput;
    }

    public boolean isExecutionSuccess() {
        return executionSuccess;
    }

    public void setExecutionSuccess(boolean executionSuccess) {
        this.executionSuccess = executionSuccess;
    }

    public String getExecutionOutput() {
        return executionOutput;
    }

    public void setExecutionOutput(String executionOutput) {
        this.executionOutput = executionOutput;
    }

    public boolean isOutputMatches() {
        return outputMatches;
    }

    public void setOutputMatches(boolean outputMatches) {
        this.outputMatches = outputMatches;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Business Logic
    public boolean isPassed() {
        if (project != null && project.getConfiguration().isInterpreted()) {
            // For interpreted languages, only check execution and output
            return executionSuccess && outputMatches;
        }
        // For compiled languages, check all criteria
        return compilationSuccess && executionSuccess && outputMatches;
    }

    @Override
    public String toString() {
        return "StudentResult{" +
                "studentId='" + studentId + '\'' +
                ", passed=" + isPassed() +
                '}';
    }
} 