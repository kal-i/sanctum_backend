package com.kali.sanctum.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kali.sanctum.dto.response.ApiResponse;
import com.kali.sanctum.service.insightservice.IInsightService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("insights")
@RequiredArgsConstructor
public class InsightController {
    private final IInsightService insightService;

    @GetMapping("weekly-mood")
    public ResponseEntity<ApiResponse> getWeeklyMoodInsight() {
        try {
            String weeklyMoodInsight = insightService.generateWeeklyMoodInsight();
            return ResponseEntity.ok().body(new ApiResponse("Fetched weekly mood insight", weeklyMoodInsight));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("trigger-pattern")
    public ResponseEntity<ApiResponse> getTriggerPatternInsight() {
        try {
            String triggerPatternInsight = insightService.generateTriggerPatternInsight();
            return ResponseEntity.ok().body(new ApiResponse("Fetched trigger pattern", triggerPatternInsight));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("reflection")
    public ResponseEntity<ApiResponse> getReflectionInsight() {
        try {
            String reflectionInsight = insightService.generateWeeklyReflectionsInsight();
            return ResponseEntity.ok().body(new ApiResponse("Fetched reflection insight", reflectionInsight));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), null));
        }
    }
}
