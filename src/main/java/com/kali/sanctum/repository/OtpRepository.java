package com.kali.sanctum.repository;

import com.kali.sanctum.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Otp findByUserEmail(String email);
}
