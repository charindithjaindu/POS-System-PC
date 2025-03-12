package com.supersaving.ui;

import com.supersaving.model.Bill;
import com.supersaving.service.POSService;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private POSService posService;
    private Scanner scanner;
    private Bill currentBill;

    public ConsoleUI(POSService posService) {
        this.posService = posService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    createNewBill();
                    break;
                case 2:
                    managePendingBills();
                    break;
                case 3:
                    viewCompletedBills();
                    break;
                case 4:
                    System.out.println("Thank you for using Super-Saving POS System");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n=== Super-Saving POS System ===");
        System.out.println("1. Create New Bill");
        System.out.println("2. Manage Pending Bills");
        System.out.println("3. View Completed Bills");
        System.out.println("4. Exit");
    }

    private void createNewBill() {
        System.out.println("\n=== Create New Bill ===");
        String cashierId = getStringInput("Enter Cashier ID: ");
        String customerId = getStringInput("Enter Customer ID (or press Enter to skip): ");
        if (customerId.isEmpty()) customerId = null;

        try {
            currentBill = posService.createNewBill(cashierId, customerId);
            processBill();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void processBill() {
        while (true) {
            System.out.println("\n=== Current Bill: " + currentBill.getBillId() + " ===");
            System.out.println("1. Add Item");
            System.out.println("2. Complete Bill");
            System.out.println("3. Save as Pending");
            System.out.println("4. Cancel");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addItemToBill();
                    break;
                case 2:
                    posService.completeBill(currentBill);
                    displayBillSummary(currentBill);
                    return;
                case 3:
                    posService.savePendingBill(currentBill);
                    System.out.println("Bill saved as pending.");
                    return;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addItemToBill() {
        String itemCode = getStringInput("Enter Item Code: ");
        int quantity = getIntInput("Enter Quantity: ");

        try {
            posService.addItemToBill(currentBill, itemCode, quantity);
            System.out.println("Item added successfully.");
            displayBillSummary(currentBill);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void managePendingBills() {
        List<Bill> pendingBills = posService.getPendingBills();
        if (pendingBills.isEmpty()) {
            System.out.println("No pending bills found.");
            return;
        }

        System.out.println("\n=== Pending Bills ===");
        for (Bill bill : pendingBills) {
            System.out.println(bill.getBillId() + " - Customer: " + 
                (bill.getCustomer() != null ? bill.getCustomer().getName() : "Walk-in"));
        }

        String billId = getStringInput("Enter Bill ID to resume (or press Enter to cancel): ");
        if (!billId.isEmpty()) {
            try {
                currentBill = posService.resumePendingBill(billId);
                processBill();
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void viewCompletedBills() {
        List<Bill> completedBills = posService.getCompletedBills();
        if (completedBills.isEmpty()) {
            System.out.println("No completed bills found.");
            return;
        }

        System.out.println("\n=== Completed Bills ===");
        for (Bill bill : completedBills) {
            displayBillSummary(bill);
            System.out.println("-------------------");
        }
    }

    private void displayBillSummary(Bill bill) {
        System.out.println("\nBill ID: " + bill.getBillId());
        System.out.println("Cashier: " + bill.getCashier().getName());
        System.out.println("Customer: " + 
            (bill.getCustomer() != null ? bill.getCustomer().getName() : "Walk-in"));
        System.out.println("\nItems:");
        for (com.supersaving.model.BillItem item : bill.getItems()) {
            System.out.printf("%s - %d x %.2f (-%d%% discount) = %.2f\n",
                item.getItem().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                (int)item.getDiscount(),
                item.getNetPrice());
        }
        System.out.printf("\nTotal Discount: %.2f\n", bill.getTotalDiscount());
        System.out.printf("Total Cost: %.2f\n", bill.getTotalCost());
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}