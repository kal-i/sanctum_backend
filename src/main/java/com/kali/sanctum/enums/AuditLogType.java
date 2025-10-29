package com.kali.sanctum.enums;

public enum AuditLogType {
    // User-related
    CREATE_USER,
    UPDATE_USER,
    DELETE_USER,
    LOGIN_USER,
    LOGOUT_USER,
    CREATE_USER_ATTEMPT,
    FAILED_LOGIN_ATTEMPT,

    // Permission-related
    GRANT_PERMISSION,
    REVOKE_PERMISSION,

    // Security-related
    RESET_PASSWORD,
    CHANGE_PASSWORD,

    // Mood-related
    CREATE_MOOD,
    UPDATE_MOOD,
    DELETE_MOOD
}
