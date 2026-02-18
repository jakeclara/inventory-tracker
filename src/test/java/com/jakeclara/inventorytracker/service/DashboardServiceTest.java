package com.jakeclara.inventorytracker.service;

import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.dto.InventoryDashboardView;
import com.jakeclara.inventorytracker.repository.InventoryItemRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

	private static final int DEFAULT_PAGE_SIZE = 10;

	@Mock
	private InventoryItemRepository inventoryItemRepository;

	@InjectMocks
	private DashboardService dashboardService;

	@Test
	@DisplayName("getInventoryDashboard returns dashboard view with repository data")
	void getInventoryDashboard_ReturnsDashboardView() {
		// Arrange
		InventoryDashboardItem item =
			new InventoryDashboardItem(
				1L,
				"Item A",
				"SKU-123",
				5L,
				10,
				"pcs"
			);

		Page<InventoryDashboardItem> page =
			new PageImpl<>(List.of(item), PageRequest.of(0, DEFAULT_PAGE_SIZE), 1);

		when(inventoryItemRepository
			.findInventoryByActiveStatusWithQuantity(true, PageRequest.of(0, DEFAULT_PAGE_SIZE)))
			.thenReturn(page);

		when(inventoryItemRepository.countLowStockByActiveStatus(true))
			.thenReturn(1L);

		// Act
		InventoryDashboardView result =
			dashboardService.getInventoryDashboard(0);

		// Assert
		assertThat(result.inventoryItems()).hasSize(1);
		assertThat(result.lowStockCount()).isOne();
		
		assertThat(result.pagination().currentPage()).isZero();
		assertThat(result.pagination().totalPages()).isOne();
		assertThat(result.pagination().hasNext()).isFalse();
		assertThat(result.pagination().hasPrevious()).isFalse();


		verify(inventoryItemRepository)
			.findInventoryByActiveStatusWithQuantity(true, PageRequest.of(0, DEFAULT_PAGE_SIZE));

		verify(inventoryItemRepository)
			.countLowStockByActiveStatus(true);
	}

	@Test
	@DisplayName("getInventoryDashboard clamps negative page number to zero")
	void getInventoryDashboard_ClampsNegativePageToZero() {
		// Arrange
		Page<InventoryDashboardItem> page =
			new PageImpl<>(List.of(), PageRequest.of(0, DEFAULT_PAGE_SIZE), 0);

		when(inventoryItemRepository
			.findInventoryByActiveStatusWithQuantity(true, PageRequest.of(0, DEFAULT_PAGE_SIZE)))
			.thenReturn(page);

		when(inventoryItemRepository.countLowStockByActiveStatus(true))
			.thenReturn(0L);

		// Act
		InventoryDashboardView result =
			dashboardService.getInventoryDashboard(-5);

		// Assert
		assertThat(result.pagination().currentPage()).isZero();

		verify(inventoryItemRepository)
			.findInventoryByActiveStatusWithQuantity(true, PageRequest.of(0, DEFAULT_PAGE_SIZE));
	}

	@Test
	@DisplayName("getInventoryDashboard returns empty dashboard when no items found")
	void getInventoryDashboard_ReturnsEmptyDashboard_WhenNoItemsFound() {
		// Arrange
		Page<InventoryDashboardItem> page =
			new PageImpl<>(List.of(), PageRequest.of(0, DEFAULT_PAGE_SIZE), 0);

		when(inventoryItemRepository
			.findInventoryByActiveStatusWithQuantity(true, PageRequest.of(0, DEFAULT_PAGE_SIZE)))
			.thenReturn(page);

		when(inventoryItemRepository.countLowStockByActiveStatus(true))
			.thenReturn(0L);

		// Act
		InventoryDashboardView result =
			dashboardService.getInventoryDashboard(0);

		// Assert
		assertThat(result.inventoryItems()).isEmpty();
		assertThat(result.lowStockCount()).isZero();
	}
}