# Integrated Assignment Environment (IAE)

A lightweight tool for managing programming assignments. This application allows lecturers to:

1. Create and manage programming language configurations
2. Create projects for assignments
3. Compile and run student submissions
4. Compare outputs with expected results
5. Generate reports on student performance

## Features

- Support for both compiled and interpreted languages
- Customizable compiler/interpreter settings
- Command-line argument configuration
- Expected output comparison
- Detailed reporting of compilation and execution results

## Requirements

- Java 11 or later
- Maven (for building)

## Building the Project

```bash
cd IAE
mvn clean package
```

## Running the Application

```bash
java -jar target/integrated-assignment-environment-1.0-SNAPSHOT.jar
```

## Using the Application

### Creating a Configuration

1. Click on "Manage Configurations"
2. Click "New Configuration"
3. Enter the configuration details:
   - Name: A descriptive name for the configuration
   - Compiler Path: Path to the compiler/interpreter
   - Compiler Arguments: Each argument on a separate line
   - Run Command: Command to run the compiled program (for compiled languages)
   - Is Interpreted: Check for interpreted languages

### Creating a Project

1. Click "New Project" on the main screen
2. Enter the project details:
   - Project Name: A descriptive name for the assignment
   - Configuration: Select from the dropdown
   - Main File Name: The name of the main source file to compile/run
   - Command Line Arguments: Each argument on a separate line
   - Expected Output: The expected output for comparison
   - Submissions Directory: Directory containing student submissions

### Running a Project

1. Select a project from the list
2. Click "Run Project"
3. Verify or change the submissions directory
4. Click "Run" to process all submissions
5. View results in the table and details panel 