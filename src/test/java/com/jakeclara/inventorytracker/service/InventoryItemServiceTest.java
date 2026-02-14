package com.jakeclara.inventorytracker.service;

import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.repository.InventoryItemRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
class InventoryItemServiceTest {
	
	@Mock
	private InventoryItemRepository inventoryItemRepository;
	
	@InjectMocks
	private InventoryItemService inventoryItemService;

	@Test
	@DisplayName("getCurrentQuantity should return quantity from repository")
	void getCurrentQuantity_ShouldReturnQuantityFromRepository() {

		Long itemId = 1L;
		Long quantity = 25L;

		when(inventoryItemRepository.findCurrentQuantityByItemId(itemId))
			.thenReturn(quantity);

		Long result = inventoryItemService.getCurrentQuantity(itemId);

		assertThat(result).isEqualTo(quantity);

		verify(inventoryItemRepository)
			.findCurrentQuantityByItemId(itemId);
	}

	@Test
	@DisplayName("getInactiveItems should return inactive items from repository")
	void getInactiveItems_ShouldReturnInactiveItemsFromRepository() {
		
		InventoryDashboardItem itemA = new InventoryDashboardItem(
			1L, 
			"Item A", 
			"SKU-123", 
			10L, 
			5, 
			"pcs"
		);

		InventoryDashboardItem itemB = new InventoryDashboardItem(
			2L, 
			"Item B", 
			"SKU-456", 
			4L, 
			5, 
			"each"
		);

		List<InventoryDashboardItem> items = List.of(itemA, itemB);
		when(inventoryItemRepository.findInactiveInventoryWithQuantity()).thenReturn(items);

		List<InventoryDashboardItem> results = inventoryItemService.getInactiveItems();

		assertThat(results).isSameAs(items);

		verify(inventoryItemRepository).findInactiveInventoryWithQuantity();
	}

}
