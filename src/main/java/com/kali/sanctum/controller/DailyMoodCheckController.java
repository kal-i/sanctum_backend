package com.kali.sanctum.controller;

import static org.springframework.http.HttpStatus.*;

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
import com.kali.sanctum.exceptions.ResourceNotFoundException;
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
            Page<DailyMoodCheckDto> dailymoodchecks = dailyMoodCheckService.getUserDailyMoodCheckDto(page, size);
            return ResponseEntity.ok(new ApiResponse("Fetched daily mood checks", dailymoodchecks));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("log-daily-mood-check")
    public ResponseEntity<ApiResponse> logDailyMoodCheck(@RequestBody LogDailyMoodCheckRequest request) {
        try {
            DailyMoodCheck dailyMoodCheck = dailyMoodCheckService.logDailyMoodCheck(request);
            return ResponseEntity.ok(new ApiResponse("Logged daily mood check", dailyMoodCheck));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
