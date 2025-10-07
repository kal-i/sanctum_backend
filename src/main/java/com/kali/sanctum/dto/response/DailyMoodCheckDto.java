package com.kali.sanctum.dto.response;

import java.util.Set;

import lombok.Data;

@Data
public class DailyMoodCheckDto {
        private Long id;
        private Set<String> threeWordSummary;
        private ReflectionDto reflectionDto;
        private TimestampDto timestampDto;
}
