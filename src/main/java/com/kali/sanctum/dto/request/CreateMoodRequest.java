package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateMoodRequest(
        @NotBlank
        String name,
        int color,
        String icon
) {}
