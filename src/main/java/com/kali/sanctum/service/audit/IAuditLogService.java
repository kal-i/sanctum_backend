package com.kali.sanctum.service.audit;

import com.kali.sanctum.enums.AuditLogType;

public interface IAuditLogService {
    void logAction(Long actorId, AuditLogType auditLogType, Long targetId, String remarks);
}
