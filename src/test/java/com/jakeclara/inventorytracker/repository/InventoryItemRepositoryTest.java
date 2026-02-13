package com.jakeclara.inventorytracker.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.util.TestInventoryItemFactory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;


@DataJpaTest
class InventoryItemRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private InventoryItemRepository inventoryItemRepository;

	@Test
	@DisplayName("existsByName returns true when an item with name exists")
	void existsByName_ReturnsTrue_WhenItemWithNameExists() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		entityManager.persistAndFlush(item);

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

		boolean exists = inventoryItemRepository.existsByNameAndIdNot(item.getName(), item.getId());
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("existsByNameAndIdNot returns false when name does not exist")
	void existsByNameAndIdNot_ReturnsFalse_WhenNameDoesNotExist() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		entityManager.persistAndFlush(item);

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

		boolean exists = inventoryItemRepository.existsByNameAndIdNot(itemA.getName(), itemB.getId());
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("existsBySku returns true when an item with sku exists")
	void existsBySku_ReturnsTrue_WhenItemWithSkuExists() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		entityManager.persistAndFlush(item);

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
	@DisplayName("findActiveInventoryWithQuantity returns 0 when no movements exits")
	void findActiveInventoryWithQuantity_Returns0_WhenNoMovementsExits() {
		InventoryItem item = TestInventoryItemFactory.createDefaultItem();
		entityManager.persistAndFlush(item);

		List<InventoryDashboardItem> results = 
			inventoryItemRepository.findActiveInventoryWithQuantity();
		
		assertThat(results).hasSize(1);
		InventoryDashboardItem dto = results.get(0);

		assertThat(dto.name()).isEqualTo(item.getName());
		assertThat(dto.currentQuantity()).isZero();
	}








}