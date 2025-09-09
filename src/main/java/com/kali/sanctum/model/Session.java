package com.kali.sanctum.model;

import com.kali.sanctum.enums.TokenStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(
        name = "sessions",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_token_hash", columnList = "hashedRefreshToken"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_user_status", columnList = "user_id, status")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_jti", columnNames = "jwtTokenId"),
                @UniqueConstraint(name = "uk_token_hash", columnNames = "hashedRefreshToken")
        }
)
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hashedRefreshToken;

    @Column(nullable = false)
    private String jwtTokenId;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false)
    private Instant slidingExpiresAt;

    @Column(nullable = false)
    private Instant absoluteExpiresAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private DeviceInfo deviceInfo;

    @Embedded
    private Timestamp timestamp;
}
