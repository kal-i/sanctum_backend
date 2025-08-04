package com.kali.sanctum.dto.response;

import com.kali.sanctum.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private Role role;
    private Set<String> permissions;
}
