package com.kali.sanctum.dto.request;

import com.kali.sanctum.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String username;
}
