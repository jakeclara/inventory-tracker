package com.jakeclara.inventorytracker.service;

import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.dto.InventoryDashboardView;
import com.jakeclara.inventorytracker.dto.InventoryItemDetailsView;
import com.jakeclara.inventorytracker.dto.InventoryItemForm;
import com.jakeclara.inventorytracker.exception.DuplicateNameException;
import com.jakeclara.inventorytracker.exception.DuplicateSkuException;
import com.jakeclara.inventorytracker.exception.ResourceNotFoundException;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.repository.InventoryItemRepository;
import com.jakeclara.inventorytracker.util.TestInventoryItemFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;



@ExtendWith(MockitoExtension.class)
class InventoryItemServiceTest {
	
	@Mock
	private InventoryItemRepository inventoryItemRepository;
	
	@InjectMocks
	private InventoryItemService inventoryItemService;

	// Helper method for creating a valid form
	private InventoryItemForm validItemForm() {
        return new InventoryItemForm(
            "Item A",
            "SKU-123",
            5,
            null
        );
    }

	@Test
	@DisplayName("createInventoryItem should save and return id when form is valid")
	void createInventoryItem_ShouldSaveAndReturnId_WhenFormIsValid() {
		// Arrange
		InventoryItemForm form = validItemForm();
		
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
	void createInventoryItem_ShouldThrowDuplicateNameException_WhenNameAlreadyExists() {
		// Arrange
		InventoryItemForm form = validItemForm();
		
		when(inventoryItemRepository.existsByName(form.getName()))
			.thenReturn(true);
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryItemService.createInventoryItem(form))
			.isInstanceOf(DuplicateNameException.class);
		
		verify(inventoryItemRepository).existsByName(form.getName());
		verify(inventoryItemRepository, never()).existsBySku(form.getSku());
		verify(inventoryItemRepository, never()).save(any());
	}

	@Test
	@DisplayName("createInventoryItem should throw DuplicateSkuException when sku already exists")
	void createInventoryItem_ShouldThrowDuplicateSkuException_WhenSkuAlreadyExists() {
		// Arrange
		InventoryItemForm form = validItemForm();
		
		when(inventoryItemRepository.existsByName(form.getName()))
			.thenReturn(false);
		
		when(inventoryItemRepository.existsBySku(form.getSku()))
			.thenReturn(true);
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryItemService.createInventoryItem(form))
			.isInstanceOf(DuplicateSkuException.class);
		
		verify(inventoryItemRepository).existsByName(form.getName());
		verify(inventoryItemRepository).existsBySku(form.getSku());
		verify(inventoryItemRepository, never()).save(any());
	}

	@Test
	@DisplayName("activateInventoryItem should set item to active when item exists")
	void activateInventoryItem_ShouldSetItemToActive_WhenItemExists() {
		// Arrange
		Long id = 1L;
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		ReflectionTestUtils.setField(item, "id", id);

		// Ensure item is inactive
		item.setIsActive(false);
		
		when(inventoryItemRepository.findById(id))
			.thenReturn(Optional.of(item));
		
		// Act
		inventoryItemService.activateInventoryItem(id);
		
		// Assert
		assertThat(item.isActive()).isTrue();

		verify(inventoryItemRepository).findById(id);
	}

	@Test
	@DisplayName("activateInventoryItem should throw ResourceNotFoundException when item does not exist")
	void activateInventoryItem_ShouldThrowResourceNotFoundException_WhenItemDoesNotExist() {
		// Arrange
		Long id = 999L;
		
		when(inventoryItemRepository.findById(id))
			.thenReturn(Optional.empty());
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryItemService.activateInventoryItem(id))
			.isInstanceOf(ResourceNotFoundException.class);
		
		verify(inventoryItemRepository).findById(id);
	}

	@Test
	@DisplayName("deactivateInventoryItem should set item to inactive when item exists")
	void deactivateInventoryItem_ShouldSetItemToInactive_WhenItemExists() {
		// Arrange
		Long id = 1L;
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		ReflectionTestUtils.setField(item, "id", id);

		// Ensure item is active
		item.setIsActive(true);
		
		when(inventoryItemRepository.findById(id))
			.thenReturn(Optional.of(item));
		
		// Act
		inventoryItemService.deactivateInventoryItem(id);
		
		// Assert
		assertThat(item.isActive()).isFalse();

		verify(inventoryItemRepository).findById(id);
	}

	@Test
	@DisplayName("deactivateInventoryItem should throw ResourceNotFoundException when item does not exist")
	void deactivateInventoryItem_ShouldThrowResourceNotFoundException_WhenItemDoesNotExist() {
		// Arrange
		Long id = 999L;
		
		when(inventoryItemRepository.findById(id))
			.thenReturn(Optional.empty());
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryItemService.deactivateInventoryItem(id))
			.isInstanceOf(ResourceNotFoundException.class);
		
		verify(inventoryItemRepository).findById(id);
	}

	@Test
	@DisplayName("updateInventoryItem should update fields when input is valid")
	void updateInventoryItem_ShouldUpdateFields_WhenInputIsValid() {
		// Arrange
		Long id = 1L;
		InventoryItem existingItem = TestInventoryItemFactory.createDefaultItem();
		ReflectionTestUtils.setField(existingItem, "id", id);
		// Sku should not change when updating
		String originalSku = existingItem.getSku();
		
		InventoryItemForm form = new InventoryItemForm(
			"Updated Name",
			originalSku,
			15,
			"boxes"
		);
		
		when(inventoryItemRepository.findById(id))
			.thenReturn(Optional.of(existingItem));
		
		when(inventoryItemRepository.existsByNameAndIdNot(form.getName(), id))
			.thenReturn(false);
		
		// Act
		inventoryItemService.updateInventoryItem(id, form);
		
		// Assert
		assertThat(existingItem.getName()).isEqualTo(form.getName());
		assertThat(existingItem.getSku()).isEqualTo(originalSku);
		assertThat(existingItem.getReorderThreshold()).isEqualTo(form.getReorderThreshold());
		assertThat(existingItem.getUnit()).isEqualTo(form.getUnit());
		
		verify(inventoryItemRepository).findById(id);
		verify(inventoryItemRepository).existsByNameAndIdNot(form.getName(), id);
	}

	@Test
	@DisplayName("updateInventoryItem should throw DuplicateNameException when name already exists")
	void updateInventoryItem_ShouldThrowDuplicateNameException_WhenNameAlreadyExists() {
		// Arrange
		Long id = 999L;
		InventoryItem existingItem = TestInventoryItemFactory.createDefaultItem();
		ReflectionTestUtils.setField(existingItem, "id", id);
		
		InventoryItemForm form = validItemForm();
		
		when(inventoryItemRepository.findById(id))
			.thenReturn(Optional.of(existingItem));
		
		when(inventoryItemRepository.existsByNameAndIdNot(form.getName(), id))
			.thenReturn(true);
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryItemService.updateInventoryItem(id, form))
			.isInstanceOf(DuplicateNameException.class);
		
		verify(inventoryItemRepository).findById(id);
		verify(inventoryItemRepository).existsByNameAndIdNot(form.getName(), id);
	}

	@Test
	@DisplayName("updateInventoryItem should throw ResourceNotFoundException when item does not exist")
	void updateInventoryItem_ShouldThrowResourceNotFoundException_WhenItemDoesNotExist() {
		// Arrange
		Long id = 999L;
		InventoryItemForm form = validItemForm();
		
		when(inventoryItemRepository.findById(id))
			.thenReturn(Optional.empty());
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryItemService.updateInventoryItem(id, form))
			.isInstanceOf(ResourceNotFoundException.class);
		
		verify(inventoryItemRepository).findById(id);
		verify(inventoryItemRepository, never()).existsByNameAndIdNot(any(), any());
	}

	@Test
	@DisplayName("getInventoryItemById should return item when it exists")
	void getInventoryItemById_ShouldReturnItem_WhenItemExists() {
		// Arrange
		Long id = 1L;
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		ReflectionTestUtils.setField(item, "id", id);
		
		when(inventoryItemRepository.findById(id))
			.thenReturn(Optional.of(item));
		
		// Act
		InventoryItem result = inventoryItemService.getInventoryItemById(id);
		
		// Assert
		assertThat(result).isSameAs(item);
		
		verify(inventoryItemRepository).findById(id);
	}

	@Test
	@DisplayName("getInventoryItemById should throw ResourceNotFoundException when item does not exist")
	void getInventoryItemById_ShouldThrowResourceNotFoundException_WhenItemDoesNotExist() {
		// Arrange
		Long id = 999L;
		
		when(inventoryItemRepository.findById(id))
			.thenReturn(Optional.empty());
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryItemService.getInventoryItemById(id))
			.isInstanceOf(ResourceNotFoundException.class);
		
		verify(inventoryItemRepository).findById(id);
	}

	@Test
	@DisplayName("getCurrentQuantity should return correct quantity")
	void getCurrentQuantity_ShouldReturnCorrectQuantity() {
		// Arrange
		Long itemId = 1L;
		Long quantity = 25L;

		when(inventoryItemRepository.findCurrentQuantityByItemId(itemId))
			.thenReturn(quantity);

		// Act
		Long result = inventoryItemService.getCurrentQuantity(itemId);

		// Assert
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
	@DisplayName("getItemDetails should throw ResourceNotFoundException when item does not exist")
	void getItemDetails_ShouldThrowResourceNotFoundException_WhenItemDoesNotExist() {
		// Arrange
		Long id = 999L;

		when(inventoryItemRepository.findById(id))
			.thenReturn(Optional.empty());
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryItemService.getItemDetails(id))
			.isInstanceOf(ResourceNotFoundException.class);
		
		verify(inventoryItemRepository).findById(id);
		verify(inventoryItemRepository, never()).findCurrentQuantityByItemId(any());
	}

	@Test
	@DisplayName("getInactiveItems should return paginated inactive items with low stock count")
	void getInactiveItems_ShouldReturnPaginatedInactiveItems() {
		// Arrange
		int page = 0;

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

		List<InventoryDashboardItem> content = List.of(itemA, itemB);

		Page<InventoryDashboardItem> itemPage =
			new PageImpl<>(content, PageRequest.of(page, 10), content.size());

		when(inventoryItemRepository
			.findInventoryByActiveStatusWithQuantity(eq(false), any(PageRequest.class)))
			.thenReturn(itemPage);

		when(inventoryItemRepository.countLowStockByActiveStatus(false))
			.thenReturn(1L);

		// Act
		InventoryDashboardView result =
			inventoryItemService.getInactiveItems(page);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.inventoryItems()).hasSize(2);
		assertThat(result.lowStockCount()).isEqualTo(1L);
		assertThat(result.pagination().currentPage()).isZero();

		verify(inventoryItemRepository)
			.findInventoryByActiveStatusWithQuantity(eq(false), any(PageRequest.class));

		verify(inventoryItemRepository)
			.countLowStockByActiveStatus(false);
	}

	@Test
	@DisplayName("getInactiveItems should default to page 0 when negative page provided")
	void getInactiveItems_ShouldDefaultToZero_WhenNegativePageProvided() {
		// Arrange
		when(inventoryItemRepository
			.findInventoryByActiveStatusWithQuantity(eq(false), any(PageRequest.class)))
			.thenReturn(Page.empty());

		when(inventoryItemRepository.countLowStockByActiveStatus(false))
			.thenReturn(0L);

		// Act
		inventoryItemService.getInactiveItems(-5);

		ArgumentCaptor<PageRequest> pageCaptor =
			ArgumentCaptor.forClass(PageRequest.class);

		verify(inventoryItemRepository)
			.findInventoryByActiveStatusWithQuantity(eq(false), pageCaptor.capture());

		// Assert
		assertThat(pageCaptor.getValue().getPageNumber()).isZero();
	}
}
