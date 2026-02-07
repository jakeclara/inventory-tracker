package com.jakeclara.inventorytracker.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.jakeclara.inventorytracker.dto.InventoryItemForm;
import com.jakeclara.inventorytracker.exception.DuplicateNameException;
import com.jakeclara.inventorytracker.exception.DuplicateSkuException;
import com.jakeclara.inventorytracker.exception.ResourceNotFoundException;
import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.dto.InventoryItemDetailsView;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.repository.InventoryItemRepository;

import jakarta.transaction.Transactional;

@Service
public class InventoryItemService {
    
    private final InventoryItemRepository inventoryItemRepository;

    public InventoryItemService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Transactional
    public Long createInventoryItem(InventoryItemForm form) {

        if (inventoryItemRepository.existsByName(form.getName())) {
            throw new DuplicateNameException("Item with that name already exists");
        }

        if (inventoryItemRepository.existsBySku(form.getSku())) {
            throw new DuplicateSkuException("Item with that SKU already exists");
        }

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

        if (inventoryItemRepository.existsByNameAndIdNot(editForm.getName(), itemID)) {
            throw new DuplicateNameException("Item with that name already exists");
        }

        existingItem.rename(editForm.getName());
        existingItem.updateReorderThreshold(editForm.getReorderThreshold());
        existingItem.setUnit(editForm.getUnit());
    }

    public InventoryItem getInventoryItemById(Long itemId) {
        return inventoryItemRepository.findById(itemId)
        .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found " + itemId));
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

    public List<InventoryDashboardItem> getInactiveItems() {
        return inventoryItemRepository.findInactiveInventoryWithQuantity();
    }
    
}
