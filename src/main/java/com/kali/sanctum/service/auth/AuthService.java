package com.kali.sanctum.service.auth;

import com.kali.sanctum.dto.request.CreateUserRequest;
import com.kali.sanctum.dto.request.LoginRequest;
import com.kali.sanctum.dto.response.JwtResponse;
import com.kali.sanctum.dto.response.UserDto;
import com.kali.sanctum.enums.AuditLogType;
import com.kali.sanctum.exceptions.CustomAuthException;
import com.kali.sanctum.model.User;
import com.kali.sanctum.security.jwt.JwtUtils;
import com.kali.sanctum.security.user.CustomUserDetails;
import com.kali.sanctum.service.audit.IAuditLogService;
import com.kali.sanctum.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService implements IAuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final IUserService userService;
    private final IAuditLogService auditLogService;

    @Override
    public JwtResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()
                    ));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateTokenForUser(authentication);
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            auditLogService.logAction(
                    customUserDetails.getId(),
                    AuditLogType.LOGIN_USER,
                    customUserDetails.getId(),
                    "User logged in successfully"
            );

            return new JwtResponse(customUserDetails.getId(), jwt);
        } catch (BadCredentialsException e) {
            auditLogService.logAction(
                    null,
                    AuditLogType.FAILED_LOGIN_ATTEMPT,
                    null,
                    "Failed login attempt for email: " + request.getEmail()
            );

            throw new CustomAuthException("Invalid email or password");
        } catch (DisabledException e) {
            throw new CustomAuthException("Your account has been disabled");
        } catch (LockedException e) {
            throw new CustomAuthException("Your account has been locked");
        } catch (AuthenticationException e) {
            throw new CustomAuthException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public UserDto register(CreateUserRequest request) {
        User user = userService.createUser(request);
        return userService.convertToDto(user);
    }
}
