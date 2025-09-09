package com.kali.sanctum.repository;

import com.kali.sanctum.enums.TokenStatus;
import com.kali.sanctum.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findAllByUserId(Long userId);
    Optional<Session> findByHashedRefreshToken(@Param("hash") String hashedToken);
    List<Session> findAllByStatus(TokenStatus status);
    List<Session> findByUserIdAndStatus(Long userId, TokenStatus status);
}
