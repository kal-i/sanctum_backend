package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record VerifyOtpRequest(
        @Email(message = "Please enter a valid email.")
        @NotBlank(message = "Email is required.")
        String email,

        @NotBlank(message = "Otp code is required.")
        String otpCode
) {}