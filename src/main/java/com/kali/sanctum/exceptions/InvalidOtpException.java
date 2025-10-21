package com.kali.sanctum.exceptions;

public class InvalidOtpException extends OtpVerificationException {
    public InvalidOtpException(String message) {
        super(message);
    }
}
