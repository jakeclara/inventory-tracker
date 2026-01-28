package com.jakeclara.inventorytracker.dto;

public record InventoryDashboardItem (
    Long id,
    String name,
    String sku,
    Long currentQuantity,
    int reorderThreshold,
    String unit
) {
    public boolean lowStock() {
        return currentQuantity < reorderThreshold;
    }    
}
