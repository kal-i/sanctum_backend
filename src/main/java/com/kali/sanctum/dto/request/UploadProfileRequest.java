package com.kali.sanctum.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public record UploadProfileRequest(
    @NotNull(message = "Image file is required.")
    MultipartFile imageFile
) {
    
}
