package com.jakeclara.inventorytracker.dto.common;

import org.springframework.data.domain.Page;

public record Pagination(
    int currentPage,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {
    public static Pagination from(Page<?> page) {
        return new Pagination(
            page.getNumber(), 
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious()
        );
    }
}
