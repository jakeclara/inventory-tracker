package com.jakeclara.inventorytracker.model;

import java.time.LocalDate;
import org.junit.jupiter.api.*;

import com.jakeclara.inventorytracker.util.TestInventoryItemFactory;
import com.jakeclara.inventorytracker.util.TestInventoryMovementFactory;
import com.jakeclara.inventorytracker.util.TestUserFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class InventoryMovementTest {
	@Test
	@DisplayName("Constructor with valid data sets fields correctly")
	void constructor_WithValidData_SetsFields() {
		InventoryMovement movement = TestInventoryMovementFactory.createDefaultInventoryMovement();

		assertThat(movement.getId())
			.as("New movement should have null id before being saved to DB")
			.isNull();

		assertThat(movement.getItem().getName()).isEqualTo(TestInventoryItemFactory.VALID_NAME);
		assertThat(movement.getItem().getSku()).isEqualTo(TestInventoryItemFactory.VALID_SKU);
		assertThat(movement.getItem().getReorderThreshold()).isEqualTo(TestInventoryItemFactory.VALID_REORDER_THRESHOLD);

		assertThat(movement.getQuantity()).isEqualTo(TestInventoryMovementFactory.VALID_QUANTITY);
		assertThat(movement.getMovementType()).isEqualTo(TestInventoryMovementFactory.VALID_MOVEMENT_TYPE);
		assertThat(movement.getMovementDate()).isBeforeOrEqualTo(LocalDate.now());

		assertThat(movement.getCreatedBy().getUsername()).isEqualTo(TestUserFactory.VALID_USERNAME);
		assertThat(movement.getCreatedBy().getPasswordHash()).isEqualTo(TestUserFactory.VALID_PASSWORD_HASH);
		assertThat(movement.getCreatedBy().getRole()).isEqualTo(TestUserFactory.VALID_ROLE);

		assertThat(movement.getReference()).isNull();
		assertThat(movement.getNote()).isNull();

	}

	@Test
	@DisplayName("Constructor with null item throws exception")
	void constructor_WithNullItem_ThrowsException() {
		LocalDate movementDate = LocalDate.now();
		User createdBy = TestUserFactory.createDefaultUser();

		assertThatThrownBy(() -> new InventoryMovement(
			null,
			TestInventoryMovementFactory.VALID_QUANTITY,
			TestInventoryMovementFactory.VALID_MOVEMENT_TYPE,
			movementDate,
			createdBy
		))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Item cannot be null");
	}

	@Test
	@DisplayName("Constructor with quantity less than 1 throws exception")
	void constructor_WithInvalidQuantity_ThrowsException() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		int invalidQuantity = -1;
		LocalDate movementDate = LocalDate.now();
		User createdBy = TestUserFactory.createDefaultUser();

		assertThatThrownBy(() -> new InventoryMovement(
			item,
			invalidQuantity,
			TestInventoryMovementFactory.VALID_MOVEMENT_TYPE,
			movementDate,
			createdBy
		))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Quantity cannot be less than 1");
	}

	@Test 
	@DisplayName("Constructor with null movement type throws exception")
	void constructor_WithNullMovementType_ThrowsException() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		LocalDate movementDate = LocalDate.now();
		User createdBy = TestUserFactory.createDefaultUser();

		assertThatThrownBy(() -> new InventoryMovement(
			item,
			TestInventoryMovementFactory.VALID_QUANTITY,
			null,
			movementDate,
			createdBy
		))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Movement type cannot be null");
	}

	@Test
	@DisplayName("Constructor with null movement date throws exception")
	void constructor_WithNullMovementDate_ThrowsException() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		User createdBy = TestUserFactory.createDefaultUser();

		assertThatThrownBy(() -> new InventoryMovement(
			item,
			TestInventoryMovementFactory.VALID_QUANTITY,
			TestInventoryMovementFactory.VALID_MOVEMENT_TYPE,
			null,
			createdBy
		))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Movement date cannot be null");
	}

	@Test
	@DisplayName("Constructor with null createdBy throws exception")
	void constructor_WithNullCreatedBy_ThrowsException() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		LocalDate movementDate = LocalDate.now();

		assertThatThrownBy(() -> new InventoryMovement(
			item,
			TestInventoryMovementFactory.VALID_QUANTITY,
			TestInventoryMovementFactory.VALID_MOVEMENT_TYPE,
			movementDate,
			null
		))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Created by cannot be null");
	}

	@Test
	@DisplayName("Constructor with future movement date throws exception")
	void constructor_WithFutureMovementDate_ThrowsException() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		LocalDate futureMovementDate = LocalDate.now().plusDays(1);
		User createdBy = TestUserFactory.createDefaultUser();

		assertThatThrownBy(() -> new InventoryMovement(
			item,
			TestInventoryMovementFactory.VALID_QUANTITY,
			TestInventoryMovementFactory.VALID_MOVEMENT_TYPE,
			futureMovementDate,
			createdBy
		))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Movement date cannot be in the future");
	}

	@Test
	@DisplayName("Set reference should clean input and handle nulls")
	void setReference_SanitizesInput() {
		InventoryMovement movement = TestInventoryMovementFactory.createDefaultInventoryMovement();

		movement.setReference("  valid reference  ");
		assertThat(movement.getReference()).isEqualTo("valid reference");

		movement.setReference(null);
		assertThat(movement.getReference()).isNull();

		movement.setReference("  ");
		assertThat(movement.getReference()).isNull();
	}

	@Test
	@DisplayName("Set reference with too long value throws exception")
	void setReference_WithTooLongValue_ThrowsException() {
		InventoryMovement movement = TestInventoryMovementFactory.createDefaultInventoryMovement();
		String tooLongReference = "x".repeat(101);

		assertThatThrownBy(() -> movement.setReference(tooLongReference))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Reference cannot exceed 100 characters");
	}

	@Test
	@DisplayName("Set note should clean input and handle nulls")
	void setNote_SanitizesInput() {
		InventoryMovement movement = TestInventoryMovementFactory.createDefaultInventoryMovement();

		movement.setNote("  valid note  ");
		assertThat(movement.getNote()).isEqualTo("valid note");

		movement.setNote(null);
		assertThat(movement.getNote()).isNull();

		movement.setNote("  ");
		assertThat(movement.getNote()).isNull();
	}

	@Test
	@DisplayName("Set note with too long value throws exception")
	void setNote_WithTooLongValue_ThrowsException() {
		InventoryMovement movement = TestInventoryMovementFactory.createDefaultInventoryMovement();
		String tooLongNote = "x".repeat(256);

		assertThatThrownBy(() -> movement.setNote(tooLongNote))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Note cannot exceed 255 characters");
	}
}
