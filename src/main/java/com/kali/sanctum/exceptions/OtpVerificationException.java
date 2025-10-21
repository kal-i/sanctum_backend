package com.kali.sanctum.exceptions;

public abstract class OtpVerificationException extends RuntimeException {
  public OtpVerificationException(String message) {
    super(message);
  }
}
