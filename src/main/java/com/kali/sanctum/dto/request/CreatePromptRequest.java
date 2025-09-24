package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePromptRequest(
        @NotNull
        Long moodId,
        @NotBlank
        String question
) {}
