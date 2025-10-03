package com.kali.sanctum.dto.response;

import java.time.Instant;

import lombok.Data;

@Data
public class TimestampDto {
    private Instant createdAt;
    private Instant updatedAt;
}
