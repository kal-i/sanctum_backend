package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateUserRequest(
        @NotBlank(message = "Username cannot be blank.")
        String username
) {}