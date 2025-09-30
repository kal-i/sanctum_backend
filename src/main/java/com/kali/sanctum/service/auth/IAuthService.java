package com.kali.sanctum.service.auth;

import com.kali.sanctum.dto.request.CreateUserRequest;
import com.kali.sanctum.dto.request.LoginRequest;
import com.kali.sanctum.dto.request.RefreshTokenRequest;
import com.kali.sanctum.dto.response.JwtResponse;
import com.kali.sanctum.dto.response.UserDto;

public interface IAuthService {
    JwtResponse login(LoginRequest request);
    UserDto register(CreateUserRequest request);
    JwtResponse refresh(RefreshTokenRequest request);
}
