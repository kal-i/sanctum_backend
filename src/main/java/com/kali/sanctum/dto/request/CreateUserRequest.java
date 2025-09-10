package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateUserRequest(
        @NotBlank(message = "User cannot be empty or null")
        String username,
        @Email
        @NotBlank(message = "Email cannot be empty or null")
        String email,
        @NotBlank(message = "Password cannot be empty or null")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
        String password
) {}
