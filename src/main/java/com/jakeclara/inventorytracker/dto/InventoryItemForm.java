package com.jakeclara.inventorytracker.dto;

import com.jakeclara.inventorytracker.model.InventoryItem;

public class InventoryItemForm {
    private String name;
    private String sku;
    private int reorderThreshold;
    private String unit;
    
    public InventoryItemForm() {
    }

    public InventoryItemForm(String name, String sku, int reorderThreshold, String unit) {
        this.name = name;
        this.sku = sku;
        this.reorderThreshold = reorderThreshold;
        this.unit = unit;
    }

    public static InventoryItemForm from(InventoryItem item) {
        return new InventoryItemForm(
            item.getName(),
            item.getSku(),
            item.getReorderThreshold(),
            item.getUnit()
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(int reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
