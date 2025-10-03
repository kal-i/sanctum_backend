package com.kali.sanctum.repository;

import com.kali.sanctum.model.DailyMoodCheck;
import com.kali.sanctum.model.User;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyMoodCheckRepository extends JpaRepository<DailyMoodCheck, Long> {
    Page<DailyMoodCheck> findByUser(User user, Pageable pageable);
    boolean existsByUserAndTimestampCreatedAtBetween(User user, Instant start, Instant end);
}
