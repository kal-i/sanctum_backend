package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateMoodRequest(
        @NotBlank(message = "Mood name is required.")
        String name,

        @NotNull(message = "Mood color is required.")
        Integer color,

        String icon
) {}
