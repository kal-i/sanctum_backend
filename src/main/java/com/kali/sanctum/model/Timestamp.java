package com.kali.sanctum.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class Timestamp {
    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private Instant updatedAt;
}
