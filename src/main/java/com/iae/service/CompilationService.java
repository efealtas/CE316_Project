package com.iae.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.iae.model.Configuration;
import com.iae.model.Project;
import com.iae.model.StudentResult;


 //service class for compiling and executing code
 
public class CompilationService {
    
    /**
     Compiles and runs a students submission 
     @param project The project configuration
     @param studentDir The directory containing the students submission
     @return The result of compilation and execution
     */
    public StudentResult compileAndRun(Project project, Path studentDir) {
        String studentId = studentDir.getFileName().toString();
        StudentResult result = new StudentResult(studentId);
        result.setProject(project);
        
        Configuration config = project.getConfiguration();
        String mainFileName = project.getMainFileName();
        
        // Check if main file exists
        Path mainFilePath = studentDir.resolve(mainFileName);
        if (!Files.exists(mainFilePath)) {
            result.setCompilationSuccess(false);
            result.setErrorMessage("Main file not found: " + mainFileName);
            return result;
        }
        
        // Compile the code if its not interpreted
        if (!config.isInterpreted()) {
            boolean compileSuccess = compile(config, studentDir, mainFilePath, result);
            if (!compileSuccess) {
                return result;
            }
        }
        
        // Run the program
        boolean runSuccess = runProgram(project, config, studentDir, project.getCommandLineArguments(), result);
        if (!runSuccess) {
            return result;
        }
        
        // Compare output
        compareOutput(project.getExpectedOutput(), result.getExecutionOutput(), result);
        
        return result;
    }
    
    
     //Compiles the source code
     
    private boolean compile(Configuration config, Path workingDir, Path mainFilePath, StudentResult result) {
        List<String> command = new ArrayList<>();
        command.add(config.getCompilerPath());
        command.addAll(config.getCompilerArguments());
        command.add(mainFilePath.toString());
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDir.toFile());
        
        try {
            Process process = processBuilder.start();
            boolean completed = process.waitFor(30, TimeUnit.SECONDS);
            
            if (!completed) {
                process.destroyForcibly();
                result.setCompilationSuccess(false);
                result.setErrorMessage("Compilation timed out");
                return false;
            }
            
            int exitCode = process.exitValue();
            result.setCompilationSuccess(exitCode == 0);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String compilationOutput = reader.lines().collect(Collectors.joining("\n"));
            result.setCompilationOutput(compilationOutput);
            
            if (exitCode != 0) {
                result.setErrorMessage("Compilation failed with exit code: " + exitCode);
                return false;
            }
            
            return true;
        } catch (IOException | InterruptedException e) {
            result.setCompilationSuccess(false);
            result.setErrorMessage("Error during compilation: " + e.getMessage());
            return false;
        }
    }
    
    //Runs the program
    private boolean runProgram(Project project, Configuration config, Path workingDir, List<String> args, StudentResult result) {
        List<String> command = new ArrayList<>();
        
        if (config.isInterpreted()) {
            command.add(config.getCompilerPath());
            command.add(workingDir.resolve(project.getMainFileName()).toString());
        } else {
            command.add(config.getRunCommand());
        }
        
        command.addAll(args);
        
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDir.toFile());
        
        try {
            Process process = processBuilder.start();
            boolean completed = process.waitFor(30, TimeUnit.SECONDS);
            
            if (!completed) {
                process.destroyForcibly();
                result.setExecutionSuccess(false);
                result.setErrorMessage("Execution timed out");
                return false;
            }
            
            int exitCode = process.exitValue();
            result.setExecutionSuccess(exitCode == 0);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String executionOutput = reader.lines().collect(Collectors.joining("\n"));
            result.setExecutionOutput(executionOutput);
            
            if (exitCode != 0) {
                result.setErrorMessage("Execution failed with exit code: " + exitCode);
                return false;
            }
            
            return true;
        } catch (IOException | InterruptedException e) {
            result.setExecutionSuccess(false);
            result.setErrorMessage("Error during execution: " + e.getMessage());
            return false;
        }
    }
    
   //  Compares the actual output with the expected output

    private void compareOutput(String expected, String actual, StudentResult result) {
        if (expected == null || actual == null) {
            result.setOutputMatches(false);
            return;
        }
        
        // line endings and trim whitespace
        expected = expected.replaceAll("\\r\\n", "\n").trim();
        actual = actual.replaceAll("\\r\\n", "\n").trim();
        
        result.setOutputMatches(expected.equals(actual));
    }
    
    //Processes all student submissions in the project
    public List<StudentResult> processStudentSubmissions(Project project) {
        List<StudentResult> results = new ArrayList<>();
        Path submissionsDir = project.getSubmissionsDirectory();
        
        if (submissionsDir == null || !Files.exists(submissionsDir)) {
            return results;
        }
        
        try {
            Files.list(submissionsDir)
                    .filter(Files::isDirectory)
                    .forEach(studentDir -> {
                        try {
                            StudentResult result = compileAndRun(project, studentDir);
                            results.add(result);
                        } catch (Exception e) {
                            StudentResult result = new StudentResult(studentDir.getFileName().toString());
                            result.setCompilationSuccess(false);
                            result.setErrorMessage("Error processing submission: " + e.getMessage());
                            results.add(result);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return results;
    }
} 