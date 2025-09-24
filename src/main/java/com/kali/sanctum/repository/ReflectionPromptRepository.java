package com.kali.sanctum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReflectionPromptRepository extends JpaRepository<com.kali.sanctum.model.ReflectionPrompt, Long> {
    @Query(value = "SELECT * FROM guided_reflection WHERE mood_id = :moodId ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    ReflectionPromptRepository findRandomByMoodId(@Param("moodId") Long moodId);
}
