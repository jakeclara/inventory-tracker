package com.jakeclara.inventorytracker.dto;

public record CreateInventoryItemRequest (
    String name,
    String sku,
    int reorderThreshold,
    String unit
) {}
