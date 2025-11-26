package com.example.demo.dto.response;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,

        String email,

        String password,

        String name,

        String description,

        LocalDateTime createdAt

) {
}
