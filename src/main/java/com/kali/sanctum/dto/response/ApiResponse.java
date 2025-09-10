package com.kali.sanctum.dto.response;

import lombok.Builder;

@Builder
public record ApiResponse(
        String message,
        Object data
) {}