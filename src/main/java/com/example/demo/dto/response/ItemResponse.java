package com.example.demo.dto.response;

import com.example.demo.entity.ItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemResponse(
        Long id,

        String name,

        String description,

        BigDecimal pricePerDay,

        ItemStatus status,

        LocalDateTime createdAt
) {
}
