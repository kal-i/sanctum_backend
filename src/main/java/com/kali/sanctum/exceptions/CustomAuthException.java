package com.kali.sanctum.exceptions;

import org.springframework.http.HttpStatus;

public class CustomAuthException extends RuntimeException {
    private final HttpStatus status;

    public CustomAuthException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getHttpStatus() {
        return status;
    }
}
