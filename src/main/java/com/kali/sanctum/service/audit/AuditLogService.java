package com.kali.sanctum.service.audit;

import com.kali.sanctum.enums.AuditLogType;
import com.kali.sanctum.model.AuditLog;
import com.kali.sanctum.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AuditLogService implements IAuditLogService{
    private final AuditLogRepository auditLogRepository;

    @Override
    public void logAction(Long actorId, AuditLogType auditLogType, Long targetId, String remarks) {
        AuditLog log = AuditLog.builder()
                .actorId(actorId)
                .auditLogType(auditLogType)
                .targetId(targetId)
                .remarks(remarks)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(log);
    }
}
