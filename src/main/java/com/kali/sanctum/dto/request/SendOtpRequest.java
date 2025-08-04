package com.kali.sanctum.dto.request;

import com.kali.sanctum.enums.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendOtpRequest {
    @Email
    @NotBlank
    private String email;

    @NotNull
    private OtpPurpose purpose;
}
