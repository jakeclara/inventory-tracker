package com.jakeclara.inventorytracker.dto;

import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.util.TestInventoryItemFactory;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryItemFormTest {
	@Test
	@DisplayName("from should map InventoryItem to InventoryItemForm correctly")
	void from_ShouldMapInventoryItemCorrectly() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();

		InventoryItemForm form = InventoryItemForm.from(item);

		assertThat(form.getName()).isEqualTo(item.getName());
		assertThat(form.getSku()).isEqualTo(item.getSku());
		assertThat(form.getReorderThreshold()).isEqualTo(item.getReorderThreshold());
		assertThat(form.getUnit()).isEqualTo(item.getUnit());
	}
}
