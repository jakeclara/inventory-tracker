package com.jakeclara.inventorytracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.model.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    /**
     * Find all active inventory items with their total quantity.
     * 
     * @return list of InventoryDashboardItem containing the 
     * item id, name, sku, total quantity, reorder threshold, and unit.
     */
    @Query("""
        SELECT new com.jakeclara.inventorytracker.dto.InventoryDashboardItem(
            item.id, 
            item.name, 
            item.sku, 
            COALESCE(SUM(
                CASE
                    WHEN movement.movementType IN('SALE', 'ADJUST_OUT')
                    THEN -movement.quantity
                    ELSE movement.quantity
                END
            ), 0),
            item.reorderThreshold, 
            item.unit
        )
        FROM InventoryItem item
        LEFT JOIN InventoryMovement movement ON movement.item = item
        WHERE item.isActive = true
        GROUP BY item.id, item.name, item.sku, item.reorderThreshold, item.unit
        ORDER BY item.name ASC
    """)
    List<InventoryDashboardItem> findActiveInventoryWithQuantity();
    
}
