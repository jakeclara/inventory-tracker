package com.jakeclara.inventorytracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.dto.InventoryDashboardResponse;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.repository.InventoryItemRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class InventoryItemService {
    
    private final InventoryItemRepository inventoryItemRepository;

    public InventoryItemService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    /**
     * Retrieves the inventory dashboard.
     * 
     * The inventory dashboard contains a list of all active inventory items with their total quantity.
     * It also includes the count of inventory items that have a quantity below the reorder threshold.
     * 
     * @return an InventoryDashboardResponse containing the list of inventory items and the low stock count.
     */
    public InventoryDashboardResponse getInventoryDashboard() {
        List<InventoryDashboardItem> inventoryItems = inventoryItemRepository.findActiveInventoryWithQuantity();

        long lowStockCount = inventoryItems.stream()
            .filter(InventoryDashboardItem::lowStock)
            .count();

        return new InventoryDashboardResponse(inventoryItems, lowStockCount);
    }


    public InventoryItem createInventoryItem(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }

    @Transactional
    public void deactivateInventoryItem(Long id) {
        InventoryItem item = getInventoryItemById(id);
        item.setIsActive(false);
    }

    @Transactional
    public void reactivateInventoryItem(Long id) {
        InventoryItem item = getInventoryItemById(id);
        item.setIsActive(true);
    }

    @Transactional
    public void updateInventoryItem(Long id, InventoryItem updatedItem) {
        InventoryItem existingItem = getInventoryItemById(id);
        existingItem.rename(updatedItem.getName());
        existingItem.updateReorderThreshold(updatedItem.getReorderThreshold());
        existingItem.setUnit(updatedItem.getUnit());
    }

    public InventoryItem getInventoryItemById(Long id) {
        return inventoryItemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Inventory item not found " + id));
    }
    
}
