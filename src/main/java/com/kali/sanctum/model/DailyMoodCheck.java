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
            name = "three_word_summary",
            joinColumns = @JoinColumn(name = "three_word_summary_id"),
            uniqueConstraints = {
                    @UniqueConstraint(columnNames = {"three_word_summary_id", "word"})
            }
    )
    @Column(name = "word")
    private Set<String> threeWordSummary;

    @Column(nullable = false )
    private String journalEntry;

    @ManyToOne
    @JoinColumn(name = "mood_id")
    private Mood mood;

    @OneToOne(mappedBy = "dailyMoodCheck", cascade = CascadeType.ALL, orphanRemoval = true)
    private Reflection reflection;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private Timestamp timestamp;
}
