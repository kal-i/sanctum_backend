package com.kali.sanctum.model;

import com.kali.sanctum.enums.OtpPurpose;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private LocalDateTime expiration;

    @OneToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private OtpPurpose otpPurpose;
}
