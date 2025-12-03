package com.example.demo.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingRequest(
        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        @NotNull
        Long itemId
) { }
