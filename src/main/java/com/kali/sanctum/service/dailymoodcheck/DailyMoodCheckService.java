package com.kali.sanctum.service.dailymoodcheck;

import com.kali.sanctum.dto.request.CreateDailyMoodCheckEntryRequest;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.*;
import com.kali.sanctum.repository.DailyMoodCheckRepository;
import com.kali.sanctum.service.mood.IMoodService;
import com.kali.sanctum.service.reflectionprompt.IReflectionPromptService;
import com.kali.sanctum.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DailyMoodCheckService implements IDailyMoodCheckService {
    private final DailyMoodCheckRepository dailyMoodCheckRepository;
    private final IMoodService moodService;
    private final IReflectionPromptService reflectionPromptService;
    private final IUserService userService;

    @Override
    public DailyMoodCheck getById(Long id) {
        return dailyMoodCheckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Daily mood check not found"));
    }

    @Override
    public Page<DailyMoodCheck> getUserDailyMoodCheck(int page, int size) {
        User user = userService.getAuthenticatedUser();
        Pageable pageable = PageRequest.of(page, size);
        return dailyMoodCheckRepository.findByUser(user, pageable);
    }

    @Override
    public DailyMoodCheck logDailyMoodCheck(CreateDailyMoodCheckEntryRequest request) {
        Mood mood = moodService.getMoodById(request.moodId());
        ReflectionPrompt reflectionPrompt = reflectionPromptService.getById(request.reflectionPromptId());

        if (!reflectionPrompt.getMood().equals(mood)) {
            throw new IllegalArgumentException("Reflection prompt does not match selected mood");
        }

        User user = userService.getAuthenticatedUser();

        DailyMoodCheck dailyMoodCheck = DailyMoodCheck.builder()
                .mood(mood)
                .moodKeywords(request.moodKeywords())
                .reflectionPrompt(reflectionPrompt)
                .journalEntry(request.journalEntry())
                .user(user)
                .build();

        return dailyMoodCheckRepository.save(dailyMoodCheck);
    }
}
