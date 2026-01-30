package com.jakeclara.inventorytracker.dto;

import java.time.LocalDate;

import com.jakeclara.inventorytracker.model.InventoryMovementType;

public record CreateInventoryMovementRequest (
    int quantity,
    InventoryMovementType movementType,
    LocalDate movementDate,
    String reference,
    String note
) {}
