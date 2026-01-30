package com.jakeclara.inventorytracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.jakeclara.inventorytracker.dto.CreateInventoryItemRequest;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.service.InventoryItemService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;




@Controller
@RequestMapping("/items")
public class InventoryItemController {
    
    private final InventoryItemService inventoryItemService;

    public InventoryItemController(InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("item", new CreateInventoryItemRequest());
        model.addAttribute("mode", "create");
        return "/items/item-form";
    }

    @PostMapping
    public String createInventoryItem(@ModelAttribute CreateInventoryItemRequest request) {
        Long itemId = inventoryItemService.createInventoryItem(request);
        return "redirect:/items/" + itemId;
    }

    @GetMapping("/{id}")
    public InventoryItem getInventoryItemById(@PathVariable Long id) {
        return inventoryItemService.getInventoryItemById(id);
    }

    @PostMapping("/{itemId}/deactivate")
    public String deactivateInventoryItem(@PathVariable Long itemId) {
        inventoryItemService.deactivateInventoryItem(itemId);
        return "redirect:/items/" + itemId;
    }
    
    @PostMapping("/{itemId}/activate")
    public String reactivateInventoryItem(@PathVariable Long itemId) {
        inventoryItemService.reactivateInventoryItem(itemId);
        return "redirect:/items/" + itemId;
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
