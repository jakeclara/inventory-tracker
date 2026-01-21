package com.jakeclara.inventorytracker.dto;

public record InventoryDashboardItem (
    Long id,
    String name,
    String sku,
    int currentQuantity,
    String unit,
    boolean lowStock
) {
    
}
