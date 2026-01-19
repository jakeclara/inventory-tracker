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

@Entity
@Table(name = "inventory_item")
public class InventoryItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_item_id")
    private Long id;

    @Column(name = "item_name", nullable = false, unique = true, length = 150)
    @NotBlank
    private String name;
    
    @Column(name = "item_sku", nullable = false, unique = true, length = 50)
    @NotBlank
    private String sku;

    @Column(name = "reorder_threshold", nullable = false)
    @Min(0)
    private int reorderThreshold;

    @Column(name = "item_unit", length = 20)
    private String unit;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    protected InventoryItem() {}

    public InventoryItem(String name, String sku, int reorderThreshold) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU cannot be blank");
        }
        if (reorderThreshold < 0) {
            throw new IllegalArgumentException("Reorder threshold cannot be negative");
        }
        this.name = name.trim();
        this.sku = sku.trim();
        this.reorderThreshold = reorderThreshold;
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
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        this.name = newName.trim();
    }

    public void updateReorderThreshold(int newThreshold) {
        if (newThreshold < 0) {
            throw new IllegalArgumentException("Reorder threshold cannot be negative");
        }
        this.reorderThreshold = newThreshold;
    }

    public void setUnit(String unit) {
        this.unit = (unit == null) ? null : unit.trim();
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
