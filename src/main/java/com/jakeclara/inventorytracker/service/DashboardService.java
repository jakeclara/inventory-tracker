package com.jakeclara.inventorytracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.dto.InventoryDashboardView;
import com.jakeclara.inventorytracker.repository.InventoryItemRepository;

@Service
public class DashboardService {

    private final InventoryItemRepository inventoryItemRepository;

    public DashboardService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    /**
     * Retrieves the inventory dashboard.
     * 
     * The inventory dashboard contains a list of all active inventory items with their total quantity.
     * It also includes the count of inventory items that have a quantity below the reorder threshold.
     * 
     * @return an InventoryDashboardView containing the list of inventory items and the low stock count.
     */
    public InventoryDashboardView getInventoryDashboard() {
        List<InventoryDashboardItem> inventoryItems = inventoryItemRepository.findActiveInventoryWithQuantity();

        long lowStockCount = inventoryItems.stream()
            .filter(InventoryDashboardItem::lowStock)
            .count();

        return new InventoryDashboardView(inventoryItems, lowStockCount);
    }
    
}
