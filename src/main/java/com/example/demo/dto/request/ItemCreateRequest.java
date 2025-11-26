package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ItemCreateRequest(
        @NotNull
        @NotBlank
        String name,

        @NotNull
        @NotBlank
        String description,

        @NotNull
        BigDecimal pricePerDay
) {
}
