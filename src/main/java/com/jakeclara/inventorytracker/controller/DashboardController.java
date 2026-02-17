package com.jakeclara.inventorytracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jakeclara.inventorytracker.dto.InventoryDashboardView;
import com.jakeclara.inventorytracker.service.DashboardService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    @GetMapping
    public String getInventoryDashboard(
        @RequestParam (defaultValue = "0") int page,
        Model model
    ) {
        InventoryDashboardView dashboard = 
            dashboardService.getInventoryDashboard(page);
        
        model.addAttribute("dashboard", dashboard);
        
        return "dashboard/dashboard";
    }
}
