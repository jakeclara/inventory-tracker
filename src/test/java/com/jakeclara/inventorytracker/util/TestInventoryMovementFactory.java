package com.jakeclara.inventorytracker.util;

import java.time.LocalDate;

import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.model.InventoryMovement;
import com.jakeclara.inventorytracker.model.InventoryMovementType;
import com.jakeclara.inventorytracker.model.User;

public class TestInventoryMovementFactory {
    public static final int VALID_QUANTITY = 10;
    public static final InventoryMovementType VALID_MOVEMENT_TYPE = InventoryMovementType.RECEIVE;

    public static InventoryMovement createDefaultInventoryMovement() {
        return new InventoryMovement(
            TestInventoryItemFactory.createDefaultItem(), 
            VALID_QUANTITY,
            VALID_MOVEMENT_TYPE, 
            LocalDate.now(),
            TestUserFactory.createDefaultUser());
    }

    public static InventoryMovement createInventoryMovement(
        InventoryItem item,
        int quantity,
        InventoryMovementType movementType,
        LocalDate movementDate,
        User createdBy
    ) {
        return new InventoryMovement(
            item,
            quantity,
            movementType,
            movementDate,
            createdBy
        );
    }
    
}
