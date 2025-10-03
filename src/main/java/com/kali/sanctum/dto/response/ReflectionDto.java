package com.kali.sanctum.dto.response;

import lombok.Data;

@Data
public class ReflectionDto {
        private Long id;
        private String entry;
        private ReflectionPromptDto reflectionPromptDto;
}
