package com.kali.sanctum.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kali.sanctum.dto.response.DailyMoodCheckDto;
import com.kali.sanctum.dto.response.MoodDto;
import com.kali.sanctum.dto.response.ReflectionDto;
import com.kali.sanctum.dto.response.ReflectionPromptDto;
import com.kali.sanctum.dto.response.TimestampDto;
import com.kali.sanctum.model.DailyMoodCheck;
import com.kali.sanctum.model.Mood;
import com.kali.sanctum.model.Reflection;
import com.kali.sanctum.model.ReflectionPrompt;
import com.kali.sanctum.model.Timestamp;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // nested mapping and convertion of entities to dto objects
        modelMapper.typeMap(Mood.class, MoodDto.class);
        modelMapper.typeMap(ReflectionPrompt.class, ReflectionPromptDto.class);
        modelMapper.typeMap(Reflection.class, ReflectionDto.class)
                .addMappings(mapper -> {
                    mapper.map(Reflection::getReflectionPrompt, ReflectionDto::setReflectionPromptDto);
                });
        modelMapper.typeMap(Timestamp.class, TimestampDto.class);

        modelMapper.typeMap(DailyMoodCheck.class, DailyMoodCheckDto.class)
                .addMappings(mapper -> {
                    mapper.map(DailyMoodCheck::getMood, DailyMoodCheckDto::setMoodDto);
                    mapper.map(DailyMoodCheck::getReflection, DailyMoodCheckDto::setReflectionDto);
                    mapper.map(DailyMoodCheck::getTimestamp, DailyMoodCheckDto::setTimestampDto);
                });

        return modelMapper;
    }
}
