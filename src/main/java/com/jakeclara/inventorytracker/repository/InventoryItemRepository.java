package com.jakeclara.inventorytracker.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.model.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsBySku(String sku);

    
    /**
     * Retrieves a list of inventory items with their current quantity.
     * The result is ordered by item name ascending.
     * 
     * @param isActive the active status of the items to retrieve
     * @param pageable the pagination information
     * @return a page of inventory items with their current quantity
     */
    @Query(
        value = """
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
            WHERE item.isActive = :isActive
            GROUP BY item.id, item.name, item.sku, item.reorderThreshold, item.unit
            ORDER BY item.name ASC
            """,
        countQuery = """
            SELECT COUNT(item)
            FROM InventoryItem item
            WHERE item.isActive = :isActive
            """
    )
    Page<InventoryDashboardItem> findInventoryByActiveStatusWithQuantity(
        @Param("isActive") boolean isActive,
        Pageable pageable
    );

    /**
     * Count the number of inventory items that are low stock by the given active status.
     * 
     * @param isActive the active status of the inventory items
     * @return the count of inventory items that are low stock
     */
    @Query("""
        SELECT COUNT(item)
        FROM InventoryItem item
        WHERE item.isActive = :isActive
        AND (
            SELECT COALESCE(SUM(
                CASE
                    WHEN movement.movementType IN('SALE', 'ADJUST_OUT')
                    THEN -movement.quantity
                    ELSE movement.quantity
                END
            ), 0)
            FROM InventoryMovement movement
            WHERE movement.item.id = item.id
        ) < item.reorderThreshold
    """)
    long countLowStockByActiveStatus(@Param("isActive") boolean isActive);

    /**
     * Find the current quantity of the inventory item with the given id.
     * 
     * @param itemId the id of the inventory item
     * @return the current quantity of the inventory item
     */
    @Query("""
        SELECT COALESCE(SUM(
            CASE
                WHEN movement.movementType IN('SALE', 'ADJUST_OUT')
                THEN -movement.quantity
                ELSE movement.quantity
            END
        ), 0)
        FROM InventoryMovement movement
        WHERE movement.item.id = :itemId
    """)
    Long findCurrentQuantityByItemId(@Param("itemId") Long itemId);
    
}


