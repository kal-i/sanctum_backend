package com.kali.sanctum.service.otp;

import com.kali.sanctum.dto.request.SendOtpRequest;
import com.kali.sanctum.dto.request.VerifyOtpRequest;
import com.kali.sanctum.exceptions.InvalidOtpException;
import com.kali.sanctum.exceptions.OtpAlreadySentException;
import com.kali.sanctum.exceptions.OtpExpiredException;
import com.kali.sanctum.exceptions.OtpNotFoundException;
import com.kali.sanctum.exceptions.OtpVerificationException;
import com.kali.sanctum.exceptions.ResourceNotFoundException;
import com.kali.sanctum.model.Otp;
import com.kali.sanctum.model.User;
import com.kali.sanctum.repository.OtpRepository;
import com.kali.sanctum.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class EmailService implements IEmailService {
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final JavaMailSender javaMailSender;

    public void sendOtp(SendOtpRequest request) {
        /*
        * Check if email is registered in the db
        * To avoid sending unnecessary OTP
        * */
        User user = Optional.ofNullable(userRepository.findByEmail(request.email()))
                .orElseThrow(() -> new ResourceNotFoundException("User email is not registered"));

        // Check if there is an existing OTP
        Otp existingOtp = otpRepository.findByUserEmail(user.getEmail());
        if (existingOtp != null) {
            if (existingOtp.getExpiration().isBefore(LocalDateTime.now())) {
                // delete the existing otp
                otpRepository.deleteById(existingOtp.getId());
            } else {
                throw new OtpAlreadySentException("An OTP code was already sent. Please check your email.");
            }
        }

        String otpCode = generateOtp();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail().trim().toLowerCase());
        message.setSubject(request.purpose().name());
        message.setText("Your OTP code is: " + otpCode);
        javaMailSender.send(message);

        Otp otp = Otp.builder()
                .code(otpCode)
                .expiration(LocalDateTime.now().plusMinutes(5))
                .otpPurpose(request.purpose())
                .user(user)
                .build();

        otpRepository.save(otp);
    }

    @Transactional
    @Override
    public void verifyOtp(VerifyOtpRequest verifyOtpRequest) {
        User user = Optional.ofNullable(userRepository.findByEmail(verifyOtpRequest.email()))
                .orElseThrow(() -> new ResourceNotFoundException("User email is not registered"));

        Otp otp = otpRepository.findByUserEmail(user.getEmail());
        if (otp == null) {
            throw new OtpNotFoundException("No OTP code generated for this user.");
        }

        if (otp.getExpiration().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP code expired.");
        }

        if (!otp.getCode().equals(verifyOtpRequest.otpCode())) {
            throw new InvalidOtpException("Invalid OTP code.");
        }

        user.setVerified(true);
        userRepository.save(user);

        otpRepository.deleteById(otp.getId());
    }

    private String generateOtp() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}
