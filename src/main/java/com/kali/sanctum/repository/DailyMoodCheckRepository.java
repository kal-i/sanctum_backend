package com.kali.sanctum.repository;

import com.kali.sanctum.model.DailyMoodCheck;
import com.kali.sanctum.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyMoodCheckRepository extends JpaRepository<DailyMoodCheck, Long> {
    Page<DailyMoodCheck> findByUser(User user, Pageable pageable);
}
