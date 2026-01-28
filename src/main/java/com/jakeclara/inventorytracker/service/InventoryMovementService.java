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
    public Long createInventoryMovement(CreateInventoryMovementRequest request) {

        InventoryItem inventoryItem = inventoryItemService.getInventoryItemById(request.getInventoryItemId());
        User createdBy = userService.getUserById(request.getCreatedByUserId());

        InventoryMovement newMovement = new InventoryMovement(
            inventoryItem,
            request.getQuantity(),
            request.getMovementType(),
            request.getMovementDate(),
            createdBy
        );

        if (request.getNote() != null) {
            newMovement.setNote(request.getNote());
        }

        if (request.getReference() != null) {
            newMovement.setReference(request.getReference());
        }

        inventoryMovementRepository.save(newMovement);
        
        return newMovement.getId();
    }

    public InventoryMovement getInventoryMovementById(Long id) {
        return inventoryMovementRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Inventory movement not found with id: " + id));
    }


}
