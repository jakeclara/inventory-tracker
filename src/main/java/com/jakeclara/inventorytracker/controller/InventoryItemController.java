package com.jakeclara.inventorytracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jakeclara.inventorytracker.dto.InventoryItemForm;
import com.jakeclara.inventorytracker.dto.InventoryItemDetails;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.service.InventoryItemService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
@RequestMapping("/items")
public class InventoryItemController {
    private static final String REDIRECT_ITEM_DETAILS = "redirect:/items/{itemId}";
    
    private final InventoryItemService inventoryItemService;

    public InventoryItemController(InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("item", new InventoryItemForm());
        model.addAttribute("mode", "create");
        return "items/item-form";
    }

    @PostMapping
    public String createInventoryItem(
        @ModelAttribute("item") InventoryItemForm form,
        RedirectAttributes redirectAttributes
    ) {
        Long itemId = inventoryItemService.createInventoryItem(form);
        redirectAttributes.addAttribute("itemId", itemId);
        return REDIRECT_ITEM_DETAILS;
    }

    @GetMapping("/{itemId}")
    public String getInventoryItemDetails(@PathVariable Long itemId, Model model) {
        InventoryItemDetails details = inventoryItemService.getItemDetails(itemId);
        model.addAttribute("itemDetails", details);
        model.addAttribute("isAdmin", true); // Placeholder for actual admin check
        return "items/item-details";
    }

    @PostMapping("/{itemId}/deactivate")
    public String deactivateInventoryItem(@PathVariable Long itemId) {
        inventoryItemService.deactivateInventoryItem(itemId);
        return REDIRECT_ITEM_DETAILS;
    }
    
    @PostMapping("/{itemId}/activate")
    public String activateInventoryItem(@PathVariable Long itemId) {
        inventoryItemService.activateInventoryItem(itemId);
        return REDIRECT_ITEM_DETAILS;
    }

    @GetMapping("/{itemId}/edit")
    public String showEditForm(@PathVariable Long itemId, Model model) {
        InventoryItem item = inventoryItemService.getInventoryItemById(itemId);
        InventoryItemDetails details = inventoryItemService.getItemDetails(itemId);
        InventoryItemForm editForm = InventoryItemForm.from(item);
        model.addAttribute("itemDetails", details);
        model.addAttribute("item", editForm);
        model.addAttribute("mode", "edit");
        return "items/item-form";
    }

    @PostMapping("/{itemId}/edit")
    public String editInventoryItem(
        @PathVariable Long itemId,
        @ModelAttribute InventoryItemForm editForm
    ) {
        inventoryItemService.updateInventoryItem(itemId, editForm);
        return REDIRECT_ITEM_DETAILS;
    }
    
}
