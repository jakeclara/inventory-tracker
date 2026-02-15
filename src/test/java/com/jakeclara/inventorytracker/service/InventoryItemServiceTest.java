package com.jakeclara.inventorytracker.service;

import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.dto.InventoryItemDetailsView;
import com.jakeclara.inventorytracker.dto.InventoryItemForm;
import com.jakeclara.inventorytracker.exception.DuplicateNameException;
import com.jakeclara.inventorytracker.exception.DuplicateSkuException;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.repository.InventoryItemRepository;
import com.jakeclara.inventorytracker.util.TestInventoryItemFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;



@ExtendWith(MockitoExtension.class)
class InventoryItemServiceTest {
	
	@Mock
	private InventoryItemRepository inventoryItemRepository;
	
	@InjectMocks
	private InventoryItemService inventoryItemService;

	// Helper method for creating a valid form
	private InventoryItemForm validForm() {
        return new InventoryItemForm(
            "Item A",
            "SKU-123",
            5,
            null
        );
    }

	@Test
	@DisplayName("createInventoryItem should save and return id when form is valid")
	void createInventoryItem_shouldSaveAndReturnId_whenFormIsValid() {
		// Arrange
		InventoryItemForm form = validForm();
		
		when(inventoryItemRepository.existsByName(form.getName()))
			.thenReturn(false);
		
		when(inventoryItemRepository.existsBySku(form.getSku()))
			.thenReturn(false);
		
		InventoryItem savedItem = new InventoryItem(
			form.getName(), 
			form.getSku(), 
			form.getReorderThreshold()
		);
		savedItem.setUnit(form.getUnit());

		// Set fields normally handled by JPA/Hibernate
		ReflectionTestUtils.setField(savedItem, "id", 1L);
		
		// Match any instance because the service creates the entity
		when(inventoryItemRepository.save(any(InventoryItem.class)))
			.thenReturn(savedItem);
		
		// Act
		Long id = inventoryItemService.createInventoryItem(form);
		
		// Assert
		assertThat(id).isEqualTo(1L);

		verify(inventoryItemRepository).existsByName(form.getName());
		verify(inventoryItemRepository).existsBySku(form.getSku());
		verify(inventoryItemRepository).save(any(InventoryItem.class));
	}

	@Test
	@DisplayName("createInventoryItem should throw DuplicateNameException when name already exists")
	void createInventoryItem_shouldThrowDuplicateNameException_whenNameAlreadyExists() {
		// Arrange
		InventoryItemForm form = validForm();
		
		when(inventoryItemRepository.existsByName(form.getName()))
			.thenReturn(true);
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryItemService.createInventoryItem(form))
			.isInstanceOf(DuplicateNameException.class)
			.hasMessageContaining("already exists");
		
		verify(inventoryItemRepository).existsByName(form.getName());
		verify(inventoryItemRepository, never()).existsBySku(form.getSku());
		verify(inventoryItemRepository, never()).save(any());
	}

	@Test
	@DisplayName("createInventoryItem should throw DuplicateSkuException when sku already exists")
	void createInventoryItem_shouldThrowDuplicateSkuException_whenSkuAlreadyExists() {
		// Arrange
		InventoryItemForm form = validForm();
		
		when(inventoryItemRepository.existsByName(form.getName()))
			.thenReturn(false);
		
		when(inventoryItemRepository.existsBySku(form.getSku()))
			.thenReturn(true);
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryItemService.createInventoryItem(form))
			.isInstanceOf(DuplicateSkuException.class)
			.hasMessageContaining("already exists");
		
		verify(inventoryItemRepository).existsByName(form.getName());
		verify(inventoryItemRepository).existsBySku(form.getSku());
		verify(inventoryItemRepository, never()).save(any());
	}

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
	@DisplayName("getItemDetails should return details view")
	void getItemDetails_ShouldReturnDetailsView() {

		// Arrange
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		
		item.setUnit("boxes");
		
		// Set fields normally handled by JPA/Hibernate
		Long id = 1L;
		Instant createdAt = Instant.now();
		ReflectionTestUtils.setField(item, "id", id);
		ReflectionTestUtils.setField(item, "createdAt", createdAt);
		
		Long quantity = 25L;

		when(inventoryItemRepository.findById(id))
			.thenReturn(Optional.of(item));

		when(inventoryItemRepository.findCurrentQuantityByItemId(id))
			.thenReturn(quantity);
		
		// Act
		InventoryItemDetailsView result = inventoryItemService.getItemDetails(id);

		LocalDateTime expectedCreatedAt = createdAt.atZone(ZoneId.systemDefault()).toLocalDateTime();

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(item.getId());
		assertThat(result.name()).isEqualTo(item.getName());
		assertThat(result.sku()).isEqualTo(item.getSku());
		assertThat(result.currentQuantity()).isEqualTo(quantity);
		assertThat(result.reorderThreshold()).isEqualTo(item.getReorderThreshold());
		assertThat(result.unit()).isEqualTo(item.getUnit());
		assertThat(result.isActive()).isEqualTo(item.isActive());
		assertThat(result.createdAt()).isEqualTo(expectedCreatedAt);
		
		verify(inventoryItemRepository).findById(id);
		verify(inventoryItemRepository).findCurrentQuantityByItemId(id);
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
