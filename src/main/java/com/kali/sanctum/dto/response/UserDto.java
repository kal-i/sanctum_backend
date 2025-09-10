package com.kali.sanctum.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kali.sanctum.enums.Role;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserDto(
        Long id,
        String email,
        String username,
        String profileImageUrl,
        Role role,
        Set<String> permissions,
        @JsonProperty("isVerified")
        boolean isVerified
) {}