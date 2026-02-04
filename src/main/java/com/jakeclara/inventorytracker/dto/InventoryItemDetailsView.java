package com.jakeclara.inventorytracker.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.jakeclara.inventorytracker.model.InventoryItem;

public record InventoryItemDetailsView(
    Long id,
    String name,
    String sku,
    Long currentQuantity,
    int reorderThreshold,
    String unit,
    boolean isActive,
    LocalDateTime createdAt
) {

    public boolean isLowStock() {
    return currentQuantity < reorderThreshold;
    }

    public static InventoryItemDetailsView from(InventoryItem item, Long currentQuantity) {
        return new InventoryItemDetailsView(
            item.getId(),
            item.getName(),
            item.getSku(),
            currentQuantity,
            item.getReorderThreshold(),
            item.getUnit(),
            item.isActive(),
            item.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
        );
    }

}
