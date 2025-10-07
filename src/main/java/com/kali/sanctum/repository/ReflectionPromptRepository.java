package com.kali.sanctum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kali.sanctum.model.ReflectionPrompt;

public interface ReflectionPromptRepository extends JpaRepository<ReflectionPrompt, Long> {
    @Query(value = "SELECT * FROM reflection_prompt WHERE mood_id = :moodId ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    ReflectionPrompt findRandomByMoodId(@Param("moodId") Long moodId);
}
