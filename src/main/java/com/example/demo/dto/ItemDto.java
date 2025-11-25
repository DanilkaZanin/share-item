package com.example.demo.dto;

import com.example.demo.dto.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ItemDto(
        @NotNull(groups = ValidationGroups.Create.class)
        @NotBlank(groups = ValidationGroups.Create.class)
        String name,

        @NotNull(groups = ValidationGroups.Create.class)
        @NotBlank(groups = ValidationGroups.Create.class)
        String description,

        @NotNull(groups = ValidationGroups.Create.class)
        BigDecimal pricePerDay
) { }
