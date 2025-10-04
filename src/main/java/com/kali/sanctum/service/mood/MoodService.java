package com.kali.sanctum.service.mood;

import com.kali.sanctum.dto.request.CreateMoodRequest;
import com.kali.sanctum.dto.response.MoodDto;
import com.kali.sanctum.exceptions.AlreadyExistsException;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.Mood;
import com.kali.sanctum.repository.MoodRepository;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MoodService implements IMoodService {
    private final MoodRepository moodRepository;
    private final ModelMapper modelMapper;

    @Override
    public Mood getMoodById(Long id) {
        return moodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mood not found"));
    }

    @Override
    public Mood createMood(CreateMoodRequest request) {
        return Optional.of(request)
                .filter(mood -> !moodRepository.existsByName(mood.name()))
                .map(req -> {
                    Mood mood = Mood.builder()
                            .name(req.name())
                            .color(req.color())
                            .icon(req.icon())
                            .build();

                    return moodRepository.save(mood);
                }).orElseThrow(() -> new AlreadyExistsException(request.name() + " already exists"));
    }

    @Override
    public MoodDto convertToDto(Mood mood) {
        return modelMapper.map(mood, MoodDto.class);
    }
}
