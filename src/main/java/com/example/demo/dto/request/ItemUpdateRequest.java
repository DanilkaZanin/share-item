package com.example.demo.dto.request;

import com.example.demo.entity.ItemStatus;

import java.math.BigDecimal;

public record ItemUpdateRequest(
        String name,
        String description,
        BigDecimal pricePerDay,
        ItemStatus status
) {
}
