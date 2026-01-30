package com.jakeclara.inventorytracker.dto;

import java.util.List;

public record InventoryDashboardResponse (
    List<InventoryDashboardItem> inventoryItems,
    long lowStockCount
){}
