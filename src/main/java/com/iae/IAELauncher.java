package com.iae;

import javafx.application.Application;

public class IAELauncher {
    
    public static void main(String[] args) {
        try {
            Application.launch(IAEApplication.class, args);
        } catch (Exception e) {
            System.err.println("Error launching application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
} 