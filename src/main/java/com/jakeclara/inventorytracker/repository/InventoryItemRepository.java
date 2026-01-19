package com.jakeclara.inventorytracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jakeclara.inventorytracker.model.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    
}
