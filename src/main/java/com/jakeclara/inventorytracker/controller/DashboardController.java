package com.jakeclara.inventorytracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.jakeclara.inventorytracker.dto.InventoryDashboardResponse;
import com.jakeclara.inventorytracker.service.DashboardService;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    @GetMapping
    public String getInventoryDashboard(Model model) {
        InventoryDashboardResponse viewData = dashboardService.getInventoryDashboard();
        
        model.addAttribute("items", viewData.inventoryItems());
        model.addAttribute("lowStockCount", viewData.lowStockCount());
        
        return "/dashboard";
    }
    
}
