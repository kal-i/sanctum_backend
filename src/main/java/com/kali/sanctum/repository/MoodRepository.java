package com.kali.sanctum.repository;

import com.kali.sanctum.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoodRepository extends JpaRepository<Mood, Long> {
    boolean existsByName(String name);
}
