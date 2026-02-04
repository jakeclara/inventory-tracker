package com.jakeclara.inventorytracker.dto;

import java.util.List;

public record InventoryDashboardView (
    List<InventoryDashboardItem> inventoryItems,
    long lowStockCount
){}
