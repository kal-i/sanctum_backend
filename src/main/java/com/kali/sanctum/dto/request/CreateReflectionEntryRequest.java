package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReflectionEntryRequest(
    @NotNull
    Long dailyMoodCheckId,
    @NotNull
    Long reflectionPromptId,    
    @NotBlank
    String entry
) {}
