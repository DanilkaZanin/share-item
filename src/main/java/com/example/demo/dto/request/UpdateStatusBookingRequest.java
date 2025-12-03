package com.example.demo.dto.request;

import com.example.demo.entity.Status;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusBookingRequest(
        @NotNull
        Status status
) { }
