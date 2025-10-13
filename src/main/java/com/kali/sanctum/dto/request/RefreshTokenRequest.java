package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token is required.")
        String refreshToken
) {}