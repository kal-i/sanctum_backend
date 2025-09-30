package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record VerifyOtpRequest(
        @Email
        @NotBlank
        String email,
        @NotBlank
        String otpCode
) {}