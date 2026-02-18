package com.jakeclara.inventorytracker.service;

import com.jakeclara.inventorytracker.dto.InventoryMovementForm;
import com.jakeclara.inventorytracker.dto.InventoryMovementView;
import com.jakeclara.inventorytracker.exception.InactiveItemException;
import com.jakeclara.inventorytracker.exception.InsufficientStockException;
import com.jakeclara.inventorytracker.exception.ResourceNotFoundException;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.model.InventoryMovement;
import com.jakeclara.inventorytracker.model.InventoryMovementType;
import com.jakeclara.inventorytracker.model.User;
import com.jakeclara.inventorytracker.repository.InventoryMovementRepository;
import com.jakeclara.inventorytracker.security.AuthenticatedUserProvider;
import com.jakeclara.inventorytracker.util.TestInventoryItemFactory;
import com.jakeclara.inventorytracker.util.TestInventoryMovementFactory;
import com.jakeclara.inventorytracker.util.TestUserFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
class InventoryMovementServiceTest {

	@Mock
	InventoryMovementRepository inventoryMovementRepository;

	@Mock
	InventoryItemService inventoryItemService;
	
	@Mock
	AuthenticatedUserProvider authenticatedUserProvider;
	
	@InjectMocks
	InventoryMovementService inventoryMovementService;

	private InventoryMovementForm validMovementForm() {
		return new InventoryMovementForm(
			10, 
			InventoryMovementType.RECEIVE,
			LocalDate.now(), 
			null,
			null
		);
	}

	@Test
	@DisplayName("addInventoryMovement should save movement when valid with note and reference provided")
	void addInventoryMovement_ShouldSaveMovement_WhenValidWithOptionalFields() {
		// Arrange
		Long id = 1L;
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		ReflectionTestUtils.setField(item, "id", id);
		item.setIsActive(true);

		User user = TestUserFactory.createDefaultUser();

		InventoryMovementForm form = new InventoryMovementForm(
			10, 
			InventoryMovementType.RECEIVE, 
			LocalDate.now(), 
			"reference", 
			"note"
		);
		
		when(inventoryItemService.getInventoryItemById(id))
			.thenReturn(item);
		
		when(authenticatedUserProvider.getAuthenticatedUser())
			.thenReturn(user);

		when(inventoryMovementRepository.save(any()))
			.thenAnswer(invocation -> invocation.getArgument(0));
		
		// Act
		inventoryMovementService.addInventoryMovement(id, form);
		
		// Assert
		ArgumentCaptor<InventoryMovement> movementCaptor = 
			ArgumentCaptor.forClass(InventoryMovement.class);
		
		verify(inventoryMovementRepository).save(movementCaptor.capture());
		
		InventoryMovement movement = movementCaptor.getValue();
		
		assertThat(movement.getItem()).isEqualTo(item);
		assertThat(movement.getQuantity()).isEqualTo(form.quantity());
		assertThat(movement.getMovementType()).isEqualTo(form.movementType());
		assertThat(movement.getMovementDate()).isEqualTo(form.movementDate());
		assertThat(movement.getCreatedBy()).isEqualTo(user);

		// Optional fields should be set
		assertThat(movement.getReference()).isEqualTo(form.reference());
		assertThat(movement.getNote()).isEqualTo(form.note());

		verify(inventoryItemService).getInventoryItemById(id);
		verify(authenticatedUserProvider).getAuthenticatedUser();
	}

	@Test
	@DisplayName("addInventoryMovement should save movement when valid and no note/reference provided")
	void addInventoryMovement_ShouldSaveMovement_WhenValidWithoutOptionalFields() {
		// Arrange
		Long id = 1L;
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		ReflectionTestUtils.setField(item, "id", id);
		item.setIsActive(true);

		User user = TestUserFactory.createDefaultUser();

		InventoryMovementForm form = validMovementForm();
		
		when(inventoryItemService.getInventoryItemById(id))
			.thenReturn(item);
		
		when(authenticatedUserProvider.getAuthenticatedUser())
			.thenReturn(user);

		when(inventoryMovementRepository.save(any()))
			.thenAnswer(invocation -> invocation.getArgument(0));
		
		// Act
		inventoryMovementService.addInventoryMovement(id, form);
		
		// Assert
		ArgumentCaptor<InventoryMovement> movementCaptor = 
			ArgumentCaptor.forClass(InventoryMovement.class);
		
		verify(inventoryMovementRepository).save(movementCaptor.capture());
		
		InventoryMovement movement = movementCaptor.getValue();
		
		assertThat(movement.getItem()).isEqualTo(item);
		assertThat(movement.getQuantity()).isEqualTo(form.quantity());
		assertThat(movement.getMovementType()).isEqualTo(form.movementType());
		assertThat(movement.getMovementDate()).isEqualTo(form.movementDate());
		assertThat(movement.getCreatedBy()).isEqualTo(user);

		// Optional fields should be null
		assertThat(movement.getReference()).isNull();
		assertThat(movement.getNote()).isNull();

		verify(inventoryItemService).getInventoryItemById(id);
		verify(authenticatedUserProvider).getAuthenticatedUser();
	}

	@Test
	@DisplayName("addInventoryMovement should throw InsufficientStockException when stock is insufficient")
	void addInventoryMovement_ShouldThrow_WhenStockIsInsufficient() {
		// Arrange
		Long id = 1L;
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		ReflectionTestUtils.setField(item, "id", id);
		item.setIsActive(true);

		InventoryMovementForm form =  new InventoryMovementForm(
			10, 
			InventoryMovementType.SALE, 
			LocalDate.now(), 
			null, 
			null
		);
		
		when(inventoryItemService.getInventoryItemById(id))
			.thenReturn(item);
		
		// Stock is 5 and movement asks for -10
		when(inventoryItemService.getCurrentQuantity(id))
			.thenReturn(5L);
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryMovementService.addInventoryMovement(id, form))
			.isInstanceOf(InsufficientStockException.class);
		
		verify(inventoryItemService).getInventoryItemById(id);
		verify(inventoryItemService).getCurrentQuantity(id);

		verifyNoInteractions(authenticatedUserProvider);
		verifyNoInteractions(inventoryMovementRepository);
	}

	@Test
	@DisplayName("addInventoryMovement should throw InactiveItemException when item is inactive")
	void addInventoryMovement_ShouldThrow_WhenItemIsInactive() {
		// Arrange
		Long id = 1L;
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		ReflectionTestUtils.setField(item, "id", id);
		item.setIsActive(false);

		InventoryMovementForm form = validMovementForm();
		
		when(inventoryItemService.getInventoryItemById(id))
			.thenReturn(item);
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryMovementService.addInventoryMovement(id, form))
			.isInstanceOf(InactiveItemException.class);
		
		verify(inventoryItemService).getInventoryItemById(id);

		// These should not be called
		verify(inventoryItemService, never()).getCurrentQuantity(any());
		verifyNoInteractions(inventoryMovementRepository);
		verifyNoInteractions(authenticatedUserProvider);
	}

	@Test
	@DisplayName("addInventoryMovement should throw ResourceNotFoundException when item does not exist")
	void addInventoryMovement_ShouldThrow_WhenItemDoesNotExist() {
		// Arrange
		Long id = 999L;

		InventoryMovementForm form = validMovementForm();

		when(inventoryItemService.getInventoryItemById(id))
			.thenThrow(new ResourceNotFoundException("not found"));
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryMovementService.addInventoryMovement(id, form))
			.isInstanceOf(ResourceNotFoundException.class);
		
		verify(inventoryItemService).getInventoryItemById(id);
		verifyNoInteractions(inventoryMovementRepository);
		verifyNoInteractions(authenticatedUserProvider);
	}

	@Test
	@DisplayName("getMovementsForItem should return mapped movements for item")
	void getMovementsForItem_ShouldReturnMappedMovementsForItem() {
		// Arrange
		Long id = 1L;
		int page = 0;

		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		ReflectionTestUtils.setField(item, "id", id);

		User createdBy = TestUserFactory.createDefaultUser();

		InventoryMovement movement = TestInventoryMovementFactory.createInventoryMovement(
			item, 
			10, 
			InventoryMovementType.RECEIVE, 
			LocalDate.now(), 
			createdBy
		);
		movement.setReference("Invoice #12345");

		when(inventoryItemService.getInventoryItemById(id))
			.thenReturn(item);
		
		Page<InventoryMovement> movementPage = new PageImpl<>(List.of(movement));

		when(inventoryMovementRepository
			.findByItemIdOrderByMovementDateDesc(eq(id), any(PageRequest.class)))
			.thenReturn(movementPage);
		
		// Act
		Page<InventoryMovementView> result = 
			inventoryMovementService.getMovementsForItem(id, page);
		
		// Assert
		assertThat(result.getContent()).hasSize(1);

		InventoryMovementView view = result.getContent().get(0);
		assertThat(view.movementType()).isEqualTo(movement.getMovementType());
		assertThat(view.quantity()).isEqualTo(movement.getQuantity());
		assertThat(view.movementDate()).isEqualTo(movement.getMovementDate());
		assertThat(view.reference()).isEqualTo(movement.getReference());
		assertThat(view.createdBy()).isEqualTo(createdBy.getUsername());

		verify(inventoryItemService).getInventoryItemById(id);
		verify(inventoryMovementRepository)
			.findByItemIdOrderByMovementDateDesc(eq(id), any(PageRequest.class));
	}

	@Test
	@DisplayName("getMovementsForItem should throw ResourceNotFoundException when item does not exist")
	void getMovementsForItem_ShouldThrow_WhenItemDoesNotExist() {
		// Arrange
		Long id = 999L;
		
		when(inventoryItemService.getInventoryItemById(id))
			.thenThrow(new ResourceNotFoundException("not found"));
		
		// Act & Assert
		assertThatThrownBy(() -> inventoryMovementService.getMovementsForItem(id, 0))
			.isInstanceOf(ResourceNotFoundException.class);
		
		verify(inventoryItemService).getInventoryItemById(id);
		
		verify(inventoryMovementRepository, never())
			.findByItemIdOrderByMovementDateDesc(any(), any());
	}

	@Test
	@DisplayName("getMovementsForItem should use page 0 when negative page provided")
	void getMovementsForItem_ShouldDefaultToZero_WhenNegativePageProvided() {
		// Arrange
		Long id = 1L;

		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		ReflectionTestUtils.setField(item, "id", id);

		when(inventoryItemService.getInventoryItemById(id))
			.thenReturn(item);

		when(inventoryMovementRepository
			.findByItemIdOrderByMovementDateDesc(eq(id), any(PageRequest.class)))
			.thenReturn(Page.empty());

		// Act
		inventoryMovementService.getMovementsForItem(id, -5);

		ArgumentCaptor<PageRequest> pageCaptor =
			ArgumentCaptor.forClass(PageRequest.class);

		verify(inventoryMovementRepository)
			.findByItemIdOrderByMovementDateDesc(eq(id), pageCaptor.capture());

		// Assert
		assertThat(pageCaptor.getValue().getPageNumber()).isZero();
	}

	@Test
	@DisplayName("ensureSufficientStock should not throw when stock is sufficient")
	void ensureSufficientStock_ShouldNotThrow_WhenStockIsSufficient() {
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

	@Test
	@DisplayName("ensureSufficientStock should throw when stock is insufficient")
	void ensureSufficientStock_ShouldThrow_WhenStockIsInsufficient() {
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
			.hasMessageContaining("current stock is 5")
			.hasMessageContaining("requested quantity is -10");

		verify(inventoryItemService).getCurrentQuantity(itemId);
	}
}
