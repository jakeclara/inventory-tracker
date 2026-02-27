package com.jakeclara.inventorytracker.exception;

public class UnauthorizedMovementTypeException extends RuntimeException {
    public UnauthorizedMovementTypeException(String message) {
        super(message);
    }
}