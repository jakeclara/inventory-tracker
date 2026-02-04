package com.jakeclara.inventorytracker.dto;

import java.time.LocalDate;

import com.jakeclara.inventorytracker.model.InventoryMovementType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record InventoryMovementForm (
    @Min(value = 1, message = "Quantity must be at least 1")
    int quantity,

    @NotNull(message = "Movement type is required")
    InventoryMovementType movementType,

    @NotNull(message = "Movement date is required")
    @PastOrPresent(message = "Movement date cannot be in the future")
    LocalDate movementDate,

    @Size(max = 100, message = "Reference cannot exceed 100 characters")
    String reference,
    
    @Size(max = 255, message = "Note cannot exceed 255 characters")
    String note
) {
    public static InventoryMovementForm empty() {
        return new InventoryMovementForm(
            0, 
            null, 
            null, 
            null, 
            null);
    }
}
