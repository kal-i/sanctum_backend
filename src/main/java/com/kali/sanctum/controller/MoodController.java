package com.kali.sanctum.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kali.sanctum.dto.request.CreateMoodRequest;
import com.kali.sanctum.dto.request.UpdateMoodRequest;
import com.kali.sanctum.dto.response.ApiResponse;
import com.kali.sanctum.dto.response.MoodDto;
import com.kali.sanctum.exceptions.AlreadyExistsException;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.service.mood.IMoodService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("moods")
public class MoodController {
    private final IMoodService moodService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllMoods() {
        try {
            List<MoodDto> moods = moodService.getAllMoods();
            return ResponseEntity.ok().body(new ApiResponse("Successfully retrieved all moods", moods));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{moodId}")
    public ResponseEntity<ApiResponse> getMoodById(@PathVariable Long moodId) {
        try {
            MoodDto mood = moodService.getMoodById(moodId);
            return ResponseEntity.ok().body(new ApiResponse("Successfully retrieved mood", mood));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(null, null));
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createMood(@Valid @RequestBody CreateMoodRequest request) {
        try {
            MoodDto mood = moodService.createMood(request);
            return ResponseEntity.ok().body(new ApiResponse("Successfully created mood", mood));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }


    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PatchMapping("/{moodId}")
    public ResponseEntity<ApiResponse> updateMood(
            @PathVariable Long moodId,
            @Valid @RequestBody UpdateMoodRequest request) {
        try {
            MoodDto mood = moodService.updateMood(moodId, request);
            return ResponseEntity.ok().body(new ApiResponse("Successfully updated mood", mood));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @DeleteMapping("/{moodId}")
    public ResponseEntity<ApiResponse> deleteMood(@PathVariable Long moodId) {
        try {
            moodService.deleteMood(moodId);
            return ResponseEntity.ok().body(new ApiResponse("Successfully deleted mood", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
