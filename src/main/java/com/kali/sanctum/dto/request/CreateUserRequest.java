package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateUserRequest(
        @NotBlank(message = "Username cannot be blank.")
        String username,

        @Email(message = "Invalid email format. Please enter a valid email.")
        @NotBlank(message = "Email cannot be blank.")
        String email,
        
        @NotBlank(message = "Password cannot be blank.")
        @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long.")
        String password
) {}
