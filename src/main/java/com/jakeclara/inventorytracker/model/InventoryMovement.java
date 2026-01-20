package com.jakeclara.inventorytracker.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "inventory_movement")
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_movement_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    @NotNull
    private InventoryItem item;

    @Column(nullable = false, updatable = false)
    @Min(1)
    private int quantity;

    @Column(name = "movement_type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private InventoryMovementType movementType;

    @Column(name = "movement_date", nullable = false, updatable = false)
    @PastOrPresent
    @NotNull
    private LocalDate movementDate;

    @Column(length = 100)
    @Size(max = 100)
    private String reference;

    @Column(length = 255)
    @Size(max = 255)
    private String note;

    @JoinColumn(name = "created_by", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    @NotNull
    private User createdBy;

    protected InventoryMovement() {}

    public InventoryMovement(
        InventoryItem item, 
        int quantity,
        InventoryMovementType movementType, 
        LocalDate movementDate,
        User createdBy
    ) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }
        if (movementType == null) {
            throw new IllegalArgumentException("Movement type cannot be null");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("Created by cannot be null");
        }

        this.item = item;
        this.quantity = quantity;
        this.movementType = movementType;
        this.movementDate = validateMovementDate(movementDate);
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public InventoryItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public InventoryMovementType getMovementType() {
        return movementType;
    }

    public LocalDate getMovementDate() {
        return movementDate;
    }

    public String getReference() {
        return reference;
    }

    public String getNote() {
        return note;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setReference(String reference) {
        this.reference = validateAndTrimOptional(reference, 100, "Reference");
    }

    public void setNote(String note) {
        this.note = validateAndTrimOptional(note, 255, "Note");
    }

    private String validateAndTrimOptional(String value, int maxLength, String fieldName) {
        if (value == null) return null;
        
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " cannot exceed " + maxLength + " characters");
        }
        
        return trimmed.isEmpty() ? null : trimmed;
    }

    private LocalDate validateMovementDate(LocalDate movementDate) {
        if (movementDate == null) {
            throw new IllegalArgumentException("Movement date cannot be null");
        }
        if (movementDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Movement date cannot be in the future");
        }
        return movementDate;
    }

    @Override
    public String toString() {
        return "InventoryMovement [id=" + id + 
        ", item=" + item + 
        ", quantity=" + quantity + 
        ", movementType=" + movementType + 
        ", movementDate=" + movementDate + 
        ", reference=" + reference + 
        ", note=" + note + 
        ", createdBy=" + createdBy + "]";
    }

}
