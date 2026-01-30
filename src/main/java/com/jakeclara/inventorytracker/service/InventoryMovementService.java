package com.jakeclara.inventorytracker.service;

import com.jakeclara.inventorytracker.model.User;
import org.springframework.stereotype.Service;

import com.jakeclara.inventorytracker.dto.CreateInventoryMovementRequest;
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
    public Long createInventoryMovement(Long itemId, CreateInventoryMovementRequest request) {

        InventoryItem inventoryItem = inventoryItemService.getInventoryItemById(itemId);

        // TEMPORARY hardcoded user until auth is implemented
        User createdBy = userService.getUserById(1L);

        InventoryMovement newMovement = new InventoryMovement(
            inventoryItem,
            request.quantity(),
            request.movementType(),
            request.movementDate(),
            createdBy
        );

        if (request.note() != null) {
            newMovement.setNote(request.note());
        }

        if (request.reference() != null) {
            newMovement.setReference(request.reference());
        }

        inventoryMovementRepository.save(newMovement);
        
        return newMovement.getId();
    }

    public InventoryMovement getInventoryMovementById(Long id) {
        return inventoryMovementRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Inventory movement not found with id: " + id));
    }


}
