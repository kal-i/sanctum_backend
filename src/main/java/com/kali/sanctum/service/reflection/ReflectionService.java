package com.kali.sanctum.service.reflection;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.kali.sanctum.dto.request.CreateReflectionEntryRequest;
import com.kali.sanctum.dto.response.ReflectionDto;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.DailyMoodCheck;
import com.kali.sanctum.model.Reflection;
import com.kali.sanctum.model.ReflectionPrompt;
import com.kali.sanctum.repository.ReflectionRepository;
import com.kali.sanctum.service.dailymoodcheck.IDailyMoodCheckService;
import com.kali.sanctum.service.reflectionprompt.IReflectionPromptService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReflectionService implements IReflectionService {
    private final ReflectionRepository reflectionRepository;
    private final IReflectionPromptService reflectionPromptService;
    private final IDailyMoodCheckService dailyMoodCheckService;
    private final ModelMapper modelMapper;

    @Override
    public Reflection getById(Long id) {
        return reflectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reflection not found"));
    }

    @Override
    public Reflection addEntry(CreateReflectionEntryRequest request) {
        ReflectionPrompt reflectionPrompt = reflectionPromptService.getById(request.reflectionPromptId());

        DailyMoodCheck dailyMoodCheck = dailyMoodCheckService.getById(request.dailyMoodCheckId());

        return Reflection.builder()
                .entry(request.entry())
                .reflectionPrompt(reflectionPrompt)
                .dailyMoodCheck(dailyMoodCheck)
                .build();
    }

    @Override
    public ReflectionDto convertToDto(Reflection reflection) {
        return modelMapper.map(reflection, ReflectionDto.class);
    }
}
