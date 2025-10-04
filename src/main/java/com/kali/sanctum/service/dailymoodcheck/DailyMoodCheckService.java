package com.kali.sanctum.service.dailymoodcheck;

import com.kali.sanctum.dto.request.LogDailyMoodCheckRequest;
import com.kali.sanctum.dto.response.DailyMoodCheckDto;
import com.kali.sanctum.dto.response.MoodDto;
import com.kali.sanctum.dto.response.ReflectionDto;
import com.kali.sanctum.dto.response.ReflectionPromptDto;
import com.kali.sanctum.dto.response.TimestampDto;
import com.kali.sanctum.exceptions.AlreadyExistsException;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.interfaces.CommonTrigger;
import com.kali.sanctum.model.*;
import com.kali.sanctum.repository.DailyMoodCheckRepository;
import com.kali.sanctum.service.mood.IMoodService;
import com.kali.sanctum.service.reflectionprompt.IReflectionPromptService;
import com.kali.sanctum.service.user.IUserService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyMoodCheckService implements IDailyMoodCheckService {
    private final DailyMoodCheckRepository dailyMoodCheckRepository;
    private final IMoodService moodService;
    private final IReflectionPromptService reflectionPromptService;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Override
    public DailyMoodCheck getById(Long id) {
        return dailyMoodCheckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Daily mood check not found"));
    }

    @Override
    public Page<DailyMoodCheck> getUserDailyMoodCheck(int page, int size) {
        User user = userService.getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page - 1, size);
        return dailyMoodCheckRepository.findByUser(user, pageable);
    }

    @Override
    public Page<DailyMoodCheckDto> getUserDailyMoodCheckDto(int page, int size) {
        Page<DailyMoodCheck> pageEntity = getUserDailyMoodCheck(page, size);
        return pageEntity.map(this::convertToDto);
    }

    @Override
    public DailyMoodCheck logDailyMoodCheck(LogDailyMoodCheckRequest request) {
        User user = userService.getAuthenticatedUser();

        if (hasLoggedToday(user)) {
            throw new AlreadyExistsException("You already logged your mood today");
        }

        Mood mood = moodService.getMoodById(request.moodId());
        ReflectionPrompt reflectionPrompt = reflectionPromptService.getById(request.reflectionPromptId());

        if (!reflectionPrompt.getMood().equals(mood)) {
            throw new IllegalArgumentException("Reflection prompt does not match selected mood");
        }

        DailyMoodCheck dailyMoodCheck = DailyMoodCheck.builder()
                .mood(mood)
                .threeWordSummary(request.threeWordSummary())
                .user(user)
                .build();

        Reflection reflection = Reflection.builder()
                .entry(request.entry())
                .reflectionPrompt(reflectionPrompt)
                .dailyMoodCheck(dailyMoodCheck)
                .build();

        // Link reflection object to daily mood check entity
        dailyMoodCheck.setReflection(reflection);

        // Cascade will save both objects automatically
        return dailyMoodCheckRepository.save(dailyMoodCheck);
    }

    private boolean hasLoggedToday(User user) {
        Instant startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);

        return dailyMoodCheckRepository.existsByUserAndTimestampCreatedAtBetween(user, startOfDay, endOfDay);
    }

    @Override
    public List<CommonTrigger> getCommonDailyMoodTriggers(int limit) {
        User user = userService.getAuthenticatedUser();

        return dailyMoodCheckRepository.findCommonDailyMoodTriggersByUser(user.getId(), limit);
    }

    @Override
    public DailyMoodCheckDto convertToDto(DailyMoodCheck dailyMoodCheck) {
        return modelMapper.map(dailyMoodCheck, DailyMoodCheckDto.class);
    }
}
