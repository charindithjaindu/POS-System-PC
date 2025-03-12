package com.supersaving.service;

import com.supersaving.model.Item;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemService {
    private static final String CSV_FILE_PATH = "src/main/resources/data/items.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private List<Item> items;

    public ItemService() {
        this.items = new ArrayList<>();
        loadItemsFromCsv();
    }

    private void loadItemsFromCsv() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] data = line.split(",");
                Item item = createItemFromCsvData(data);
                items.add(item);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading items from CSV: " + e.getMessage());
        }
    }

    private Item createItemFromCsvData(String[] data) {
        String itemCode = data[0];
        String name = data[1];
        double price = Double.parseDouble(data[2]);
        double weight = Double.parseDouble(data[3]);
        LocalDate manufacturingDate = LocalDate.parse(data[4], DATE_FORMATTER);
        LocalDate expiryDate = LocalDate.parse(data[5], DATE_FORMATTER);
        String manufacturer = data[6];

        return Item.builder()
            .itemCode(itemCode)
            .name(name)
            .price(price)
            .weight(weight)
            .manufacturingDate(manufacturingDate)
            .expiryDate(expiryDate)
            .manufacturer(manufacturer)
            .build();
    }

    public Optional<Item> findItemByCode(String itemCode) {
        return items.stream()
                .filter(item -> item.getItemCode().equals(itemCode))
                .findFirst();
    }

    public void updateItemDiscount(String itemCode, double discount) {
        findItemByCode(itemCode).ifPresent(item -> item.setDiscount(discount));
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }
}