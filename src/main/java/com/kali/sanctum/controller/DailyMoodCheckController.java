package com.kali.sanctum.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kali.sanctum.dto.request.LogDailyMoodCheckRequest;
import com.kali.sanctum.dto.response.ApiResponse;
import com.kali.sanctum.dto.response.DailyMoodCheckDto;
import com.kali.sanctum.enums.DateRange;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.interfaces.CommonTrigger;
import com.kali.sanctum.interfaces.MoodBubble;
import com.kali.sanctum.model.DailyMoodCheck;
import com.kali.sanctum.service.dailymoodcheck.IDailyMoodCheckService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("daily-mood-checks")
public class DailyMoodCheckController {
    private final IDailyMoodCheckService dailyMoodCheckService;

    @GetMapping
    public ResponseEntity<ApiResponse> getDailyMoodChecks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "7") int size) {
        try {
            Page<DailyMoodCheckDto> dailymoodchecks = dailyMoodCheckService.getUserDailyMoodCheck(page, size);
            return ResponseEntity.ok(new ApiResponse("Successfully fetched daily mood checks", dailymoodchecks));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("generate-reflection-prompt")
    public ResponseEntity<ApiResponse> generateReflectionPrompt(@RequestParam String mood) {
        try {
            String prompt = dailyMoodCheckService.generateContextualPrompt(mood);
            return ResponseEntity.ok().body(new ApiResponse("Generated reflection prompt", prompt));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("log-daily-mood-check")
    public ResponseEntity<ApiResponse> logDailyMoodCheck(@RequestBody LogDailyMoodCheckRequest request) {
        try {
            DailyMoodCheck dailyMoodCheck = dailyMoodCheckService.logDailyMoodCheck(request);
            DailyMoodCheckDto dailyMoodCheckDto = dailyMoodCheckService.convertToDto(dailyMoodCheck);
            return ResponseEntity.ok(new ApiResponse("Logged daily mood check", dailyMoodCheckDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("get-mood-bubbles")
    public ResponseEntity<ApiResponse> getMoodBubbles(@RequestParam(defaultValue = "weekly") String dateRange) {
        try {
            System.out.println("Triggered this endpoint\n\n\n\n\n");
            DateRange range = DateRange.from(dateRange);
            List<MoodBubble> moodBubbles = dailyMoodCheckService.getMoodBubbles(range);
            return ResponseEntity.ok().body(new ApiResponse("Fetched mood bubbles", moodBubbles));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("get-common-triggers")
    public ResponseEntity<ApiResponse> getCommonTriggers(@RequestParam(defaultValue = "5") int limit) {
        try {
            List<CommonTrigger> commonTriggers = dailyMoodCheckService.getCommonDailyMoodTriggers(limit);
            return ResponseEntity.ok().body(new ApiResponse("Fetched common triggers", commonTriggers));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
}