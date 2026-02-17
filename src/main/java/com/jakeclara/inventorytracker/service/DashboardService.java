package com.jakeclara.inventorytracker.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.jakeclara.inventorytracker.dto.InventoryDashboardItem;
import com.jakeclara.inventorytracker.dto.InventoryDashboardView;
import com.jakeclara.inventorytracker.dto.common.Pagination;
import com.jakeclara.inventorytracker.repository.InventoryItemRepository;

@Service
public class DashboardService {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final InventoryItemRepository inventoryItemRepository;

    public DashboardService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public InventoryDashboardView getInventoryDashboard(int page) {

        int safePage = Math.max(page, 0);
        
        Page<InventoryDashboardItem> inventoryItemsPage = 
            inventoryItemRepository.findInventoryByActiveStatusWithQuantity(
                true,
                PageRequest.of(safePage, DEFAULT_PAGE_SIZE)
            );
        
        List<InventoryDashboardItem> inventoryItems = inventoryItemsPage.getContent();

        long lowStockCount = 
            inventoryItemRepository.countLowStockByActiveStatus(true);

        Pagination pagination = Pagination.from(inventoryItemsPage);

        return new InventoryDashboardView(
            inventoryItems, 
            lowStockCount, 
            pagination
        );
    }
}
