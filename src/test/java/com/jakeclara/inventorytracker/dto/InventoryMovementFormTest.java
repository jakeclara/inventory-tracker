package com.jakeclara.inventorytracker.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryMovementFormTest {

    @Test
    @DisplayName("empty should return form with default values")
    void empty_ShouldReturnDefaultForm() {
        InventoryMovementForm form = InventoryMovementForm.empty();
        
        assertThat(form.quantity()).isZero();
        assertThat(form.movementType()).isNull();
        assertThat(form.movementDate()).isNull();
        assertThat(form.reference()).isNull();
        assertThat(form.note()).isNull();
    }
}
