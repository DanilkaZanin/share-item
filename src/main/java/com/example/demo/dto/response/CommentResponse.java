package com.example.demo.dto.response;

public record CommentResponse(
        Long id,
        Long userId,
        Long itemId,
        Long bookingId,
        String text
) {
}
