package com.supersaving.model;

public class BillItem {
    private Item item;
    private int quantity;
    private double unitPrice;
    private double discount;
    private double netPrice;

    public BillItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = item.getPrice();
        this.discount = item.getDiscount();
        this.netPrice = item.getNetPrice() * quantity;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public double getNetPrice() {
        return netPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.netPrice = item.getNetPrice() * quantity;
    }
}