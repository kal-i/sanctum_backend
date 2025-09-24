package com.kali.sanctum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class DailyMoodCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(
            name = "daily_mood_keywords",
            joinColumns = @JoinColumn(name = "daily_mood_check_id"),
            uniqueConstraints = {
                    @UniqueConstraint(columnNames = {"daily_mood_check_id", "word"})
            }
    )
    @Column(name = "word")
    private Set<String> moodKeywords;

    @Column(nullable = false )
    private String journalEntry;

    @ManyToOne
    @JoinColumn(name = "mood_id")
    private Mood mood;

    @ManyToOne
    @JoinColumn(name = "reflection_prompt_id")
    private ReflectionPrompt reflectionPrompt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private Timestamp timestamp;
}
