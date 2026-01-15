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
@Table(name = "inventory_items")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    @NotBlank
    @Size(min = 2, max = 150)
    private String name;
    
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank
    @Size(min = 2, max = 50)
    private String sku;

    @Column(name = "reorder_threshold", nullable = false)
    @Min(0)
    private int reorderThreshold;

    @Column(length = 20)
    @Size(max = 20)
    private String unit;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    protected InventoryItem() {}

    public InventoryItem(String name, String sku, int reorderThreshold) {
        this.name = (name == null) ? null : name.trim();
        this.sku = (sku == null) ? null : sku.trim();
        this.reorderThreshold = reorderThreshold;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null");
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

    @Override
    public String toString() {
        return "InventoryItem [id=" + id + ", name=" + name + ", sku=" + sku + ", reorderThreshold=" + reorderThreshold
                + ", unit=" + unit + ", createdAt=" + createdAt + "]";
    }
    
}
