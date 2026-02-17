package com.jakeclara.inventorytracker.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jakeclara.inventorytracker.model.InventoryMovement;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    Page<InventoryMovement> findByItemIdOrderByMovementDateDesc(Long itemId, Pageable pageable);
}
