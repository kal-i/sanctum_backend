package com.kali.sanctum.dto.request;

import lombok.Builder;

@Builder
public record UpdateMoodRequest(
    String name,
    Integer color,
    String icon
) {}
