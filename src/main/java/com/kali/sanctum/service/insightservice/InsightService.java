package com.kali.sanctum.service.insightservice;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kali.sanctum.service.aipromptservice.IAiPromptService;
import com.kali.sanctum.service.user.IUserService;
import com.kali.sanctum.interfaces.*;
import com.kali.sanctum.model.User;
import com.kali.sanctum.repository.DailyMoodCheckRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsightService implements IInsightService {
        private final DailyMoodCheckRepository dailyMoodCheckRepository;
        private final IUserService userService;
        private final IAiPromptService aiPromptService;

        @Override
        public String generateWeeklyMoodInsight() {
                User user = userService.getAuthenticatedUser();
                Instant now = Instant.now();
                Instant startDate = now.minus(7, ChronoUnit.DAYS);

                String moodDistributionSummary = getMoodDistributionSummary(user.getId(), startDate, now);
                String frequentTriggerSummary = getFrequentTriggerSummary(user.getId(), Optional.of(5));
                String reflectionSummary = summarizeReflections(user.getId(), startDate, now);

                if (moodDistributionSummary.equals("No mood data this week")
                                && reflectionSummary.equals("No reflection entries to summary")) {
                        return "Insufficient data to generate a trigger pattern.";
                }

                String context = String.format(
                                """
                                You are an empathetic emotional well-being coach analyzing a user's emotional week.

                                User data:
                                - Mood summary for the past 7 days: %s
                                - Frequent triggers this week: %s
                                - Recent reflection excerpts: %s

                                Task:
                                Write a short 2-3  sentence insight summarizing the user's emotional trend for that week.
                                Be kind, constructive, and emotionally supportive.
                                Use a reflective and human tone, not robotic or overly clinical.
                                Avoid repeating exact words from their reflections.
                                End with gentle encouragement for the upcoming week.
                                
                                When writing your response, format the output as readable paragraphs.
                                Avoid using '\n' or escape sequences; use natural paragraph spacing instead.
                                """,
                                moodDistributionSummary, frequentTriggerSummary, reflectionSummary);

                return aiPromptService.generatePrompt(context);
        }

        @Override
        public String generateTriggerPatternInsight() {
                User user = userService.getAuthenticatedUser();
                Instant now = Instant.now();
                Instant startDate = now.minus(7, ChronoUnit.DAYS);

                String moodDistributionSummary = getMoodDistributionSummary(user.getId(), startDate, now);
                String frequentTriggerSummary = getFrequentTriggerSummary(user.getId(), Optional.of(5));
                String reflectionSummary = summarizeReflections(user.getId(), startDate, now);

                if (moodDistributionSummary.equals("No mood data this week")
                                && reflectionSummary.equals("No reflection entries to summary")) {
                        return "Insufficient data to generate a trigger pattern.";
                }

                String context = String.format("""
                                You are a reflective assistant helping a user understand emotional patterns.

                                User data:
                                - Frequent triggers: %s
                                - Mood distribution: %s
                                - Reflection highlights: %s

                                Task:
                                Write a brief insight (2 sentences max) that helps the user understand
                                what these triggers might say about their emotional state or daily habits.
                                Offer one constructive suggestion for emotional balance.
                                Be gentle and human, not analytical.

                                When writing your response, format the output as readable paragraphs.
                                Avoid using '\n' or escape sequences; use natural paragraph spacing instead.
                                """, frequentTriggerSummary, moodDistributionSummary, reflectionSummary);

                return aiPromptService.generatePrompt(context);
        }

        @Override
        public String generateWeeklyReflectionsInsight() {
                User user = userService.getAuthenticatedUser();
                Instant now = Instant.now();
                Instant startDate = now.minus(7, ChronoUnit.DAYS);

                String moodDistributionSummary = getMoodDistributionSummary(user.getId(), startDate, now);
                String frequentTriggerSummary = getFrequentTriggerSummary(user.getId(), Optional.of(5));
                String reflectionSummary = summarizeReflections(user.getId(), startDate, now);

                if (moodDistributionSummary.equals("No mood data this week")
                                && reflectionSummary.equals("No reflection entries to summary")) {
                        return "Insufficient data to generate a trigger pattern.";
                }

                String context = String.format("""
                                You are a compassionate reflection coach reviewing a user's recent journal entries.

                                User data:
                                - Recent reflections: %s
                                - Common mood patterns: %s
                                - Frequent triggers: %s

                                Task:
                                Analyze the user's reflections and provide a short, encouraging summary
                                about their emotional growth or recurring patterns.
                                Be supportive, use second-person perspective ("you"), and highlight self-awareness.
                                Avoid harsh language or advice - use empathy and reflection instead.

                                When writing your response, format the output as readable paragraphs.
                                Avoid using '\n' or escape sequences; use natural paragraph spacing instead.
                                 """, reflectionSummary, moodDistributionSummary, frequentTriggerSummary);

                return aiPromptService.generatePrompt(context);
        }

        private String getMoodDistributionSummary(Long userId, Instant startDate, Instant endDate) {
                List<MoodBubble> moodDistribtion = dailyMoodCheckRepository.findMoodBubblesByUserAndDateRange(
                                userId,
                                startDate,
                                endDate);

                String moodDistributionSummary = moodDistribtion.isEmpty()
                                ? "No mood data this week"
                                : moodDistribtion.stream()
                                                .map(mb -> String.format("%s (%.1f%%)", mb.getMood(),
                                                                mb.getPercentage()))
                                                .collect(Collectors.joining(", "));

                return moodDistributionSummary;
        }

        private String getFrequentTriggerSummary(Long userId, Optional<Integer> limitOpt) {
                int limit = limitOpt.orElse(3);

                List<CommonTrigger> frequentTriggers = dailyMoodCheckRepository.findCommonDailyMoodTriggersByUser(
                                userId,
                                limit);

                String frequentTriggerSummary = frequentTriggers.isEmpty()
                                ? "No frequent triggers identified"
                                : frequentTriggers.stream()
                                                .map(CommonTrigger::getWord)
                                                .collect(Collectors.joining(", "));

                return frequentTriggerSummary;
        }

        private String summarizeReflections(Long userId, Instant startDate, Instant endDate) {
                List<String> reflectionEntries = dailyMoodCheckRepository.findReflectionEntriesByUserAndDateRange(
                                userId,
                                startDate,
                                endDate);

                if (reflectionEntries.isEmpty()) {
                        return "No reflection entries to summary";
                }

                String context = String.format(
                                """
                                                You are an empathethic reflection coach analyzing a user's recent journal entries.

                                                User data:
                                                - Reflection entries:
                                                %s

                                                Task:
                                                Summarize these reflections into 3-4 sentences.
                                                Focus on emotional tone, recurring thoughts, and general themes.
                                                Do not quote directly or repeat phase; instead, descrie the meaning behind them.
                                                Use a compassionate, conversational tone - as if you were gently describing what the user might be experiencing or learning.
                                                Avoid giving advice or solutions; focus on awareness and understanding.
                                                """,
                                reflectionEntries);

                return aiPromptService.generatePrompt(context);
        }
}
