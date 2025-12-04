package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequest(
        @NotNull
        Long itemId,

        @NotNull
        Long bookingId,

        @NotBlank
        String text
) {
}
