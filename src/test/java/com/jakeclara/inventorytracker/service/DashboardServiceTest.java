package com.jakeclara.inventorytracker.service;

import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.dto.InventoryDashboardView;
import com.jakeclara.inventorytracker.repository.InventoryItemRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

	@Mock
	private InventoryItemRepository inventoryItemRepository;

	@InjectMocks
	private DashboardService dashboardService;

	@Test
	@DisplayName("getInventoryDashboard should calculate lowStock correctly")
	void getInventoryDashboard_ShouldCalculateLowStockCorrectly() {
		
		// Arrange

		// Not low stock
		InventoryDashboardItem itemA = new InventoryDashboardItem(
			1L, 
			"Item A", 
			"SKU-123", 
			10L, 
			5, 
			"pcs"
		);

		// Low stock
		InventoryDashboardItem itemB = new InventoryDashboardItem(
			2L, 
			"Item B", 
			"SKU-456", 
			4L, 
			5, 
			"each"
		);

		// Low stock
		InventoryDashboardItem itemC = new InventoryDashboardItem(
			3L, 
			"Item C", 
			"SKU-789", 
			0L, 
			5, 
			"each"
		);

		List<InventoryDashboardItem> items = List.of(itemA, itemB, itemC);
		when(inventoryItemRepository.findActiveInventoryWithQuantity()).thenReturn(items);

		// Act
		InventoryDashboardView results = dashboardService.getInventoryDashboard();

		// Assert
		assertThat(results.inventoryItems()).hasSize(3);
		assertThat(results.lowStockCount()).isEqualTo(2);

		verify(inventoryItemRepository).findActiveInventoryWithQuantity();
	}

	@Test
	@DisplayName("getInventoryDashboard should return empty list when no items found")
	void getInventoryDashboard_ShouldReturnEmptyList_WhenNoItemsFound() {
		
		when(inventoryItemRepository.findActiveInventoryWithQuantity()).thenReturn(List.of());

		InventoryDashboardView results = dashboardService.getInventoryDashboard();

		assertThat(results.inventoryItems()).isEmpty();
		assertThat(results.lowStockCount()).isZero();

		verify(inventoryItemRepository).findActiveInventoryWithQuantity();
	}

	@Test
	@DisplayName("getInventoryDashboard should return 0 lowStockCount when no items are low stock")
	void getInventoryDashboard_ShouldReturn0LowStockCount_WhenNoItemsAreLowStock() {
		
		InventoryDashboardItem itemA = new InventoryDashboardItem(
			1L, 
			"Item A", 
			"SKU-123", 
			15L, 
			10, 
			"pcs"
		);

		InventoryDashboardItem itemB = new InventoryDashboardItem(
			2L, 
			"Item B", 
			"SKU-456", 
			12L, 
			10, 
			"each"
		);

		List<InventoryDashboardItem> items = List.of(itemA, itemB);
		when(inventoryItemRepository.findActiveInventoryWithQuantity()).thenReturn(items);

		InventoryDashboardView results = dashboardService.getInventoryDashboard();

		assertThat(results.inventoryItems()).hasSize(2);
		assertThat(results.lowStockCount()).isZero();

		verify(inventoryItemRepository).findActiveInventoryWithQuantity();
	}

	@Test
	@DisplayName("getInventoryDashboard should handle threshold boundary correctly")
	void getInventoryDashboard_ShouldHandleThresholdBoundaryCorrectly() {
		
		InventoryDashboardItem itemA = new InventoryDashboardItem(
			1L, 
			"Item A", 
			"SKU-123", 
			5L, 
			5, 
			"pcs"
		);

		InventoryDashboardItem itemB = new InventoryDashboardItem(
			2L, 
			"Item B", 
			"SKU-456", 
			10L, 
			10, 
			"each"
		);

		List<InventoryDashboardItem> items = List.of(itemA, itemB);
		when(inventoryItemRepository.findActiveInventoryWithQuantity()).thenReturn(items);

		InventoryDashboardView results = dashboardService.getInventoryDashboard();

		assertThat(results.inventoryItems()).hasSize(2);
		assertThat(results.lowStockCount()).isZero();

		verify(inventoryItemRepository).findActiveInventoryWithQuantity();
	}
}
