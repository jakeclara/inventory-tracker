package com.jakeclara.inventorytracker.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InventoryItemDetailsViewTest {

    @Test
    @DisplayName("isLowStock returns true when current quantity is less than reorder threshold")
    void isLowStock_ReturnsTrue_WhenCurrentBelowThreshold() {
        // Arrange
        InventoryItemDetailsView item = new InventoryItemDetailsView(
            1L,
            "Item A",
            "SKU-123",
            5L,
            10,
            "unit",
            true,
            LocalDateTime.now()
        );
        
        // Act
        boolean result = item.isLowStock();
        
        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("isLowStock returns false when current quantity is equal to reorder threshold")
    void isLowStock_ReturnsFalse_WhenCurrentEqualsThreshold() {
        // Arrange
        InventoryItemDetailsView item = new InventoryItemDetailsView(
            1L,
            "Item A",
            "SKU-123",
            10L,
            10,
            "unit",
            true,
            LocalDateTime.now()
        );
        
        // Act
        boolean result = item.isLowStock();
        
        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isLowStock returns false when current quantity is greater than reorder threshold")
    void isLowStock_ReturnsFalse_WhenCurrentAboveThreshold() {
        // Arrange
        InventoryItemDetailsView item = new InventoryItemDetailsView(
            1L,
            "Item A",
            "SKU-123",
            15L,
            10,
            "unit",
            true,
            LocalDateTime.now()
        );
        
        // Act
        boolean result = item.isLowStock();
        
        // Assert
        assertThat(result).isFalse();
    }
}
