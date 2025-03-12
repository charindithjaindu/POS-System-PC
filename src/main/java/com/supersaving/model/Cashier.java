package com.supersaving.model;

public class Cashier {
    private String id;
    private String name;
    private String branch;

    public Cashier(String id, String name, String branch) {
        this.id = id;
        this.name = name;
        this.branch = branch;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBranch() {
        return branch;
    }
}