package com.jakeclara.inventorytracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.model.InventoryMovement;
import com.jakeclara.inventorytracker.model.InventoryMovementType;
import com.jakeclara.inventorytracker.model.User;
import com.jakeclara.inventorytracker.util.TestInventoryItemFactory;
import com.jakeclara.inventorytracker.util.TestInventoryMovementFactory;
import com.jakeclara.inventorytracker.util.TestUserFactory;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InventoryMovementRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Test
    @DisplayName("findByItemIdOrderByMovementDateDesc returns empty list when no movements exist for given item ID")
    void findByItemIdOrderByMovementDateDesc_ReturnsEmptyList_WhenNoMovementsExist() {
        Long nonexistentItemId = 999L;
        List<InventoryMovement> movements = 
            inventoryMovementRepository.findByItemIdOrderByMovementDateDesc(nonexistentItemId);
        assertThat(movements).isEmpty();
    }

    @Test
    @DisplayName("findByItemIdOrderByMovementDateDesc returns movements ordered by date descending")
    void findByItemIdOrderByMovementDateDesc_ReturnsMovementsOrderedByDateDesc() {
        
        // Arrange
        User user = entityManager.persist(TestUserFactory.createDefaultUser());
        InventoryItem itemA = entityManager.persist(TestInventoryItemFactory.createDefaultItem());
        InventoryItem itemB = entityManager.persist(
            TestInventoryItemFactory.createItem("Item B", "Description B", 25)
        );
        InventoryMovement olderMovement = 
            TestInventoryMovementFactory.createInventoryMovement(
                itemA,
                10, 
                InventoryMovementType.RECEIVE, 
                LocalDate.now().minusDays(10), 
                user
            );
        InventoryMovement newerMovement = 
            TestInventoryMovementFactory.createInventoryMovement(
                itemA, 
                5, 
                InventoryMovementType.RECEIVE, 
                LocalDate.now().minusDays(5), 
                user
            );
        InventoryMovement otherItemMovement = 
            TestInventoryMovementFactory.createInventoryMovement(
                itemB, 
                5, 
                InventoryMovementType.RECEIVE, 
                LocalDate.now().minusDays(1), 
                user
            );
        entityManager.persist(olderMovement);
        entityManager.persist(newerMovement);
        entityManager.persist(otherItemMovement);
        entityManager.flush();

        // Act
        List<InventoryMovement> movements = 
            inventoryMovementRepository.findByItemIdOrderByMovementDateDesc(itemA.getId());

        // Assert
        assertThat(movements).hasSize(2);
        
        assertThat(movements.get(0).getMovementDate())
            .isAfter(movements.get(1).getMovementDate());

        assertThat(movements)
            .extracting(movement -> movement.getItem().getId())
            .containsOnly(itemA.getId());
    }
}
