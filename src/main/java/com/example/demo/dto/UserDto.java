package com.example.demo.dto;

import com.example.demo.dto.validation.ValidationGroups;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto(
        @NotBlank(groups = ValidationGroups.Create.class)
        @Size(min = 2, max = 10)
        String name,

        @NotBlank(groups = ValidationGroups.Create.class)
        @Size(min = 2, max = 10)
        String password,

        @Size(max = 100)
        String description,

        @NotBlank(groups = ValidationGroups.Create.class)
        @Email
        String email
) { }
