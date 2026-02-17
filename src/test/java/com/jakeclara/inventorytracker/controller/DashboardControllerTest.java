package com.jakeclara.inventorytracker.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.jakeclara.inventorytracker.dto.InventoryDashboardView;
import com.jakeclara.inventorytracker.dto.common.Pagination;
import com.jakeclara.inventorytracker.service.DashboardService;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @Test
    @WithMockUser
    @DisplayName("GET /dashboard should populate model and return dashboard view")
    void getInventoryDashboard_ShouldReturnDashboardView() throws Exception {

        int page = 0;

        Pagination pagination = new Pagination(
            0,
            1,  
            false,
            false
        );

        InventoryDashboardView mockView = 
            new InventoryDashboardView(List.of(), 2, pagination);
        
        when(dashboardService.getInventoryDashboard(page))
            .thenReturn(mockView);

        mockMvc.perform(get("/dashboard").param("page", String.valueOf(page)))
            .andExpect(status().isOk())
            .andExpect(view().name("dashboard/dashboard"))
            .andExpect(model().attribute("dashboard", mockView));
        
        verify(dashboardService).getInventoryDashboard(page);
    }
}
