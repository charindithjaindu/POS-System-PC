package com.supersaving.model;

import java.time.LocalDate;

public class Item {
    private String itemCode;
    private String name;
    private double price;
    private double weight;
    private LocalDate manufacturingDate;
    private LocalDate expiryDate;
    private String manufacturer;
    private double discount;

    public Item(Builder builder) {
        this.itemCode = builder.itemCode;
        this.name = builder.name;
        this.price = builder.price;
        this.weight = builder.weight;
        this.manufacturingDate = builder.manufacturingDate;
        this.expiryDate = builder.expiryDate;
        this.manufacturer = builder.manufacturer;
        this.discount = 0.0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getWeight() {
        return weight;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        if (discount < 0 || discount > 75) {
            throw new IllegalArgumentException("Discount must be between 0 and 75%");
        }
        this.discount = discount;
    }

    public double getNetPrice() {
        return price * (1 - discount / 100);
    }

    public static class Builder {
        private String itemCode;
        private String name;
        private double price;
        private double weight;
        private LocalDate manufacturingDate;
        private LocalDate expiryDate;
        private String manufacturer;

        public Builder itemCode(String itemCode) {
            this.itemCode = itemCode;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(double price) {
            this.price = price;
            return this;
        }

        public Builder weight(double weight) {
            this.weight = weight;
            return this;
        }

        public Builder manufacturingDate(LocalDate manufacturingDate) {
            this.manufacturingDate = manufacturingDate;
            return this;
        }

        public Builder expiryDate(LocalDate expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder manufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }
}