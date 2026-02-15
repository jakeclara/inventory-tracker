package com.jakeclara.inventorytracker.service;

import com.jakeclara.inventorytracker.exception.InsufficientStockException;
import com.jakeclara.inventorytracker.repository.InventoryMovementRepository;
import com.jakeclara.inventorytracker.security.AuthenticatedUserProvider;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class InventoryMovementServiceTest {

	@Mock
	InventoryMovementRepository inventoryMovementRepository;

	@Mock
	InventoryItemService inventoryItemService;
	
	@Mock
	AuthenticatedUserProvider authenticatedUserProvider;
	
	@InjectMocks
	InventoryMovementService inventoryMovementService;

	@Test
	@DisplayName("ensureSufficientStock should throw when stock is insufficient")
	void ensureSufficientStockShouldThrowWhenStockIsInsufficient() {
		// Arrange
		Long itemId = 999L;
		Long currentQuantity = 5L;
		int quantityDelta = -10;

		when(inventoryItemService.getCurrentQuantity(itemId))
			.thenReturn(currentQuantity);
		
		// Act & Assert
		assertThatThrownBy(() -> 
			inventoryMovementService.ensureSufficientStock(itemId, quantityDelta))
			.isInstanceOf(InsufficientStockException.class)
			.hasMessageContaining("Insufficient stock");

		verify(inventoryItemService).getCurrentQuantity(itemId);
	}

	@Test
	@DisplayName("ensureSufficientStock should not throw when stock is sufficient")
	void ensureSufficientStockShouldNotThrowWhenStockIsSufficient() {
		// Arrange
		Long itemId = 1L;
		Long currentQuantity = 25L;
		int quantityDelta = -5;

		when(inventoryItemService.getCurrentQuantity(itemId))
			.thenReturn(currentQuantity);
		
		// Act & Assert
		assertThatCode(() -> 
			inventoryMovementService.ensureSufficientStock(itemId, quantityDelta))
			.doesNotThrowAnyException();

		verify(inventoryItemService).getCurrentQuantity(itemId);
	}
	
}
