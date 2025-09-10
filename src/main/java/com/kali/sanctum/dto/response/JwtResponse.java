package com.kali.sanctum.dto.response;

import lombok.Builder;

@Builder
public record JwtResponse(
        UserDto userDto,
        String accessToken,
        String refreshToken
) {}