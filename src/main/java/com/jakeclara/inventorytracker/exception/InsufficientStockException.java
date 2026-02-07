package com.jakeclara.inventorytracker.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long currentQuantity, int change) {
        super("Insufficient stock: current stock is " + currentQuantity + ", requested quantity is " + change);
    }
}
