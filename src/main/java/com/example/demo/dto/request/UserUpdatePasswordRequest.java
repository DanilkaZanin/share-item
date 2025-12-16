package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdatePasswordRequest(
        @NotBlank
        @Size(min = 2, max = 10)
        String oldPassword,

        @NotBlank
        @Size(min = 2, max = 10)
        String newPassword
) {
}
