package com.kali.sanctum.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kali.sanctum.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {
        private Long id;
        private String email;
        private String username;
        private String profileImageUrl;
        private Role role;
        private Set<String> permissions;
        @JsonProperty("verified")
        private boolean isVerified;
}