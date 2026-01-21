package com.kali.sanctum.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class ReflectionPrompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String question;

    @ManyToOne
    @JoinColumn(name = "mood_id", nullable = false)
    private Mood mood;

    @OneToOne
    @JoinColumn(name = "reflection_id", nullable = false, unique = true)
    private Reflection reflection;
}
