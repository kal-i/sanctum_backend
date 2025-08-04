package com.kali.sanctum.enums;

public enum AuditLogType {
    // User-related
    CREATE_USER,
    UPDATE_USER,
    DELETE_USER,
    LOGIN_USER,
    LOGOUT_USER,
    FAILED_LOGIN_ATTEMPT,

    // Permission-related
    GRANT_PERMISSION,
    REVOKE_PERMISSION,

    // Security-related
    RESET_PASSWORD,
    CHANGE_PASSWORD
}
