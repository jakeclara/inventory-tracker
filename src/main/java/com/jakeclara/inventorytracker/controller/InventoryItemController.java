package com.jakeclara.inventorytracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jakeclara.inventorytracker.dto.InventoryItemForm;
import com.jakeclara.inventorytracker.dto.InventoryMovementForm;
import com.jakeclara.inventorytracker.dto.InventoryMovementView;
import com.jakeclara.inventorytracker.dto.InventoryItemDetailsView;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.model.InventoryMovementType;
import com.jakeclara.inventorytracker.service.InventoryItemService;
import com.jakeclara.inventorytracker.service.InventoryMovementService;

import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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
    private final InventoryMovementService inventoryMovementService;

    public InventoryItemController(
        InventoryItemService inventoryItemService,
        InventoryMovementService inventoryMovementService
    ) {
        this.inventoryItemService = inventoryItemService;
        this.inventoryMovementService = inventoryMovementService;
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
        InventoryItemDetailsView details = inventoryItemService.getItemDetails(itemId);
        List<InventoryMovementView> movements = inventoryMovementService.getMovementsForItem(itemId);
        model.addAttribute("itemDetails", details);
        model.addAttribute("movementHistory", movements);
        model.addAttribute("movementForm", InventoryMovementForm.empty());
        model.addAttribute("movementTypes", InventoryMovementType.values());
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
        InventoryItemDetailsView details = inventoryItemService.getItemDetails(itemId);
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

    @GetMapping("/inactive")
    public String getInactiveItems(Model model) {
        List<InventoryItemDetailsView> inactiveItems = inventoryItemService.getInactiveItemDetails();
        model.addAttribute("inactiveItems", inactiveItems);
        return "items/inactive-items";
    }
    
}
