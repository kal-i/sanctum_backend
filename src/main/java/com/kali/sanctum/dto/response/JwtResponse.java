package com.kali.sanctum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private UserDto userDto;
    private String accessToken;
    private String refreshToken;
}
