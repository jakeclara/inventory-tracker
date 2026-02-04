package com.jakeclara.inventorytracker.service;

import com.jakeclara.inventorytracker.model.User;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jakeclara.inventorytracker.dto.InventoryMovementForm;
import com.jakeclara.inventorytracker.dto.InventoryMovementView;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.model.InventoryMovement;
import com.jakeclara.inventorytracker.repository.InventoryMovementRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class InventoryMovementService {
    
    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryItemService inventoryItemService;
    private final UserService userService;

    public InventoryMovementService(
        InventoryMovementRepository inventoryMovementRepository,
        InventoryItemService inventoryItemService,
        UserService userService
    ) {
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.inventoryItemService = inventoryItemService;
        this.userService = userService;
    }

    @Transactional
    public void addInventoryMovement(Long itemId, InventoryMovementForm form) {

        InventoryItem inventoryItem = inventoryItemService.getInventoryItemById(itemId);
        
        if (!inventoryItem.isActive()) {
            throw new IllegalStateException("Cannot add movement to inactive inventory item.");
        }

        Long currentQuantity = inventoryItemService.getCurrentQuantity(itemId);
        int quantityDelta = form.movementType().apply(form.quantity());
        
        if (currentQuantity + quantityDelta < 0) {
            throw new IllegalArgumentException("Insufficient inventory for this movement.");
        }

        // TEMPORARY hardcoded user until auth is implemented
        User createdBy = userService.getUserById(1L);

        InventoryMovement newMovement = new InventoryMovement(
            inventoryItem,
            form.quantity(),
            form.movementType(),
            form.movementDate(),
            createdBy
        );

        if (form.note() != null) {
            newMovement.setNote(form.note());
        }

        if (form.reference() != null) {
            newMovement.setReference(form.reference());
        }

        inventoryMovementRepository.save(newMovement);
    }

    public List<InventoryMovementView> getMovementsForItem(Long itemId) {
        inventoryItemService.getInventoryItemById(itemId);
        return inventoryMovementRepository.findByItemIdOrderByMovementDateDesc(itemId)
        .stream()
        .map(InventoryMovementView::from)
        .toList();
    }

    public InventoryMovement getInventoryMovementById(Long id) {
        return inventoryMovementRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Inventory movement not found with id: " + id));
    }


}
