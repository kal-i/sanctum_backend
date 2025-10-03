package com.kali.sanctum.controller;

import com.kali.sanctum.dto.request.*;
import com.kali.sanctum.dto.response.ApiResponse;
import com.kali.sanctum.dto.response.JwtResponse;
import com.kali.sanctum.dto.response.UserDto;
import com.kali.sanctum.exceptions.AlreadyExistsException;
import com.kali.sanctum.exceptions.OtpVerificationException;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.service.auth.IAuthService;
import com.kali.sanctum.service.otp.IEmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final IAuthService authService;
    private final IEmailService emailService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse jwtResponse = authService.login(request);
        return ResponseEntity.ok(new ApiResponse("Login successful", jwtResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserDto userDto = authService.register(request);
            return ResponseEntity.ok(new ApiResponse("Register successful", userDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        try {
            emailService.sendOtp(request);
            return ResponseEntity.ok(new ApiResponse("Otp sent. Please check your email.", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (OtpVerificationException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        try {
            emailService.verifyOtp(request);
            return ResponseEntity.ok(new ApiResponse("Otp verified.", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (OtpVerificationException e) {
            return ResponseEntity.status(BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(@RequestBody RefreshTokenRequest request) {
        JwtResponse jwtResponse = authService.refresh(request);
        return ResponseEntity.ok(new ApiResponse("Token refreshed", jwtResponse));
    }
}
