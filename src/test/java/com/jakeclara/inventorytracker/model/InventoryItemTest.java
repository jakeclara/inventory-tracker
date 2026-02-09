package com.jakeclara.inventorytracker.model;

import org.junit.jupiter.api.*;

import com.jakeclara.inventorytracker.util.TestItemFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryItemTest {
	@Test
	@DisplayName("Constructor with valid data sets fields correctly")
	void constructor_WithValidData_SetsFields() {
		InventoryItem item = TestItemFactory.createDefaultItem();
		assertThat(item.getId())
		.as("New item should have null id before being saved to DB")
		.isNull();

		assertThat(item.getName()).isEqualTo(TestItemFactory.VALID_NAME);
		assertThat(item.getSku()).isEqualTo(TestItemFactory.VALID_SKU);
		assertThat(item.getReorderThreshold()).isEqualTo(TestItemFactory.VALID_REORDER_THRESHOLD);

		assertThat(item.isActive())
		.as("New item should be active by default")
		.isTrue();
	}

	@Test
	@DisplayName("Constructor should trim inputs")
	void constructor_ShouldTrimInputs() {
		InventoryItem item = new InventoryItem(
			"   Mac Book Air   ",
			"   MB-AIR-002   ",
			TestItemFactory.VALID_REORDER_THRESHOLD);

		assertThat(item.getName()).isEqualTo("Mac Book Air");
		assertThat(item.getSku()).isEqualTo("MB-AIR-002");
	}

	@Test
	@DisplayName("Constructor with null name throws exception")
	void constructor_WithNullName_ThrowsException() {
		assertThatThrownBy(() -> new InventoryItem(
			null,
			TestItemFactory.VALID_SKU,
			TestItemFactory.VALID_REORDER_THRESHOLD))
		.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Constructor with blank name throws exception")
	void constructor_WithBlankName_ThrowsException() {
		String blankName = "   ";
		assertThatThrownBy(() -> new InventoryItem(
			blankName,
			TestItemFactory.VALID_SKU,
			TestItemFactory.VALID_REORDER_THRESHOLD))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Name cannot be blank");
	}

	@Test
	@DisplayName("Constructor with too long name throws exception")
	void constructor_WithTooLongName_ThrowsException() {
		String longName = "A".repeat(151);
		assertThatThrownBy(() -> new InventoryItem(
			longName,
			TestItemFactory.VALID_SKU,
			TestItemFactory.VALID_REORDER_THRESHOLD))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Name cannot exceed 150 characters");
	}

	@Test
	@DisplayName("Constructor with null SKU throws exception")
	void constructor_WithNullSku_ThrowsException() {
		assertThatThrownBy(() -> new InventoryItem(
			TestItemFactory.VALID_NAME,
			null,
			TestItemFactory.VALID_REORDER_THRESHOLD))
		.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("Constructor with blank SKU throws exception")
	void constructor_WithBlankSku_ThrowsException() {
		String blankSku = "   ";
		assertThatThrownBy(() -> new InventoryItem(
			TestItemFactory.VALID_NAME,
			blankSku,
			TestItemFactory.VALID_REORDER_THRESHOLD))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("SKU cannot be blank");
	}

	@Test
	@DisplayName("Constructor with too long SKU throws exception")
	void constructor_WithTooLongSku_ThrowsException() {
		String longSku = "A".repeat(51);
		assertThatThrownBy(() -> new InventoryItem(
			TestItemFactory.VALID_NAME,
			longSku,
			TestItemFactory.VALID_REORDER_THRESHOLD))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("SKU cannot exceed 50 characters");
	}

	@Test
	@DisplayName("Constructor with negative reorder threshold throws exception")
	void constructor_WithNegativeReorderThreshold_ThrowsException() {
		int negativeThreshold = -1;
		assertThatThrownBy(() -> new InventoryItem(
			TestItemFactory.VALID_NAME,
			TestItemFactory.VALID_SKU,
			negativeThreshold))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Reorder threshold cannot be negative");
	}

	@Test
	@DisplayName("Rename with valid name updates name")
	void rename_WithValidName_UpdatesName() {
		InventoryItem item = TestItemFactory.createDefaultItem();
		String newName = "Ultrabook Laptop";
		item.rename(newName);
		assertThat(item.getName()).isEqualTo(newName);
	}

	@Test
	@DisplayName("Rename with blank name throws exception")
	void rename_WithBlankName_ThrowsException() {
		InventoryItem item = TestItemFactory.createDefaultItem();
		String blankName = "   ";
		assertThatThrownBy(() -> item.rename(blankName))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Name cannot be blank");
	}

	@Test
	@DisplayName("Rename with too long name throws exception")
	void rename_WithTooLongName_ThrowsException() {
		InventoryItem item = TestItemFactory.createDefaultItem();
		String longName = "A".repeat(151);
		assertThatThrownBy(() -> item.rename(longName))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Name cannot exceed 150 characters");
	}

	@Test
	@DisplayName("Update reorder threshold with valid value updates threshold")
	void updateReorderThreshold_WithValidValue_UpdatesThreshold() {
		InventoryItem item = TestItemFactory.createDefaultItem();
		int newThreshold = 20;
		item.updateReorderThreshold(newThreshold);
		assertThat(item.getReorderThreshold()).isEqualTo(newThreshold);
	}

	@Test
	@DisplayName("Update reorder threshold with negative value throws exception")
	void updateReorderThreshold_WithNegativeValue_ThrowsException() {
		InventoryItem item = TestItemFactory.createDefaultItem();
		int negativeThreshold = -5;
		assertThatThrownBy(() -> item.updateReorderThreshold(negativeThreshold))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Reorder threshold cannot be negative");
	}

	@Test
	@DisplayName("Set unit with valid value trims and sets unit")
	void setUnit_WithValidValue_TrimsAndSetsUnit() {
		InventoryItem item = TestItemFactory.createDefaultItem();
		String unit = "   pieces   ";
		item.setUnit(unit);
		assertThat(item.getUnit()).isEqualTo("pieces");
	}

	@Test
	@DisplayName("Set unit with too long value throws exception")
	void setUnit_WithTooLongValue_ThrowsException() {
		InventoryItem item = TestItemFactory.createDefaultItem();
		String longUnit = "A".repeat(21);
		assertThatThrownBy(() -> item.setUnit(longUnit))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Unit cannot exceed 20 characters");
	}

	@Test
	@DisplayName("Set unit allows null value sets unit to null")
	void setUnit_WithNullValue_SetsUnitToNull() {
		InventoryItem item = TestItemFactory.createDefaultItem();
		item.setUnit("each");
		assertThat(item.getUnit()).isEqualTo("each");
		item.setUnit(null);
		assertThat(item.getUnit()).isNull();
	}
}
