package com.jakeclara.inventorytracker.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.jakeclara.inventorytracker.config.SecurityConfig;
import com.jakeclara.inventorytracker.dto.InventoryDashboardView;
import com.jakeclara.inventorytracker.dto.InventoryItemDetailsView;
import com.jakeclara.inventorytracker.dto.InventoryMovementView;
import com.jakeclara.inventorytracker.dto.common.Pagination;
import com.jakeclara.inventorytracker.exception.ResourceNotFoundException;
import com.jakeclara.inventorytracker.service.InventoryItemService;
import com.jakeclara.inventorytracker.service.InventoryMovementService;

@WebMvcTest(InventoryItemController.class)
@Import(SecurityConfig.class)
class InventoryItemControllerTest {

    private static final Long ITEM_ID = 1L;
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private InventoryItemService inventoryItemService;

    @MockitoBean
    private InventoryMovementService inventoryMovementService;

    // GET /items/new  (ADMIN only)
    @Nested
    @DisplayName("GET /items/new - View add item form")
    class GetNewItemForm {
        @Test
        @DisplayName("should redirect to login when unauthenticated")
        void shouldRedirectToLogin_WhenUnauthenticated() throws Exception {
            mockMvc.perform(get("/items/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

            verifyNoInteractions(inventoryItemService);
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should return 403 for non-admin user")
        void shouldReturn403_WhenUserRole() throws Exception {
            mockMvc.perform(get("/items/new"))
                .andExpect(status().isForbidden());

            verifyNoInteractions(inventoryItemService);
        }
        
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("should return item form view with create mode for ADMIN")
        void shouldReturnItemFormView_WhenAdmin() throws Exception {
            mockMvc.perform(get("/items/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/item-form"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("mode", "create"));
        }
    }

    // POST /items  (ADMIN only)
    @Nested
    @DisplayName("POST /items - Create inventory item")
    class CreateInventoryItem {
        @Test
        @DisplayName("should redirect to login when unauthenticated")
        void shouldRedirectToLogin_WhenUnauthenticated() throws Exception {
            mockMvc.perform(post("/items")
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

            verifyNoInteractions(inventoryItemService);
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should return 403 for non-admin user")
        void shouldReturn403_WhenUserRole() throws Exception {
            mockMvc.perform(post("/items")
                    .with(csrf()))
                .andExpect(status().isForbidden());

            verifyNoInteractions(inventoryItemService);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("should return form view when validation fails")
        void shouldReturnFormView_WhenValidationFails() throws Exception {
            mockMvc.perform(post("/items")
                    .with(csrf())
                    .param("name", "")
                    .param("sku", "")
                    .param("reorderThreshold", "")
            )
                .andExpect(status().isOk())
                .andExpect(view().name("items/item-form"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attributeHasFieldErrors(
                    "item", 
                    "name", 
                    "sku",
                    "reorderThreshold"))
                .andExpect(model().attribute("mode", "create"));
            
            verifyNoInteractions(inventoryItemService);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("should return form view when reorderThreshold is negative")
        void shouldReturnFormView_WhenReorderThresholdIsNegative() throws Exception {
            mockMvc.perform(post("/items")
                    .with(csrf())
                    .param("name", "Item A")
                    .param("sku", "SKU-123")
                    .param("reorderThreshold", "-1")
                    .param("unit", "pcs")
            )
                .andExpect(status().isOk())
                .andExpect(view().name("items/item-form"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attributeHasFieldErrors(
                    "item", 
                    "reorderThreshold"))
                .andExpect(model().attribute("mode", "create"));
            
            verifyNoInteractions(inventoryItemService);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("should return form view when unit is invalid")
        void shouldReturnFormView_WhenUnitIsInvalid() throws Exception {
            mockMvc.perform(post("/items")
                    .with(csrf())
                    .param("name", "Item A")
                    .param("sku", "SKU-123")
                    .param("reorderThreshold", "5")
                    .param("unit", "ThisUnitIsMoreThan20CharactersLong")
            )
                .andExpect(status().isOk())
                .andExpect(view().name("items/item-form"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attributeHasFieldErrors(
                    "item", 
                    "unit"))
                .andExpect(model().attribute("mode", "create"));
            
            verifyNoInteractions(inventoryItemService);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("should create item and redirect when input is valid")
        void shouldRedirect_WhenValid() throws Exception {

            when(inventoryItemService.createInventoryItem(any()))
                .thenReturn(ITEM_ID);

            mockMvc.perform(post("/items")
                    .with(csrf())
                    .param("name", "Item A")
                    .param("sku", "SKU-123")
                    .param("reorderThreshold", "5")
                    .param("unit", "pcs")
            )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/" + ITEM_ID));

            verify(inventoryItemService).createInventoryItem(any());
        }
    }

    // POST /items/{id}/deactivate  (ADMIN only)
    @Nested
    class DeactivateItem {

       @Test
        @DisplayName("should redirect to login when unauthenticated")
        void shouldRedirectToLogin_WhenUnauthenticated() throws Exception {
            mockMvc.perform(post("/items/{id}/deactivate", ITEM_ID)
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

            verifyNoInteractions(inventoryItemService);
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should return 403 for non-admin user")
        void deactivateInventoryItem_ShouldReturn403_WhenUserRole() throws Exception {
            mockMvc.perform(post("/items/{id}/deactivate", ITEM_ID)
                    .with(csrf()))
                .andExpect(status().isForbidden());

            verifyNoInteractions(inventoryItemService);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("should deactivate and redirect to item details view for ADMIN")
        void deactivateInventoryItem_ShouldDeactivateAndRedirect_WhenAdmin() throws Exception {

            mockMvc.perform(post("/items/{id}/deactivate", ITEM_ID)
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/" + ITEM_ID));

            verify(inventoryItemService).deactivateInventoryItem(ITEM_ID);
        }
    }

    // POST /items/{id}/activate  (ADMIN only)
    @Nested
    @DisplayName("POST /items/{id}/activate")
    class ActivateItem {

        @Test
        @DisplayName("should redirect to login when unauthenticated")
        void shouldRedirectToLogin_WhenUnauthenticated() throws Exception {
            mockMvc.perform(post("/items/{id}/activate", ITEM_ID)
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

            verifyNoInteractions(inventoryItemService);
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("activateInventoryItem should return 403 for non-admin user")
        void activateInventoryItem_ShouldReturn403_WhenUserRole() throws Exception {
            mockMvc.perform(post("/items/{id}/activate", ITEM_ID)
                    .with(csrf()))
                .andExpect(status().isForbidden());

            verifyNoInteractions(inventoryItemService);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("activateInventoryItem should activate and redirect to item details view")
        void activateInventoryItem_ShouldActivateAndRedirect() throws Exception {

            mockMvc.perform(post("/items/{id}/activate", ITEM_ID)
                    .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/" + ITEM_ID));

            verify(inventoryItemService).activateInventoryItem(ITEM_ID);
        }
    }

    // GET /items/{id} - Item details
    @Nested
    @DisplayName("GET /items/{id} - Item details")
    class GetInventoryItemDetails {

        @Test
        @DisplayName("should redirect to login when unauthenticated")
        void shouldRedirectToLogin_WhenUnauthenticated() throws Exception {
            mockMvc.perform(get("/items/{id}", ITEM_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

            verifyNoInteractions(inventoryItemService);
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should return item details view with populated model")
        void shouldReturnDetailsView_WithPopulatedModel() throws Exception {

            // Mock item details
            when(inventoryItemService.getItemDetails(ITEM_ID))
                    .thenReturn(mock(InventoryItemDetailsView.class));

            // Mock empty movement page
            Page<InventoryMovementView> movementPage =
                    new PageImpl<>(List.of());

            when(inventoryMovementService.getMovementsForItem(ITEM_ID, 0))
                    .thenReturn(movementPage);

            mockMvc.perform(get("/items/{itemId}", ITEM_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("items/item-details"))
                .andExpect(model().attributeExists("movementForm"))
                .andExpect(model().attributeExists("itemDetails"))
                .andExpect(model().attributeExists("movementHistory"))
                .andExpect(model().attributeExists("movementPagination"))
                .andExpect(model().attributeExists("movementTypes"));

            verify(inventoryItemService).getItemDetails(ITEM_ID);
            verify(inventoryMovementService).getMovementsForItem(ITEM_ID, 0);
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should redirect to dashboard when item not found")
        void shouldRedirectToDashboard_WhenItemNotFound() throws Exception {

            when(inventoryItemService.getItemDetails(ITEM_ID))
                .thenThrow(new ResourceNotFoundException("Not found"));

            mockMvc.perform(get("/items/{id}", ITEM_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attributeExists("errorMessage"));

            verify(inventoryItemService).getItemDetails(ITEM_ID);
            verifyNoInteractions(inventoryMovementService);
        }
    }

    // GET /items/inactive 
    @Nested
    @DisplayName("GET /items/inactive - Inactive items")
    class GetInactiveItems {

        @Test
        @DisplayName("should redirect to login when unauthenticated")
        void shouldRedirectToLogin_WhenUnauthenticated() throws Exception {
            mockMvc.perform(get("/items/inactive"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        
            verifyNoInteractions(inventoryItemService);
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("should return 403 for non-admin user")
        void shouldReturn403_WhenUserRole() throws Exception {
            mockMvc.perform(get("/items/inactive"))
                .andExpect(status().isForbidden());

            verifyNoInteractions(inventoryItemService);
        }
    
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("getInactiveItems should return inactive items view for ADMIN")
        void shouldReturnInactiveItemsView_WhenAdmin() throws Exception {
            int page = 0;

            Pagination pagination = new Pagination(
                0,
                1,  
                false,
                false
            );

            InventoryDashboardView mockView = 
                new InventoryDashboardView(List.of(), 2, pagination);

            when(inventoryItemService.getInactiveItems(page))
                .thenReturn(mockView);

            mockMvc.perform(get("/items/inactive"))
                .andExpect(status().isOk())
                .andExpect(view().name("items/inactive-items"))
                .andExpect(model().attribute("inactiveItems", mockView));
            
            verify(inventoryItemService).getInactiveItems(page);
        }
    }

}
