package com.jakeclara.inventorytracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jakeclara.inventorytracker.dto.CreateInventoryMovementRequest;
import com.jakeclara.inventorytracker.model.InventoryMovement;
import com.jakeclara.inventorytracker.service.InventoryMovementService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/movements")
public class InventoryMovementController {
    
    private final InventoryMovementService inventoryMovementService;

    public InventoryMovementController(InventoryMovementService inventoryMovementService) {
        this.inventoryMovementService = inventoryMovementService;
    }
    
    @PostMapping
    public ResponseEntity<Long> createInventoryMovement(@RequestBody CreateInventoryMovementRequest request) {
        Long id = inventoryMovementService.createInventoryMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping("/{id}")
    public InventoryMovement getInventoryMovementById(@PathVariable Long id) {
        return inventoryMovementService.getInventoryMovementById(id);
    }
    
    





}
