package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "Username cannot be empty or null")
    private String username;

    @Email
    @NotBlank(message = "Email cannot be empty or null")
    private String email;

    @NotBlank(message = "Password cannot be empty or null")
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters long")
    private String password;
}
