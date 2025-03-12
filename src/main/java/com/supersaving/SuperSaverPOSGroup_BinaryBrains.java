package com.supersaving;

import com.supersaving.service.DatabaseService;
import com.supersaving.service.POSService;
import com.supersaving.ui.ConsoleUI;

public class SuperSaverPOSGroup_BinaryBrains {
    public static void main(String[] args) {
        System.out.println("Initializing POS System...");
        try {
            DatabaseService dbService = new DatabaseService();
            POSService posService = new POSService(dbService);
            ConsoleUI consoleUI = new ConsoleUI(posService);
            
            System.out.println("POS System initialized successfully!");
            consoleUI.start();
        } catch (Exception e) {
            System.err.println("Error initializing POS system: " + e.getMessage());
            e.printStackTrace();
        }
    }
}