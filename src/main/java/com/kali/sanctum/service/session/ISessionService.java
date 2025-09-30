package com.kali.sanctum.service.session;

import com.kali.sanctum.enums.TokenStatus;
import com.kali.sanctum.model.Session;

public interface ISessionService {
    Session getSessionByRefreshToken(String token);
    void createSession(String token);
    void updatedAndCreateNewSession(String oldToken, String newToken);
    void updateSessionStatus(String token, TokenStatus newStatus);
}
