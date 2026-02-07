package com.jakeclara.inventorytracker.service;

import com.jakeclara.inventorytracker.model.User;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jakeclara.inventorytracker.dto.InventoryMovementForm;
import com.jakeclara.inventorytracker.dto.InventoryMovementView;
import com.jakeclara.inventorytracker.exception.InactiveItemException;
import com.jakeclara.inventorytracker.exception.InsufficientStockException;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.model.InventoryMovement;
import com.jakeclara.inventorytracker.repository.InventoryMovementRepository;
import com.jakeclara.inventorytracker.security.AuthenticatedUserProvider;

import jakarta.transaction.Transactional;

@Service
public class InventoryMovementService {
    
    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryItemService inventoryItemService;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    public InventoryMovementService(
        InventoryMovementRepository inventoryMovementRepository,
        InventoryItemService inventoryItemService,
        AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.inventoryItemService = inventoryItemService;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Transactional
    public void addInventoryMovement(Long itemId, InventoryMovementForm form) {

        InventoryItem inventoryItem = inventoryItemService.getInventoryItemById(itemId);
        
        if (!inventoryItem.isActive()) {
            throw new InactiveItemException("Cannot add movement: Item " + inventoryItem.getName() + " is inactive.");
        }

        ensureSufficientStock(itemId, form.movementType().apply(form.quantity()));

        User createdBy = authenticatedUserProvider.getAuthenticatedUser();

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

    public void ensureSufficientStock(Long itemId, int quantityDelta) {
        Long currentQuantity = inventoryItemService.getCurrentQuantity(itemId);
        if (currentQuantity + quantityDelta < 0) {
            throw new InsufficientStockException(currentQuantity, quantityDelta);
        }
    }

}
