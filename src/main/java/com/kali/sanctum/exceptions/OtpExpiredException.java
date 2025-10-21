package com.kali.sanctum.exceptions;

public class OtpExpiredException extends OtpVerificationException {

    public OtpExpiredException(String message) {
        super(message);
    }    
}
