package com.jakeclara.inventorytracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jakeclara.inventorytracker.dto.InventoryItemForm;
import com.jakeclara.inventorytracker.dto.InventoryItemDetailsView;
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

    @Transactional
    public Long createInventoryItem(InventoryItemForm form) {
        InventoryItem newItem = new InventoryItem(
            form.getName(),
            form.getSku(),
            form.getReorderThreshold()
        );
        newItem.setUnit(form.getUnit());

        return inventoryItemRepository.save(newItem).getId();
    }

    @Transactional
    public void deactivateInventoryItem(Long itemID) {
        InventoryItem item = getInventoryItemById(itemID);
        item.setIsActive(false);
    }

    @Transactional
    public void activateInventoryItem(Long itemID) {
        InventoryItem item = getInventoryItemById(itemID);
        item.setIsActive(true);
    }

    @Transactional
    public void updateInventoryItem(Long itemID, InventoryItemForm editForm) {
        InventoryItem existingItem = getInventoryItemById(itemID);
        existingItem.rename(editForm.getName());
        existingItem.updateReorderThreshold(editForm.getReorderThreshold());
        existingItem.setUnit(editForm.getUnit());
    }

    public InventoryItem getInventoryItemById(Long itemId) {
        return inventoryItemRepository.findById(itemId)
        .orElseThrow(() -> new EntityNotFoundException("Inventory item not found " + itemId));
    }

    public Long getCurrentQuantity(Long itemId) {
        return inventoryItemRepository.findCurrentQuantityByItemId(itemId);
    }
    
    public InventoryItemDetailsView getItemDetails(Long itemId) {
        InventoryItem item = getInventoryItemById(itemId);
        return InventoryItemDetailsView.from(
            item, 
            getCurrentQuantity(itemId)
        );
    }

    public List<InventoryItemDetailsView> getInactiveItemDetails() {
        return inventoryItemRepository.findByIsActiveFalseOrderByNameAsc()
        .stream()
        .map(item -> InventoryItemDetailsView.from(
            item, 
            getCurrentQuantity(item.getId())
        ))
        .toList();
    }
    
}
