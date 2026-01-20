package com.jakeclara.inventorytracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.service.InventoryItemService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/items")
public class InventoryItemController {
    
    private final InventoryItemService inventoryItemService;

    public InventoryItemController(InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryItem createInventoryItem(@RequestBody InventoryItem item) {
        return inventoryItemService.createInventoryItem(item);
    }

    @GetMapping("/{id}")
    public InventoryItem getInventoryItemById(@PathVariable Long id) {
        return inventoryItemService.getInventoryItemById(id);
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateInventoryItem(@PathVariable Long id) {
        inventoryItemService.deactivateInventoryItem(id);
    }

    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reactivateInventoryItem(@PathVariable Long id) {
        inventoryItemService.reactivateInventoryItem(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateInventoryItem(
        @PathVariable Long id, 
        @RequestBody InventoryItem updatedItem
    ) {
        inventoryItemService.updateInventoryItem(id, updatedItem);
    }
    
}
