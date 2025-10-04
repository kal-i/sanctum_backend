package com.kali.sanctum.service.reflectionprompt;

import com.kali.sanctum.dto.request.CreatePromptRequest;
import com.kali.sanctum.dto.response.ReflectionPromptDto;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.Mood;
import com.kali.sanctum.model.ReflectionPrompt;
import com.kali.sanctum.repository.ReflectionPromptRepository;
import com.kali.sanctum.service.mood.MoodService;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReflectionPromptService implements IReflectionPromptService{
    private final MoodService moodService;
    private final ReflectionPromptRepository reflectionPromptRepository;
    private final ModelMapper modelMapper;

    @Override
    public ReflectionPrompt getById(Long id) {
        return reflectionPromptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reflection prompt not found"));
    }

    @Override
    public ReflectionPrompt createPrompt(CreatePromptRequest request) {
        Mood mood = moodService.getMoodById(request.moodId());

        ReflectionPrompt reflectionPrompt = ReflectionPrompt.builder()
                .question(request.question())
                .mood(mood)
                .build();

        return reflectionPromptRepository.save(reflectionPrompt);
    }

    @Override
    public ReflectionPromptDto convertToDto(ReflectionPrompt reflectionPrompt) {
        return modelMapper.map(reflectionPrompt, ReflectionPromptDto.class);
    }
}
