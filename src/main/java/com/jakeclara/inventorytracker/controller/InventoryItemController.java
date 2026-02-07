package com.jakeclara.inventorytracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jakeclara.inventorytracker.dto.InventoryItemForm;
import com.jakeclara.inventorytracker.dto.InventoryMovementForm;
import com.jakeclara.inventorytracker.exception.DuplicateNameException;
import com.jakeclara.inventorytracker.exception.DuplicateSkuException;
import com.jakeclara.inventorytracker.exception.InsufficientStockException;
import com.jakeclara.inventorytracker.model.InventoryItem;
import com.jakeclara.inventorytracker.model.InventoryMovementType;
import com.jakeclara.inventorytracker.service.InventoryItemService;
import com.jakeclara.inventorytracker.service.InventoryMovementService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;



@Controller
@RequestMapping("/items")
public class InventoryItemController {
    private static final String REDIRECT_ITEM_DETAILS = "redirect:/items/{itemId}";
    private static final String ITEM_DETAILS_VIEW = "items/item-details";
    private static final String ITEM_FORM_VIEW = "items/item-form";
    
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
        return ITEM_FORM_VIEW;
    }

    @PostMapping
    public String createInventoryItem(
        @Valid @ModelAttribute("item") InventoryItemForm form,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        model.addAttribute("mode", "create");

        if (bindingResult.hasErrors()) {
            return ITEM_FORM_VIEW;
        }

        try {
            Long itemId = inventoryItemService.createInventoryItem(form);
            redirectAttributes.addAttribute("itemId", itemId);
            return REDIRECT_ITEM_DETAILS;
        } catch (DuplicateNameException e) {
            bindingResult.rejectValue("name", "duplicate", e.getMessage());
        } catch (DuplicateSkuException e) {
            bindingResult.rejectValue("sku", "duplicate", e.getMessage());
        }
        
        return ITEM_FORM_VIEW;
    }

    @GetMapping("/{itemId}")
    public String getInventoryItemDetails(@PathVariable Long itemId, Model model) {
        model.addAttribute("movementForm", InventoryMovementForm.empty());
        prepareDetailsData(itemId, model);
        return ITEM_DETAILS_VIEW;
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
        model.addAttribute("itemDetails", inventoryItemService.getItemDetails(itemId));
        model.addAttribute("item", InventoryItemForm.from(item));
        model.addAttribute("mode", "edit");
        return ITEM_FORM_VIEW;
    }

    @PostMapping("/{itemId}/edit")
    public String editInventoryItem(
        @PathVariable Long itemId,
        @Valid @ModelAttribute("item") InventoryItemForm editForm,
        BindingResult bindingResult,
        Model model
    ) {
        model.addAttribute("mode", "edit");

        if (bindingResult.hasErrors()) {
            model.addAttribute("itemDetails", inventoryItemService.getItemDetails(itemId));
            return ITEM_FORM_VIEW;
        }

        try {
            inventoryItemService.updateInventoryItem(itemId, editForm);
            return REDIRECT_ITEM_DETAILS;
        } catch (DuplicateNameException e) {
            bindingResult.rejectValue("name", "duplicate", e.getMessage());
        }

        model.addAttribute("itemDetails", inventoryItemService.getItemDetails(itemId));
        return ITEM_FORM_VIEW;
    }

    @PostMapping("/{itemId}/movements")
    public String addInventoryMovement(
        @PathVariable Long itemId, 
        @Valid @ModelAttribute("movementForm") InventoryMovementForm form,
        BindingResult bindingResult,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            prepareDetailsData(itemId, model);
            return ITEM_DETAILS_VIEW;
        }
        try {
            inventoryMovementService.addInventoryMovement(itemId, form);
            return REDIRECT_ITEM_DETAILS;
        } catch (InsufficientStockException e) {
            bindingResult.rejectValue("quantity", "insufficient", e.getMessage());
            prepareDetailsData(itemId, model);
            return ITEM_DETAILS_VIEW;
        }
    }

    @GetMapping("/inactive")
    public String getInactiveItems(Model model) {
        model.addAttribute("inactiveItems", inventoryItemService.getInactiveItems());
        return "items/inactive-items";
    }

    private void prepareDetailsData(Long itemId, Model model) {
        model.addAttribute("itemDetails", inventoryItemService.getItemDetails(itemId));
        model.addAttribute("movementHistory", inventoryMovementService.getMovementsForItem(itemId));
        model.addAttribute("movementTypes", InventoryMovementType.values());
        model.addAttribute("isAdmin", true); // Placeholder for actual admin check
    }
    
}
