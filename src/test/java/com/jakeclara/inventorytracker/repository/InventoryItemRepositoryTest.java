package com.jakeclara.inventorytracker.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.model.InventoryMovement;
import com.jakeclara.inventorytracker.model.InventoryMovementType;
import com.jakeclara.inventorytracker.model.User;
import com.jakeclara.inventorytracker.util.TestInventoryItemFactory;
import com.jakeclara.inventorytracker.util.TestInventoryMovementFactory;
import com.jakeclara.inventorytracker.util.TestUserFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


import java.time.LocalDate;
import java.util.List;


@DataJpaTest
class InventoryItemRepositoryTest {

	private static final int PAGE_SIZE = 10;

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private InventoryItemRepository inventoryItemRepository;

	@Test
	@DisplayName("existsByName returns true when an item with name exists")
	void existsByName_ReturnsTrue_WhenItemWithNameExists() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		entityManager.persistAndFlush(item);
		entityManager.clear();

		boolean exists = inventoryItemRepository.existsByName(item.getName());
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("existsByName returns false when no item with name exists")
	void existsByName_ReturnsFalse_WhenNoItemWithNameExists() {
		boolean exists = inventoryItemRepository.existsByName("NonExistentItem");
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("existsByNameAndIdNot returns false when same id")
	void existsByNameAndIdNot_ReturnsFalse_WhenSameId() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		entityManager.persistAndFlush(item);
		entityManager.clear();

		boolean exists = inventoryItemRepository.existsByNameAndIdNot(item.getName(), item.getId());
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("existsByNameAndIdNot returns false when name does not exist")
	void existsByNameAndIdNot_ReturnsFalse_WhenNameDoesNotExist() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		entityManager.persistAndFlush(item);
		entityManager.clear();

		boolean exists = inventoryItemRepository.existsByNameAndIdNot("NonExistentItem", item.getId());
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("existsByNameAndIdNot returns true when another item has same name")
	void existsByNameAndIdNot_ReturnsTrue_WhenDifferentItemHasSameName() {
		InventoryItem itemA = TestInventoryItemFactory.createDefaultItem();
		entityManager.persistAndFlush(itemA);
		

		InventoryItem itemB = TestInventoryItemFactory.createItem(
			"MacBook Air", 
			"MBA-123", 
			5
		);
		entityManager.persistAndFlush(itemB);
		entityManager.clear();

		boolean exists = inventoryItemRepository.existsByNameAndIdNot(itemA.getName(), itemB.getId());
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("existsBySku returns true when an item with sku exists")
	void existsBySku_ReturnsTrue_WhenItemWithSkuExists() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		entityManager.persistAndFlush(item);
		entityManager.clear();

		boolean exists = inventoryItemRepository.existsBySku(item.getSku());
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("existsBySku returns false when no item with sku exists")
	void existsBySku_ReturnsFalse_WhenNoItemWithSkuExists() {
		boolean exists = inventoryItemRepository.existsBySku("NON-EXISTENT-SKU");
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("findInventoryByActiveStatusWithQuantity returns 0 when no movements exits")
	void findInventoryByActiveStatusWithQuantity_Returns0_WhenNoMovementsExits() {
		// Arrange
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		entityManager.persistAndFlush(item);
		entityManager.clear();

		Page<InventoryDashboardItem> page = 
			inventoryItemRepository.findInventoryByActiveStatusWithQuantity(
				true,
				PageRequest.of(0, PAGE_SIZE)
			);
		
		// Act
		List<InventoryDashboardItem> results = page.getContent();
		
		// Assert
		assertThat(page.getTotalElements()).isEqualTo(1);
		
		assertThat(results).hasSize(1);
		
		InventoryDashboardItem dto = results.get(0);
		assertThat(dto.name()).isEqualTo(item.getName());
		assertThat(dto.currentQuantity()).isZero();
	}

	@Test
	@DisplayName("findInventoryByActiveStatusWithQuantity returns correct quantity with mixed movements")
	void findInventoryByActiveStatusWithQuantity_ReturnsCorrectQuantity_WithMixedMovements() {
		// Arrange
		InventoryItem item = entityManager.persist(TestInventoryItemFactory.createDefaultItem());

		User user = entityManager.persist(TestUserFactory.createDefaultUser());

		InventoryMovement receiveMovement = 
			TestInventoryMovementFactory.createInventoryMovement(
				item, 
				10, 
				InventoryMovementType.ADJUST_IN, 
				LocalDate.now(), 
				user
			);

		InventoryMovement saleMovement =
			TestInventoryMovementFactory.createInventoryMovement(
				item, 
				5, 
				InventoryMovementType.ADJUST_OUT, 
				LocalDate.now(), 
				user
			);

		entityManager.persist(receiveMovement);
		entityManager.persist(saleMovement);
		entityManager.flush();
		entityManager.clear();

		// Act
		Page<InventoryDashboardItem> page = 
			inventoryItemRepository.findInventoryByActiveStatusWithQuantity(
				true,
				PageRequest.of(0, PAGE_SIZE)
			);
		
		List<InventoryDashboardItem> results = page.getContent();
		
		// Assert
		assertThat(page.getTotalElements()).isEqualTo(1);

		assertThat(results).hasSize(1);

		InventoryDashboardItem dto = results.get(0);
		assertThat(dto.name()).isEqualTo(item.getName());
		assertThat(dto.currentQuantity()).isEqualTo(5L);
	}

	@Test
	@DisplayName("findInventoryByActiveStatusWithQuantity returns correct quantity with multiple items")
	void findInventoryByActiveStatusWithQuantity_ReturnsCorrectQuantity_WithMultipleItems() {
		// Arrange
		InventoryItem itemA = entityManager.persist(TestInventoryItemFactory.createDefaultItem());
		InventoryItem itemB = entityManager.persist(TestInventoryItemFactory.createItem(
			"MacBook Air", 
			"MBA-123", 
			5
		));

		User user = entityManager.persist(TestUserFactory.createDefaultUser());

		InventoryMovement receiveMovementA = 
			TestInventoryMovementFactory.createInventoryMovement(
				itemA, 
				10, 
				InventoryMovementType.RECEIVE, 
				LocalDate.now(), 
				user
			);

		InventoryMovement saleMovementA =
			TestInventoryMovementFactory.createInventoryMovement(
				itemA, 
				5, 
				InventoryMovementType.SALE, 
				LocalDate.now(), 
				user
			);

		InventoryMovement receiveMovementB = 
			TestInventoryMovementFactory.createInventoryMovement(
				itemB, 
				20, 
				InventoryMovementType.RECEIVE, 
				LocalDate.now(), 
				user
			);

		InventoryMovement saleMovementB =
			TestInventoryMovementFactory.createInventoryMovement(
				itemB, 
				12, 
				InventoryMovementType.SALE, 
				LocalDate.now(), 
				user
			);

		entityManager.persist(receiveMovementA);
		entityManager.persist(saleMovementA);
		entityManager.persist(receiveMovementB);
		entityManager.persist(saleMovementB);
		entityManager.flush();
		entityManager.clear();

		// Act
		Page<InventoryDashboardItem> page = 
			inventoryItemRepository.findInventoryByActiveStatusWithQuantity(
				true,
				PageRequest.of(0, PAGE_SIZE)
			);
		
		List<InventoryDashboardItem> results = page.getContent();
		
		// Assert
		assertThat(page.getTotalElements()).isEqualTo(2);

		assertThat(results)
			.hasSize(2)
			.extracting(InventoryDashboardItem::name, InventoryDashboardItem::currentQuantity)
			.containsExactlyInAnyOrder(
				tuple(itemA.getName(), 5L),
				tuple(itemB.getName(), 8L)
		);
	}

	@Test
	@DisplayName("findInventoryByActiveStatusWithQuantity returns ordered by name ascending")
	void findInventoryByActiveStatusWithQuantity_ReturnsOrderedByNameAscending() {
		// Arrange
		InventoryItem itemA = TestInventoryItemFactory.createItem(
			"A item",
			"SKU-123",
			10
		);

		InventoryItem itemB = TestInventoryItemFactory.createItem(
			"B item",
			"SKU-456",
			10
		);

		InventoryItem itemC = TestInventoryItemFactory.createItem(
			"C item",
			"SKU-789",
			10
		);

		entityManager.persist(itemA);
		entityManager.persist(itemB);
		entityManager.persist(itemC);
		entityManager.flush();
		entityManager.clear();

		// Act
		Page<InventoryDashboardItem> page = 
			inventoryItemRepository.findInventoryByActiveStatusWithQuantity(
				true,
				PageRequest.of(0, PAGE_SIZE)
			);
		
		List<InventoryDashboardItem> results = page.getContent();
		
		// Assert
		assertThat(page.getTotalElements()).isEqualTo(3);

		assertThat(results)
			.hasSize(3)
			.extracting(InventoryDashboardItem::name)
			.containsExactly("A item", "B item", "C item");
	}

	@Test
	@DisplayName("findInventoryByActiveStatusWithQuantity returns only active items when isActive=true")
	void findInventoryByActiveStatusWithQuantity_ReturnsOnlyActiveItems_WhenIsActiveTrue() {
		// Arrange
		InventoryItem itemA = TestInventoryItemFactory.createItem(
			"Active item",
			"SKU-123",
			10
		);

		InventoryItem itemB = TestInventoryItemFactory.createItem(
			"Inactive item",
			"SKU-456",
			10
		);
		itemB.setIsActive(false);

		entityManager.persist(itemA);
		entityManager.persist(itemB);
		entityManager.flush();
		entityManager.clear();

		// Act
		Page<InventoryDashboardItem> page = 
			inventoryItemRepository.findInventoryByActiveStatusWithQuantity(
				true,
				PageRequest.of(0, PAGE_SIZE)
			);
		
		List<InventoryDashboardItem> results = page.getContent();
		
		// Assert
		assertThat(page.getTotalElements()).isEqualTo(1);

		assertThat(results)
			.hasSize(1)
			.extracting(InventoryDashboardItem::name)
			.containsExactly("Active item");
	}

	@Test
	@DisplayName("findInventoryByActiveStatusWithQuantity returns only inactive items with correct quantity when isActive=false")
	void findInventoryByActiveStatusWithQuantity_ReturnsOnlyInactiveItems_WithCorrectQuantity() {
		// Arrange
		InventoryItem itemA = TestInventoryItemFactory.createItem(
			"Active item",
			"SKU-123",
			10
		);

		InventoryItem itemB = TestInventoryItemFactory.createItem(
			"Inactive item",
			"SKU-456",
			10
		);
		itemB.setIsActive(false);

		entityManager.persist(itemA);
		entityManager.persist(itemB);

		User user = entityManager.persist(TestUserFactory.createDefaultUser());

		InventoryMovement receiveMovementA = 
			TestInventoryMovementFactory.createInventoryMovement(
				itemA, 
				50, 
				InventoryMovementType.RECEIVE, 
				LocalDate.now(), 
				user
		);

		InventoryMovement receiveMovementB = 
			TestInventoryMovementFactory.createInventoryMovement(
				itemB, 
				10, 
				InventoryMovementType.RECEIVE, 
				LocalDate.now(), 
				user
		);

		entityManager.persist(receiveMovementA);
		entityManager.persist(receiveMovementB);
		entityManager.flush();
		entityManager.clear();

		// Act
		Page<InventoryDashboardItem> page = 
			inventoryItemRepository.findInventoryByActiveStatusWithQuantity(
				false, 
				PageRequest.of(0, PAGE_SIZE)
			);
		
		List<InventoryDashboardItem> results = page.getContent();
		
		// Assert
		assertThat(page.getTotalElements()).isEqualTo(1);

		assertThat(results)
			.hasSize(1)
			.extracting(InventoryDashboardItem::name, InventoryDashboardItem::currentQuantity)
			.containsExactly(
				tuple(itemB.getName(), 10L)
		);
	}

	@Test
	@DisplayName("countLowStockByActiveStatus returns 0 when no items are below threshold")
	void countLowStockByActiveStatus_Returns0_WhenNoItemsAreBelowThreshold() {
		// Arrange
		InventoryItem item = TestInventoryItemFactory.createItem(
			"Active item",
			"SKU-123",
			10
		);

		entityManager.persist(item);

		User user = entityManager.persist(TestUserFactory.createDefaultUser());

		InventoryMovement receiveMovement = 
			TestInventoryMovementFactory.createInventoryMovement(
				item, 
				11, 
				InventoryMovementType.RECEIVE, 
				LocalDate.now(), 
				user
		);

		entityManager.persist(receiveMovement);
		entityManager.flush();
		entityManager.clear();

		// Act
		Long count = inventoryItemRepository.countLowStockByActiveStatus(true);

		// Assert
		assertThat(count).isZero();
	}

	@Test
	@DisplayName("countLowStockByActiveStatus returns 1 when one item is below threshold")
	void countLowStockByActiveStatus_Returns1_WhenOneItemIsBelowThreshold() {
		// Arrange
		InventoryItem item = TestInventoryItemFactory.createItem(
			"Active item",
			"SKU-123",
			10
		);

		entityManager.persist(item);

		User user = entityManager.persist(TestUserFactory.createDefaultUser());

		InventoryMovement receiveMovement = 
			TestInventoryMovementFactory.createInventoryMovement(
				item, 
				9, 
				InventoryMovementType.RECEIVE, 
				LocalDate.now(), 
				user
		);

		entityManager.persist(receiveMovement);
		entityManager.flush();
		entityManager.clear();

		// Act
		Long count = inventoryItemRepository.countLowStockByActiveStatus(true);

		// Assert
		assertThat(count).isOne();
	}

	@Test
	@DisplayName("countLowStockByActiveStatus respects active status filter")
	void countLowStockByActiveStatus_RespectsActiveFilter() {
		// Arrange
		InventoryItem activeItem = TestInventoryItemFactory.createItem(
			"Active Low Stock",
			"SKU-123",
			10
		);

		InventoryItem inactiveItem = TestInventoryItemFactory.createItem(
			"Inactive Low Stock",
			"SKU-456",
			10
		);
		inactiveItem.setIsActive(false);

		entityManager.persist(activeItem);
		entityManager.persist(inactiveItem);

		User user = entityManager.persist(TestUserFactory.createDefaultUser());

		InventoryMovement movementA =
			TestInventoryMovementFactory.createInventoryMovement(
				activeItem,
				5,
				InventoryMovementType.RECEIVE,
				LocalDate.now(),
				user
			);

		InventoryMovement movementB =
			TestInventoryMovementFactory.createInventoryMovement(
				inactiveItem,
				5,
				InventoryMovementType.RECEIVE,
				LocalDate.now(),
				user
			);

		entityManager.persist(movementA);
		entityManager.persist(movementB);
		entityManager.flush();
		entityManager.clear();

		// Act
		long activeCount = inventoryItemRepository.countLowStockByActiveStatus(true);
		long inactiveCount = inventoryItemRepository.countLowStockByActiveStatus(false);

		// Assert
		assertThat(activeCount).isOne();
		assertThat(inactiveCount).isOne();
	}

	@Test
	@DisplayName("countLowStockByActiveStatus treats items with no movements as zero quantity")
	void countLowStockByActiveStatus_ItemWithNoMovements_IsConsideredLowStock() {
		// Arrange
		InventoryItem item = TestInventoryItemFactory.createItem(
			"No Movement Item",
			"SKU-123",
			10
		);

		entityManager.persist(item);
		entityManager.flush();
		entityManager.clear();

		// No movements

		// Act
		long count = inventoryItemRepository.countLowStockByActiveStatus(true);

		// Assert
		assertThat(count).isOne();
	}

	@Test
	@DisplayName("findCurrentQuantityByItemId returns 0 when no movements")
	void findCurrentQuantityByItemId_Returns0_WhenNoMovements() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		entityManager.persistAndFlush(item);
		entityManager.clear();

		assertThat(inventoryItemRepository.findCurrentQuantityByItemId(item.getId()))
			.isZero();
	}

	@Test
	@DisplayName("findCurrentQuantityByItemId returns 0 for non-existent item id")
	void findCurrentQuantityByItemId_Returns0_ForNonExistentItemId() {
		Long nonExistentItemId = 999L;
		assertThat(inventoryItemRepository.findCurrentQuantityByItemId(nonExistentItemId))
			.isZero();
	}

	@Test
	@DisplayName("findCurrentQuantityByItemId returns correct quantity with mixed movements")
	void findCurrentQuantityByItemId_ReturnsCorrectQuantity_WithMixedMovements() {
		InventoryItem item = entityManager.persist(
			TestInventoryItemFactory.createDefaultItem()
		);

		User user = entityManager.persist(
			TestUserFactory.createDefaultUser()
		);

		InventoryMovement receiveMovement = 
			TestInventoryMovementFactory.createInventoryMovement(
				item,
				10,
				InventoryMovementType.RECEIVE,
				LocalDate.now(),
				user
			);

		InventoryMovement saleMovement =
			TestInventoryMovementFactory.createInventoryMovement(
				item,
				3,
				InventoryMovementType.SALE,
				LocalDate.now(),
				user
			);

		InventoryMovement adjustInMovement = 
			TestInventoryMovementFactory.createInventoryMovement(
				item,
				5,
				InventoryMovementType.ADJUST_IN,
				LocalDate.now(),
				user
			);

		InventoryMovement adjustOutMovement = 
			TestInventoryMovementFactory.createInventoryMovement(
				item,
				2,
				InventoryMovementType.ADJUST_OUT,
				LocalDate.now(),
				user
			);

		entityManager.persist(receiveMovement);
		entityManager.persist(saleMovement);
		entityManager.persist(adjustInMovement);
		entityManager.persist(adjustOutMovement);
		entityManager.flush();
		entityManager.clear();

		assertThat(inventoryItemRepository.findCurrentQuantityByItemId(item.getId()))
			.isEqualTo(10);
	}

	@Test
	@DisplayName("findCurrentQuantityByItemId ignores movements from other items")
	void findCurrentQuantityByItemId_IgnoresMovementsFromOtherItems() {
		InventoryItem itemA = entityManager.persist(
			TestInventoryItemFactory.createDefaultItem()
		);

		InventoryItem itemB = entityManager.persist(
			TestInventoryItemFactory.createItem(
				"Other item",
				"SKU-456",
				10
			)
		);

		User user = entityManager.persist(
			TestUserFactory.createDefaultUser()
		);

		InventoryMovement receiveMovementA = 
			TestInventoryMovementFactory.createInventoryMovement(
				itemA,
				50,
				InventoryMovementType.RECEIVE,
				LocalDate.now(),
				user
			);

		InventoryMovement receiveMovementB = 
			TestInventoryMovementFactory.createInventoryMovement(
				itemB,
				25,
				InventoryMovementType.RECEIVE,
				LocalDate.now(),
				user
			);

		entityManager.persist(receiveMovementA);
		entityManager.persist(receiveMovementB);
		entityManager.flush();
		entityManager.clear();

		assertThat(inventoryItemRepository.findCurrentQuantityByItemId(itemA.getId()))
			.isEqualTo(50L);
	}
}