package com.jakeclara.inventorytracker.model;

public enum InventoryMovementType {
    SALE,
    RECEIVE,
    ADJUST_IN,
    ADJUST_OUT;

    public int apply(int quantity) {
        return switch (this) { 
            case SALE, ADJUST_OUT -> -quantity; 
            case RECEIVE, ADJUST_IN -> quantity; 
        }; 
    }
}
