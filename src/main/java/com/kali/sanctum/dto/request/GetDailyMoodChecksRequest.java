package com.kali.sanctum.dto.request;

import lombok.Builder;

@Builder
public record GetDailyMoodChecksRequest(
        int page,
        int size) {
}
