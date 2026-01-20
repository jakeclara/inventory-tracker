package com.jakeclara.inventorytracker.dto;

import java.time.LocalDate;

import com.jakeclara.inventorytracker.model.InventoryMovementType;

public class CreateInventoryMovementRequest {

    private Long inventoryItemId;
    private int quantity;
    private InventoryMovementType movementType;
    private LocalDate movementDate;
    private Long createdByUserId;
    private String reference;
    private String note;

    public Long getInventoryItemId() {
        return inventoryItemId;
    }
    public void setInventoryItemId(Long inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public InventoryMovementType getMovementType() {
        return movementType;
    }
    public void setMovementType(InventoryMovementType movementType) {
        this.movementType = movementType;
    }
    public LocalDate getMovementDate() {
        return movementDate;
    }
    public void setMovementDate(LocalDate movementDate) {
        this.movementDate = movementDate;
    }
    public Long getCreatedByUserId() {
        return createdByUserId;
    }
    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    
}