package com.jakeclara.inventorytracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import com.jakeclara.inventorytracker.dto.InventoryMovementForm;
import com.jakeclara.inventorytracker.model.InventoryMovement;
import com.jakeclara.inventorytracker.service.InventoryMovementService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;



@Controller
@RequestMapping("/items/{itemId}/movements")
public class InventoryMovementController {
    
    private final InventoryMovementService inventoryMovementService;

    public InventoryMovementController(InventoryMovementService inventoryMovementService) {
        this.inventoryMovementService = inventoryMovementService;
    }
    
    @PostMapping
    public String addInventoryMovement(
        @PathVariable Long itemId, 
        @ModelAttribute InventoryMovementForm form
    ) {
        inventoryMovementService.addInventoryMovement(itemId, form);
        return "redirect:/items/{itemId}";
    }

    @GetMapping("/{id}")
    public InventoryMovement getInventoryMovementById(@PathVariable Long id) {
        return inventoryMovementService.getInventoryMovementById(id);
    }
    
    





}
