package com.kali.sanctum.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record CreateDailyMoodCheckEntryRequest(
        @NotNull
        Long moodId,
        @NotNull
        Long reflectionPromptId,
        Set<String> moodKeywords,
        @NotBlank
        String journalEntry
) {}
