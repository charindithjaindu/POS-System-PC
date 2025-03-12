package com.supersaving.service;

import com.supersaving.model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class POSService {
    private DatabaseService databaseService;
    private Map<String, Bill> pendingBills;
    private List<Bill> completedBills;

    public POSService(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.pendingBills = new HashMap<>();
        this.completedBills = new ArrayList<>();
    }

    public Bill createNewBill(String cashierId, String customerId) {
        Optional<Cashier> cashier = databaseService.findCashierById(cashierId);
        Optional<Customer> customer = customerId != null ? 
            databaseService.findCustomerById(customerId) : Optional.empty();

        if (!cashier.isPresent()) {
            throw new IllegalArgumentException("Invalid cashier ID");
        }

        String billId = generateBillId();
        Bill bill = new Bill(billId, cashier.get(), customer.orElse(null));
        return bill;
    }

    public void addItemToBill(Bill bill, String itemCode, int quantity) {
        Optional<Item> item = databaseService.findItemByCode(itemCode);
        if (!item.isPresent()) {
            throw new IllegalArgumentException("Invalid item code");
        }
        bill.addItem(item.get(), quantity);
    }

    public void savePendingBill(Bill bill) {
        bill.setPending(true);
        pendingBills.put(bill.getBillId(), bill);
    }

    public Bill resumePendingBill(String billId) {
        Bill bill = pendingBills.get(billId);
        if (bill == null) {
            throw new IllegalArgumentException("No pending bill found with ID: " + billId);
        }
        return bill;
    }

    public void completeBill(Bill bill) {
        bill.setPending(false);
        bill.updateDateTime();
        completedBills.add(bill);
        pendingBills.remove(bill.getBillId());

        for (BillItem billItem : bill.getItems()) {
            String customerId = bill.getCustomer() != null ? bill.getCustomer().getId() : null;
            databaseService.saveCustomerPurchase(
                customerId,
                billItem.getItem().getItemCode(),
                billItem.getQuantity(),
                billItem.getUnitPrice(),
                billItem.getNetPrice(),
                bill.getCashier().getId()
            );
        }
    }

    public List<Bill> getPendingBills() {
        return new ArrayList<>(pendingBills.values());
    }

    public List<Bill> getCompletedBills() {
        return new ArrayList<>(completedBills);
    }

    private String generateBillId() {
        return "BILL" + System.currentTimeMillis();
    }
}