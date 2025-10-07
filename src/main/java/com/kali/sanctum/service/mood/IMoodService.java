package com.kali.sanctum.service.mood;

import java.util.List;

import com.kali.sanctum.dto.request.CreateMoodRequest;
import com.kali.sanctum.dto.response.MoodDto;
import com.kali.sanctum.model.Mood;

public interface IMoodService {
    List<Mood> getAllMoods();
    Mood getMoodById(Long id);
    Mood createMood(CreateMoodRequest request);
    MoodDto convertToDto(Mood mood);
}
