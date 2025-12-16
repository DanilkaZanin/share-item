package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserSignInRequest(
        @NotBlank
        @Size(min = 2, max = 10)
        String password,

        @NotBlank
        @Email
        String email
) { }
