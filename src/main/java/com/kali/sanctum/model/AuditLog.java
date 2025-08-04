package com.kali.sanctum.model;

import com.kali.sanctum.enums.AuditLogType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long actorId;
    private AuditLogType auditLogType;
    private Long targetId;
    private String remarks;
    private LocalDateTime timestamp;
}
