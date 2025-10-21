package com.kali.sanctum.dto.request;

import com.kali.sanctum.enums.Role;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateUserRoleRequest(
        @NotNull(message = "Role is required.")
        Role role
) {}
