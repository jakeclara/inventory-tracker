package com.jakeclara.inventorytracker.model;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "inventory_item")
public class InventoryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_item_id")
    private Long id;

    @Column(name = "item_name", nullable = false, unique = true, length = 150)
    @NotBlank
    @Size(max = 150)
    private String name;
    
    @Column(name = "item_sku", nullable = false, unique = true, length = 50)
    @NotBlank
    @Size(max = 50)
    private String sku;

    @Column(name = "reorder_threshold", nullable = false)
    @Min(0)
    private int reorderThreshold;

    @Column(name = "item_unit", length = 20)
    @Size(max = 20)
    private String unit;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    protected InventoryItem() {}

    public InventoryItem(String name, String sku, int reorderThreshold) {
        this.name = validateName(name);
        this.sku = validateSku(sku);
        this.reorderThreshold = validateReorderThreshold(reorderThreshold);
        this.isActive = true;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public String getUnit() {
        return unit;
    }

    public boolean isActive() {
        return isActive;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void rename(String newName) {
        this.name = validateName(newName);
    }

    public void updateReorderThreshold(int newThreshold) {
        this.reorderThreshold = validateReorderThreshold(newThreshold);
    }

    private String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        String trimmedName = name.trim();
        if (trimmedName.length() > 150) {
            throw new IllegalArgumentException("Name cannot exceed 150 characters");
        }
        return trimmedName;
    }

    private String validateSku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be blank");
        }
        String trimmedSku = sku.trim();
        if (trimmedSku.length() > 50) {
            throw new IllegalArgumentException("SKU cannot exceed 50 characters");
        }
        return trimmedSku;
    }

    private int validateReorderThreshold(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Reorder threshold cannot be negative");
        }
        return threshold;
    }

    public void setUnit(String unit) {
        if (unit != null) {
            String trimmedUnit = unit.trim();
            if (trimmedUnit.length() > 20) {
                throw new IllegalArgumentException("Unit cannot exceed 20 characters");
            }
            this.unit = trimmedUnit.isEmpty() ? null : trimmedUnit;
        } else {
            this.unit = null;
        }
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "InventoryItem [id=" + id + 
        ", name=" + name + 
        ", sku=" + sku + 
        ", reorderThreshold=" + reorderThreshold + 
        ", unit=" + unit + 
        ", createdAt=" + createdAt + "]";
    }

}
