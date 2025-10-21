package com.kali.sanctum.service.auth;

import com.kali.sanctum.dto.request.CreateUserRequest;
import com.kali.sanctum.dto.request.LoginRequest;
import com.kali.sanctum.dto.request.RefreshTokenRequest;
import com.kali.sanctum.dto.response.JwtResponse;
import com.kali.sanctum.dto.response.UserDto;
import com.kali.sanctum.enums.AuditLogType;
import com.kali.sanctum.enums.Token;
import com.kali.sanctum.enums.TokenStatus;
import com.kali.sanctum.exceptions.CustomAuthException;
import com.kali.sanctum.model.Permission;
import com.kali.sanctum.model.Session;
import com.kali.sanctum.security.jwt.JwtUtils;
import com.kali.sanctum.security.user.CustomUserDetails;
import com.kali.sanctum.security.user.CustomUserDetailsService;
import com.kali.sanctum.service.audit.IAuditLogService;
import com.kali.sanctum.service.session.ISessionService;
import com.kali.sanctum.service.user.IUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpStatus.*;

import java.util.Date;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthService implements IAuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IUserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ISessionService sessionService;
    private final IAuditLogService auditLogService;

    @Value("${auth.token.absoluteExpirationInMils}")
    private int absoluteExpirationTime;

    @Override
    public JwtResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            request.email(), request.password()));

            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            if (!customUserDetails.isVerified()) {
                throw new CustomAuthException(
                        "Account not verified. Please verify your account first before logging in.", FORBIDDEN);
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Date absoluteExpiry = new Date(System.currentTimeMillis() + absoluteExpirationTime);

            String accessToken = jwtUtils.generateTokenForUser(authentication, Token.ACCESS, null);
            String refreshToken = jwtUtils.generateTokenForUser(authentication, Token.REFRESH, absoluteExpiry);

            sessionService.createSession(refreshToken);

            auditLogService.logAction(
                    customUserDetails.getId(),
                    AuditLogType.LOGIN_USER,
                    customUserDetails.getId(),
                    "User logged in successfully");

            UserDto userDto = mapToDto(customUserDetails);

            return new JwtResponse(userDto, accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            auditLogService.logAction(
                    null,
                    AuditLogType.FAILED_LOGIN_ATTEMPT,
                    null,
                    "Failed login attempt for email: " + request.email());

            throw new CustomAuthException("Invalid email or password.", UNAUTHORIZED);
        } catch (DisabledException e) {
            throw new CustomAuthException("Your account has been disabled.", FORBIDDEN);
        } catch (LockedException e) {
            throw new CustomAuthException("Your account has been locked due to multiple failed login attempts.",
                    FORBIDDEN);
        } catch (AuthenticationException e) {
            throw new CustomAuthException("Authentication failed. Please check your credentials and try again.",
                    UNAUTHORIZED);
        }
    }

    @Override
    public UserDto register(CreateUserRequest request) {
        return userService.createUser(request);
    }

    @Override
    public JwtResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        // We're just validating if it is a valid token
        // We're yet to know if it is a refresh token below
        try {
            jwtUtils.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new CustomAuthException("Token has expired. Please log in again.", UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            throw new CustomAuthException("Invalid token format.", BAD_REQUEST);
        } catch (JwtException e) {
            throw new CustomAuthException("Invalid or corrupted token.", UNAUTHORIZED);
        }

        Claims claims = jwtUtils.getAllClaims(refreshToken);
        if (!"REFRESH".equals(claims.get("tokenType"))) {
            throw new CustomAuthException("Provided token is not a refresh token.", BAD_REQUEST);
        }

        long absoluteExpiry = claims.get("absoluteExpiry", Long.class);
        if (System.currentTimeMillis() > absoluteExpiry) {
            sessionService.updateSessionStatus(refreshToken, TokenStatus.EXPIRED);
            throw new CustomAuthException("Refresh token lifetime has ended. Please log in again.",
                    UNAUTHORIZED);
        }

        // Check if the token is still active, otherwise throw an exception
        // Only when the token is active, we allow user to generate a new one
        Session session = sessionService.getSessionByRefreshToken(refreshToken);
        if (session.getStatus() != TokenStatus.ACTIVE) {
            throw new CustomAuthException("Refresh token is no longer active. Please log in again.", UNAUTHORIZED);
        }

        String username = jwtUtils.getUsernameFromToken(refreshToken);
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());

        String newAccessToken = jwtUtils.generateTokenForUser(authentication, Token.ACCESS, null);
        String newRefreshToken = jwtUtils.generateTokenForUser(authentication, Token.REFRESH, new Date(absoluteExpiry));

        sessionService.updatedAndCreateNewSession(refreshToken, newRefreshToken);

        UserDto userDto = mapToDto(customUserDetails);

        return new JwtResponse(
                userDto,
                newAccessToken,
                newRefreshToken);
    }

    private UserDto mapToDto(CustomUserDetails customUserDetails) {
        return UserDto.builder()
                .id(customUserDetails.getId())
                .email(customUserDetails.getEmail())
                .username(customUserDetails.getUsername())
                .profileImageUrl(customUserDetails.getProfileImageUrl())
                .role(customUserDetails.getRole())
                .permissions(customUserDetails.getPermissions().stream()
                        .map(Permission::getName).collect(Collectors.toSet()))
                .isVerified(customUserDetails.isVerified())
                .build();
    }
}
