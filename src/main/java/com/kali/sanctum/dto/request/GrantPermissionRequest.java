package com.kali.sanctum.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;

public record GrantPermissionRequest(
        @NotEmpty(message = "Permissions list cannot be empty.") 
        Set<String> permissions
) {}
