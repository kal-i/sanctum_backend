package com.kali.sanctum.service.reflectionprompt;

import com.kali.sanctum.dto.request.CreatePromptRequest;
import com.kali.sanctum.dto.response.ReflectionPromptDto;
import com.kali.sanctum.model.ReflectionPrompt;

public interface IReflectionPromptService {
    ReflectionPrompt getById(Long id);
    ReflectionPrompt createPrompt(CreatePromptRequest request);
    ReflectionPromptDto convertToDto(ReflectionPrompt reflectionPrompt);
}
