package com.kali.sanctum.service.otp;

import com.kali.sanctum.dto.request.SendOtpRequest;
import com.kali.sanctum.dto.request.VerifyOtpRequest;

public interface IEmailService {
    void sendOtp(SendOtpRequest request);
    void verifyOtp(VerifyOtpRequest request);
}
