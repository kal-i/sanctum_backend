package com.kali.sanctum.service.dailymoodcheck;

import com.kali.sanctum.dto.request.CreatePromptRequest;
import com.kali.sanctum.dto.request.LogDailyMoodCheckRequest;
import com.kali.sanctum.dto.response.DailyMoodCheckDto;
import com.kali.sanctum.enums.DateRange;
import com.kali.sanctum.exceptions.AlreadyExistsException;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.interfaces.CommonTrigger;
import com.kali.sanctum.interfaces.MoodBubble;
import com.kali.sanctum.model.*;
import com.kali.sanctum.repository.DailyMoodCheckRepository;
import com.kali.sanctum.service.aipromptservice.IAiPromptService;
import com.kali.sanctum.service.audit.IAuditLogService;
import com.kali.sanctum.service.mood.IMoodService;
import com.kali.sanctum.service.reflectionprompt.IReflectionPromptService;
import com.kali.sanctum.service.user.IUserService;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final IAiPromptService aiPromptService;
    private final IAuditLogService auditLogService;
    private final ModelMapper modelMapper;

    @Override
    public DailyMoodCheck getById(Long id) {
        return dailyMoodCheckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Daily mood check not found."));
    }

    @Override
    public Page<DailyMoodCheckDto> getUserDailyMoodCheck(int page, int size) {
        User user = userService.getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<DailyMoodCheck> pageEntity = dailyMoodCheckRepository.findByUser(user, pageable); 
        return pageEntity.map(this::convertToDto);
    }

    @Override
    public String generateContextualPrompt(String mood) {
        if (!moodService.existsByName(mood)) {
            throw new ResourceNotFoundException("Mood " + mood + " not found.");
        }

        User user = userService.getAuthenticatedUser();

        Instant now = Instant.now();
        Instant startDate = Instant.now().minus(7, ChronoUnit.DAYS);

        List<MoodBubble> moodBubbles = dailyMoodCheckRepository.findMoodBubblesByUserAndDateRange(user.getId(),
                startDate, now);
        List<CommonTrigger> commonTriggers = dailyMoodCheckRepository.findCommonDailyMoodTriggersByUser(user.getId(),
                5);

        Optional<String> lastReflectionOpt = Optional
                .ofNullable(dailyMoodCheckRepository.findLastReflectionEntryByUser(user.getId()));

        if (moodBubbles.isEmpty() && commonTriggers.isEmpty() && lastReflectionOpt.isEmpty()) {
            return generateSimpleReflectionPrompt(mood);
        }

        String lastReflection = lastReflectionOpt.orElse("No reflection found");

        String moodSummary = moodBubbles.isEmpty() ? "No mood data this week"
                : moodBubbles.stream().map(mb -> String.format("%s (%.1f%%)", mb.getMood(), mb.getPercentage()))
                        .collect(Collectors.joining(", "));

        String triggersSummary = commonTriggers.isEmpty() ? "No frequent triggers identified"
                : commonTriggers.stream().map(CommonTrigger::getWord).collect(Collectors.joining(", "));

        String context = String.format("""
                You are a gentle and supportive reflection coach.
                You help users process emotions through compassionate questions.

                Username: %s
                Current mood: %s
                Recent moods: %s
                Frequent triggers: %s
                Last reflection: "%s"

                Based on the user's recent mood trends, frequent triggers, and current mood,
                craft a *single, concise reflection question* that helps them understand their emotions
                or find balance. Keep it empathetic and natural, as if guiding a friend.
                Avoid repeating previous reflections.
                """, user.getUsername(), mood, moodSummary, triggersSummary, lastReflection);

        return aiPromptService.generatePrompt(context);
    }

    private String generateSimpleReflectionPrompt(String mood) {
        String prompt = String.format("""
                    Generate a short reflection question for someone feeling %s.
                    Make it thoughtful and encouraging introspection.
                    Avoid mentioning past events.
                """, mood);

        return aiPromptService.generatePrompt(prompt);
    }

    @Override
    public DailyMoodCheck logDailyMoodCheck(LogDailyMoodCheckRequest request) {
        User user = userService.getAuthenticatedUser();

        if (hasLoggedToday(user)) {
            throw new AlreadyExistsException("You already logged your mood today");
        }

        Mood mood = moodService.getMoodEntityById(request.moodId());
        ReflectionPrompt reflectionPrompt = reflectionPromptService
                .createPrompt(new CreatePromptRequest(mood.getId(), request.reflectionPrompt()));

        DailyMoodCheck dailyMoodCheck = DailyMoodCheck.builder()
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
    public List<MoodBubble> getMoodBubbles(DateRange dateRange) {
        User user = userService.getAuthenticatedUser();

        Instant now = Instant.now();
        Instant startDate;

        switch (dateRange) {
            case WEEKLY -> startDate = now.minus(7, ChronoUnit.DAYS);
            case MONTHLY -> startDate = now.minus(1, ChronoUnit.MONTHS);
            case YEARLY -> startDate = now.minus(1, ChronoUnit.YEARS);
            default -> throw new IllegalArgumentException("Unsupported range:" + dateRange);
        }

        return dailyMoodCheckRepository.findMoodBubblesByUserAndDateRange(user.getId(), startDate, now);
    }

    @Override
    public DailyMoodCheckDto convertToDto(DailyMoodCheck dailyMoodCheck) {
        return modelMapper.map(dailyMoodCheck, DailyMoodCheckDto.class);
    }
}
