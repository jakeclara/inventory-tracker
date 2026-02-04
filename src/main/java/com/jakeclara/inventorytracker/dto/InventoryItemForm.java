package com.jakeclara.inventorytracker.dto;

import com.jakeclara.inventorytracker.model.InventoryItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InventoryItemForm {
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 150, message = "Name must be between 3 and 150 characters")
    private String name;

    @NotBlank(message = "SKU is required")
    @Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    private String sku;

    @NotBlank(message = "Reorder threshold is required")
    @Min(value = 0, message = "Reorder threshold must be zero or greater")
    private int reorderThreshold;

    @Size(max = 20, message = "Unit cannot exceed 20 characters")
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
