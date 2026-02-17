package com.jakeclara.inventorytracker.dto;

import java.util.List;

import com.jakeclara.inventorytracker.dto.common.Pagination;

public record InventoryDashboardView (
    List<InventoryDashboardItem> inventoryItems,
    long lowStockCount,
    Pagination pagination
){}
