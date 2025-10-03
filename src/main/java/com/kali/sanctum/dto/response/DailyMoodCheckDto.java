package com.kali.sanctum.dto.response;

import java.util.Set;

import lombok.Data;

@Data
public class DailyMoodCheckDto {
        private Long id;
        private MoodDto moodDto;
        private Set<String> threeWordSummary;
        private ReflectionDto reflectionDto;
        private TimestampDto timestampDto;
}
