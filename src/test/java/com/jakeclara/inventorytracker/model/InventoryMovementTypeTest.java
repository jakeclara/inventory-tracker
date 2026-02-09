package com.jakeclara.inventorytracker.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryMovementTypeTest {
	@ParameterizedTest(name = "{0} applies to {1} resulting in {2}")
	@CsvSource({
		"SALE, 10, -10",
		"ADJUST_OUT, 5, -5",
		"RECEIVE, 20, 20",
		"ADJUST_IN, 7, 7",
	})
	@DisplayName("apply() should return correct positive or negative quantity based on movement type")
	void apply_ReturnsCorrectQuantity(InventoryMovementType type, int inputQuantity, int expectedQuantity) {
		assertThat(type.apply(inputQuantity)).isEqualTo(expectedQuantity);
	}
}
