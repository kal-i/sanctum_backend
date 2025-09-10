package com.kali.sanctum.dto.request;

import com.kali.sanctum.enums.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendOtpRequest(
        @Email
        @NotBlank
        String email,
        @NotNull
        OtpPurpose purpose
) {}
