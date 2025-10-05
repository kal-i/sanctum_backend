package com.kali.sanctum.repository;

import com.kali.sanctum.interfaces.CommonTrigger;
import com.kali.sanctum.interfaces.MoodBubble;
import com.kali.sanctum.model.DailyMoodCheck;
import com.kali.sanctum.model.User;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface DailyMoodCheckRepository extends JpaRepository<DailyMoodCheck, Long> {
    Page<DailyMoodCheck> findByUser(User user, Pageable pageable);

    boolean existsByUserAndTimestampCreatedAtBetween(User user, Instant start, Instant end);

    @Query(value = """
            SELECT m.name AS mood,
                (COUNT(*) * 100.0 / SUM(COUNT(*)) OVER()) AS percentage
            FROM daily_mood_check dmc
            JOIN moods m 
                ON dmc.mood_id = m.id
            WHERE dmc.user_id = :userId
                AND dmc.created_at >= :startDate
                AND dmc.created_at < :endDate
            GROUP BY m.name
            ORDER BY percentage DESC
            """, nativeQuery = true)
    List<MoodBubble> findMoodBubblesByUserAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate
    );

    @Query(value = """
            SELECT tws.word, COUNT(*) AS frequency
            FROM daily_mood_check dmc
            JOIN three_word_summary tws
                ON dmc.id = tws.daily_mood_check_id
            WHERE dmc.user_id = :userId
            GROUP BY tws.word
            ORDER BY frequency DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<CommonTrigger> findCommonDailyMoodTriggersByUser(@Param("userId") Long userId,
            @Param("limit") int limit);
}
