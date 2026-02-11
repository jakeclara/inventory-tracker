package com.jakeclara.inventorytracker.util;

import com.jakeclara.inventorytracker.model.InventoryItem;

public class TestInventoryItemFactory {

    public static final String VALID_NAME = "Gaming Laptop";
    public static final String VALID_SKU = "PC-LP-001";
    public static final int VALID_REORDER_THRESHOLD = 10;

    public static InventoryItem createDefaultItem() {
        return new InventoryItem(VALID_NAME, VALID_SKU, VALID_REORDER_THRESHOLD);
    }

    public static InventoryItem createItem(
        String name,
        String sku,
        int reorderThreshold
    ) {
        return new InventoryItem(name, sku, reorderThreshold);
    }
}
