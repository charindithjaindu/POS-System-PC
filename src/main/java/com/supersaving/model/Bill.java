package com.supersaving.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bill {
    private String billId;
    private Cashier cashier;
    private Customer customer;
    private List<BillItem> items;
    private LocalDateTime dateTime;
    private boolean isPending;
    private double totalDiscount;
    private double totalCost;

    public Bill(String billId, Cashier cashier, Customer customer) {
        this.billId = billId;
        this.cashier = cashier;
        this.customer = customer;
        this.items = new ArrayList<>();
        this.dateTime = LocalDateTime.now();
        this.isPending = false;
        this.totalDiscount = 0.0;
        this.totalCost = 0.0;
    }

    public void addItem(Item item, int quantity) {
        BillItem billItem = new BillItem(item, quantity);
        items.add(billItem);
        calculateTotals();
    }

    public void removeItem(String itemCode) {
        items.removeIf(item -> item.getItem().getItemCode().equals(itemCode));
        calculateTotals();
    }

    private void calculateTotals() {
        totalDiscount = 0.0;
        totalCost = 0.0;
        
        for (BillItem item : items) {
            double itemDiscount = (item.getUnitPrice() - item.getNetPrice() / item.getQuantity()) 
                                * item.getQuantity();
            totalDiscount += itemDiscount;
            totalCost += item.getNetPrice();
        }
    }

    public String getBillId() {
        return billId;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<BillItem> getItems() {
        return new ArrayList<>(items);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void updateDateTime() {
        this.dateTime = LocalDateTime.now();
    }
}