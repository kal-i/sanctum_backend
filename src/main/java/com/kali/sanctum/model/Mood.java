package com.kali.sanctum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "moods")
public class Mood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private int color;

    @Column(nullable = false)
    private String icon;

    @OneToMany(mappedBy = "mood", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReflectionPrompt> reflectionPrompts;
}
