package com.jakeclara.inventorytracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jakeclara.inventorytracker.model.InventoryMovement;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    List<InventoryMovement> findByItemIdOrderByMovementDateDesc(Long itemId);
    
}
