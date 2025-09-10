package com.kali.sanctum.dto.request;

import lombok.Builder;

@Builder
public record UpdateUserRequest(
        String username
) {}