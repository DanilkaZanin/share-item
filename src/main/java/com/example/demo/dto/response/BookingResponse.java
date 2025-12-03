package com.example.demo.dto.response;

import com.example.demo.entity.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        Long itemId,
        Long bookerId,
        LocalDateTime createdAt,
        Status status,
        LocalDateTime verifiedAt
) { }