package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank
        @Size(min = 2, max = 10)
        String name,

        @NotBlank
        @Size(min = 2, max = 10)
        String password,

        @NotBlank
        @Size(max = 100)
        String description,

        @NotBlank
        @Email
        String email
) {
}
