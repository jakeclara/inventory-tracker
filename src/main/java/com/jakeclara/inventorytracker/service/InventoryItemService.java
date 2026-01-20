package com.jakeclara.inventorytracker.service;

import org.springframework.stereotype.Service;

import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.repository.InventoryItemRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class InventoryItemService {
    
    private final InventoryItemRepository inventoryItemRepository;

    public InventoryItemService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public InventoryItem createInventoryItem(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }

    @Transactional
    public void deactivateInventoryItem(Long id) {
        InventoryItem item = getInventoryItemById(id);
        item.setIsActive(false);
    }

    @Transactional
    public void reactivateInventoryItem(Long id) {
        InventoryItem item = getInventoryItemById(id);
        item.setIsActive(true);
    }

    @Transactional
    public void updateInventoryItem(Long id, InventoryItem updatedItem) {
        InventoryItem existingItem = getInventoryItemById(id);
        existingItem.rename(updatedItem.getName());
        existingItem.updateReorderThreshold(updatedItem.getReorderThreshold());
        existingItem.setUnit(updatedItem.getUnit());
    }

    public InventoryItem getInventoryItemById(Long id) {
        return inventoryItemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Inventory item not found " + id));
    }
    
}
