package com.jakeclara.inventorytracker.exception;

public class InactiveItemException extends RuntimeException {
    public InactiveItemException(String message) {
        super(message);
    }
}
