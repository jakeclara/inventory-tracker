package com.jakeclara.inventorytracker.dto;

import java.time.LocalDate;

import com.jakeclara.inventorytracker.model.InventoryMovement;
import com.jakeclara.inventorytracker.model.InventoryMovementType;

public record InventoryMovementView(
    InventoryMovementType movementType,
    int quantity,
    LocalDate movementDate,
    String reference,
    String createdBy
) {

    public static InventoryMovementView from(InventoryMovement movement) {
        return new InventoryMovementView(
            movement.getMovementType(),
            movement.getQuantity(),
            movement.getMovementDate(),
            movement.getReference(),
            movement.getCreatedBy().getUsername()
        );
    }
    
}
