package com.example.demo.dto.request;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 2, max = 10)
        String name,

        @Size(max = 100)
        String description
) {
}
