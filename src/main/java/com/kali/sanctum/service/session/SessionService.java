package com.kali.sanctum.service.session;

import com.kali.sanctum.enums.TokenStatus;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.DeviceInfo;
import com.kali.sanctum.model.Session;
import com.kali.sanctum.model.Timestamp;
import com.kali.sanctum.model.User;
import com.kali.sanctum.repository.SessionRepository;
import com.kali.sanctum.security.jwt.JwtUtils;
import com.kali.sanctum.service.user.UserService;
import com.kali.sanctum.utils.TokenHashUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SessionService implements ISessionService{
    private final UserService userService;
    private final SessionRepository sessionRepository;
    private final JwtUtils jwtUtils;
    private final HttpServletRequest request;

    @Override
    public Session getSessionByRefreshToken(String token) {
        String hashedToken = TokenHashUtils.sha256(token);

        return sessionRepository.findByHashedRefreshToken(hashedToken)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
    }

    @Override
    public void createSession(String token) {
        Claims claims = jwtUtils.getAllClaims(token);

        String jti = claims.getId();
        String username = claims.getSubject(); // I think we can omit this because I can just get the user in the current context
        Instant issuedAt = claims.getIssuedAt().toInstant();
        Instant slidingExpiresAt = claims.getExpiration().toInstant();
        Long extractedAbsoluteExpiresAt = claims.get("absoluteExpiry", Long.class);
        Instant convertedAbsoluteExpiresAt = Instant.ofEpochMilli(extractedAbsoluteExpiresAt);

        User user = userService.getAuthenticatedUser();

        String hashedToken = TokenHashUtils.sha256(token);

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null) {
            ipAddress = forwardedFor.split(",")[0];
        }

        DeviceInfo deviceInfo = DeviceInfo.builder()
                .deviceId("client-provided")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        Session session = Session.builder()
                .hashedRefreshToken(hashedToken)
                .jwtTokenId(jti)
                .issuedAt(issuedAt)
                .slidingExpiresAt(slidingExpiresAt)
                .absoluteExpiresAt(convertedAbsoluteExpiresAt)
                .status(TokenStatus.ACTIVE)
                .user(user)
                .deviceInfo(deviceInfo)
                .build();

        sessionRepository.save(session);
    }

    @Override
    public void updatedAndCreateNewSession(String oldToken, String newToken) {
        Session oldSession = getSessionByRefreshToken(oldToken);

        oldSession.setStatus(TokenStatus.ROTATED);
        sessionRepository.save(oldSession);

        createSession(newToken);
    }

    @Override
    public void updateSessionStatus(String token, TokenStatus newStatus) {
        String hashedToken = TokenHashUtils.sha256(token);

        Session session = getSessionByRefreshToken(hashedToken);

        session.setStatus(newStatus);
        sessionRepository.save(session);
    }
}
