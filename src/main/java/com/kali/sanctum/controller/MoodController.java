package com.kali.sanctum.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kali.sanctum.dto.request.CreateMoodRequest;
import com.kali.sanctum.dto.response.ApiResponse;
import com.kali.sanctum.dto.response.MoodDto;
import com.kali.sanctum.exceptions.AlreadyExistsException;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.Mood;
import com.kali.sanctum.service.mood.IMoodService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("moods")
public class MoodController {
    private final IMoodService moodService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllMoods() {
        try {
            List<Mood> moods = moodService.getAllMoods();
            List<MoodDto> moodDtos = moods.stream().map(mood -> moodService.convertToDto(mood)).toList();
            return ResponseEntity.ok().body(new ApiResponse("Fetched all moods", moodDtos)); 
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{moodId}")
    public ResponseEntity<ApiResponse> getMoodById(@PathVariable Long moodId) {
        try {
            Mood mood = moodService.getMoodById(moodId);
            MoodDto moodDto = moodService.convertToDto(mood);
            return ResponseEntity.ok().body(new ApiResponse("Fetced mood", moodDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(null, null));
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createMood(@RequestBody CreateMoodRequest request) {
        try {
            Mood mood = moodService.createMood(request);
            MoodDto moodDto = moodService.convertToDto(mood);
            return ResponseEntity.ok().body(new ApiResponse("Mood created", moodDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }


}
