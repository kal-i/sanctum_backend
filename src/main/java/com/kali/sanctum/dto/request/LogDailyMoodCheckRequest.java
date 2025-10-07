package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record LogDailyMoodCheckRequest(
        @NotNull
        Long moodId,
        @NotNull
        String reflectionPrompt,
        Set<String> threeWordSummary,
        @NotBlank
        String entry
) {}
