package com.kali.sanctum.service.mood;

import com.kali.sanctum.dto.request.CreateMoodRequest;
import com.kali.sanctum.model.Mood;

public interface IMoodService {
    Mood getMoodById(Long id);
    Mood createMood(CreateMoodRequest request);
}
