package com.kali.sanctum.service.mood;

import com.kali.sanctum.dto.request.CreateMoodRequest;
import com.kali.sanctum.dto.request.UpdateMoodRequest;
import com.kali.sanctum.dto.response.MoodDto;
import com.kali.sanctum.enums.AuditLogType;
import com.kali.sanctum.exceptions.AlreadyExistsException;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.Mood;
import com.kali.sanctum.model.User;
import com.kali.sanctum.repository.MoodRepository;
import com.kali.sanctum.service.audit.IAuditLogService;
import com.kali.sanctum.service.user.IUserService;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MoodService implements IMoodService {
    private final MoodRepository moodRepository;
    private final IUserService userService;
    private final IAuditLogService auditLogService;
    private final ModelMapper modelMapper;

    @Override
    public List<MoodDto> getAllMoods() {
        List<Mood> moods = moodRepository.findAll();
        List<MoodDto> moodDtos = moods.stream()
                .map(this::convertToDto).toList();
        return moodDtos;
    }

    @Override
    public MoodDto getMoodById(Long id) {
        Mood mood = getMoodEntityById(id);
        return convertToDto(mood);
    }

    @Override
    public Mood getMoodEntityById(Long id) {
        return moodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mood not found."));
    }

    @Override
    public MoodDto createMood(CreateMoodRequest request) {
        return Optional.of(request)
                .filter(mood -> !moodRepository.existsByName(mood.name()))
                .map(req -> {
                    Mood mood = Mood.builder()
                            .name(req.name())
                            .color(req.color().intValue())
                            .icon(req.icon())
                            .build();

                    Mood savedMood = moodRepository.save(mood);

                    User actor = userService.getAuthenticatedUser();

                    auditLogService.logAction(
                            actor.getId(),
                            AuditLogType.CREATE_MOOD,
                            savedMood.getId(),
                            "Created mood " + savedMood.getName());

                    return convertToDto(savedMood);
                }).orElseThrow(() -> new AlreadyExistsException(request.name() + " already exists."));
    }

    @Override
    public MoodDto updateMood(Long id, UpdateMoodRequest request) {
        return moodRepository.findById(id)
                .map(existingMood -> {
                    // We're doingn service-layer validation because our DTO fields are optional
                    if (request.name() != null && !request.name().isBlank()) {
                        ensureMoodDoesNotExist(request.name());
                        existingMood.setName(request.name());
                    }

                    if (request.color() != null) {
                        existingMood.setColor(request.color());
                    }

                    if (request.icon() != null && !request.name().isBlank()) {
                        existingMood.setIcon(request.icon());
                    }

                    Mood updatedMood = moodRepository.save(existingMood);

                    User actor = userService.getAuthenticatedUser();

                    auditLogService.logAction(
                            actor.getId(),
                            AuditLogType.UPDATE_MOOD,
                            updatedMood.getId(),
                            "Updated mood with IDs: " + updatedMood.getId());

                    return convertToDto(updatedMood);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Mood not found."));
    }

    @Override
    public void deleteMood(Long id) {
        moodRepository.findById(id)
                .ifPresentOrElse(mood -> {
                    moodRepository.delete(mood);

                    User actor = userService.getAuthenticatedUser();

                    auditLogService.logAction(
                            actor.getId(),
                            AuditLogType.UPDATE_MOOD,
                            mood.getId(),
                            "Removed mood with IDs: " + mood.getId());
                }, () -> new ResourceNotFoundException("Mood not found."));
    }

    @Override
    public MoodDto convertToDto(Mood mood) {
        return modelMapper.map(mood, MoodDto.class);
    }

    @Override
    public void ensureMoodDoesNotExist(String name) {
        if (existsByName(name)) {
            throw new AlreadyExistsException("Mood " + name + " already exists.");
        }
    }

    @Override
    public boolean existsByName(String name) {
        return moodRepository.existsByName(name);
    }
}
