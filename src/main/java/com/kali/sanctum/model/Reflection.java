package com.kali.sanctum.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Reflection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 300, nullable = false)
    private String entry;

    @ManyToOne
    @JoinColumn(name = "reflection_prompt_id", nullable = false)
    private ReflectionPrompt reflectionPrompt;

    @OneToOne
    @JoinColumn(name = "daily_mood_check_id", nullable = false)
    private DailyMoodCheck dailyMoodCheck;
}
