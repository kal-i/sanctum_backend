package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
        @Email(message = "Invalid email format. Please enter a valid email address.")
        @NotBlank(message = "Email cannot be blank.")
        String email,

        @NotBlank(message = "Password cannot be blank.")
        String password
) {}