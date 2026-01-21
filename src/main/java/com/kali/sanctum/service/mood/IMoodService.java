package com.kali.sanctum.service.mood;

import java.util.List;

import com.kali.sanctum.dto.request.CreateMoodRequest;
import com.kali.sanctum.dto.request.UpdateMoodRequest;
import com.kali.sanctum.dto.response.MoodDto;
import com.kali.sanctum.model.Mood;

public interface IMoodService {
    List<MoodDto> getAllMoods();
    MoodDto getMoodById(Long id);
    MoodDto createMood(CreateMoodRequest request);
    MoodDto updateMood(Long id, UpdateMoodRequest request);
    void deleteMood(Long id);
    MoodDto convertToDto(Mood mood);
    Mood getMoodEntityById(Long id);
    void ensureMoodDoesNotExist(String name);
    boolean existsByName(String name);
}
