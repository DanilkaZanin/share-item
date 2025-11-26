package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingResponse(
        LocalDate startDate,
        LocalDate endDate,
        Long itemId,
        Long bookerId,
        LocalDateTime createdAt,
        LocalDateTime verifiedAt
) { }