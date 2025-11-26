package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 2, max = 10)
        String name,

        @Size(min = 2, max = 10)
        String password,

        @Size(max = 100)
        String description,

        @Email
        String email
) {
}
