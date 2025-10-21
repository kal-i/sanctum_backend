package com.kali.sanctum.exceptions;

public class OtpAlreadySentException extends OtpVerificationException {
    public OtpAlreadySentException(String message) {
        super(message);
    }
}
