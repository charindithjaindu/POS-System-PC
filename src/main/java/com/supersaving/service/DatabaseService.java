package com.supersaving.service;

import com.supersaving.model.Item;
import com.supersaving.model.Customer;
import com.supersaving.model.Cashier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseService {
    private static final String DATA_PATH = "src/main/resources/data/";
    private List<Item> items;
    private List<Customer> customers;
    private List<Cashier> cashiers;

    public DatabaseService() {
        items = new ArrayList<>();
        customers = new ArrayList<>();
        cashiers = new ArrayList<>();
        initializeDataDirectory();
        loadData();
    }

    private void initializeDataDirectory() {
        File dataDir = new File(DATA_PATH);
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                throw new RuntimeException("Failed to create data directory: " + DATA_PATH);
            }
        }
        createFileIfNotExists("items.csv", "itemCode,name,price,weight,manufacturingDate,expiryDate,manufacturer\n");
        createFileIfNotExists("customers.csv", "customerId,name,email,phone,loyaltyPoints\n");
        createFileIfNotExists("cashiers.csv", "cashierId,name,branch\n");
    }

    private void createFileIfNotExists(String fileName, String header) {
        File file = new File(DATA_PATH + fileName);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(header);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create " + fileName + ": " + e.getMessage());
            }
        }
    }

    private void loadData() {
        loadItems();
        loadCustomers();
        loadCashiers();
    }

    private void loadItems() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_PATH + "items.csv"))) {
            String line = reader.readLine(); 
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] data = line.split(",");
                    if (data.length < 7) {
                        throw new IllegalArgumentException("Insufficient columns in items.csv at line " + lineNumber);
                    }
                    items.add(Item.builder()
                        .itemCode(validateNotEmpty(data[0], "itemCode"))
                        .name(validateNotEmpty(data[1], "name"))
                        .price(parseDouble(data[2], "price"))
                        .weight(parseDouble(data[3], "weight"))
                        .manufacturingDate(parseDate(data[4], "manufacturingDate"))
                        .expiryDate(parseDate(data[5], "expiryDate"))
                        .manufacturer(validateNotEmpty(data[6], "manufacturer"))
                        .build());
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing item at line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading items: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_PATH + "customers.csv"))) {
            String line = reader.readLine(); 
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] data = line.split(",");
                    if (data.length < 5) {
                        throw new IllegalArgumentException("Insufficient columns in customers.csv at line " + lineNumber);
                    }
                    Customer customer = new Customer(
                        validateNotEmpty(data[0], "customerId"),
                        validateNotEmpty(data[1], "name"),
                        validateEmail(data[2]),
                        validatePhone(data[3]),
                        validateNotEmpty(data[4], "loyaltyPoints")
                    );
                    customers.add(customer);
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing customer at line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading customers: " + e.getMessage());
        }
    }

    private void loadCashiers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_PATH + "cashiers.csv"))) {
            String line = reader.readLine(); 
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] data = line.split(",");
                    if (data.length < 3) {
                        throw new IllegalArgumentException("Insufficient columns in cashiers.csv at line " + lineNumber);
                    }
                    cashiers.add(new Cashier(
                        validateNotEmpty(data[0], "cashierId"),
                        validateNotEmpty(data[1], "name"),
                        validateNotEmpty(data[2], "branch")
                    ));
                } catch (Exception e) {
                    throw new RuntimeException("Error parsing cashier at line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading cashiers: " + e.getMessage());
        }
    }

    private String validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value.trim();
    }

    private String validateEmail(String email) {
        email = validateNotEmpty(email, "email");
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        return email;
    }

    private String validatePhone(String phone) {
        phone = validateNotEmpty(phone, "phone");
        if (!phone.matches("^\\+?[0-9]{10,}$")) {
            throw new IllegalArgumentException("Invalid phone number format: " + phone);
        }
        return phone;
    }

    private double parseDouble(String value, String fieldName) {
        try {
            double result = Double.parseDouble(value.trim());
            if (result < 0) {
                throw new IllegalArgumentException(fieldName + " cannot be negative");
            }
            return result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + fieldName + " format: " + value);
        }
    }

    private LocalDate parseDate(String value, String fieldName) {
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid " + fieldName + " format: " + value + ". Use YYYY-MM-DD format.");
        }
    }

    public Optional<Item> findItemByCode(String itemCode) {
        return items.stream()
                .filter(item -> item.getItemCode().equals(itemCode))
                .findFirst();
    }

    public Optional<Customer> findCustomerById(String customerId) {
        return customers.stream()
                .filter(customer -> customer.getId().equals(customerId))
                .findFirst();
    }

    public Optional<Cashier> findCashierById(String cashierId) {
        return cashiers.stream()
                .filter(cashier -> cashier.getId().equals(cashierId))
                .findFirst();
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers);
    }

    public List<Cashier> getAllCashiers() {
        return new ArrayList<>(cashiers);
    }

    public void saveCustomerPurchase(String customerId, String itemCode, int quantity, double unitPrice, double totalPrice, String cashierId) {
        String purchaseId = generatePurchaseId();
        String purchaseDate = LocalDate.now().toString();
        String record = String.format("%s,%s,%s,%d,%.2f,%.2f,%s,%s\n",
            purchaseId, customerId, itemCode, quantity, unitPrice, totalPrice, purchaseDate, cashierId);

        try (FileWriter writer = new FileWriter(DATA_PATH + "customer_purchases.csv", true)) {
            writer.write(record);
        } catch (IOException e) {
            throw new RuntimeException("Error saving customer purchase: " + e.getMessage());
        }
    }

    private String generatePurchaseId() {
        return "PUR" + System.currentTimeMillis();
    }
}