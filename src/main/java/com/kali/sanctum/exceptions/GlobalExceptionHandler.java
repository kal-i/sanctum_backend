package com.kali.sanctum.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.kali.sanctum.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
        List<Map<String, String>> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    Map<String, String> err = new HashMap<>();
                    err.put("field", error.getField());
                    err.put("message", error.getDefaultMessage());
                    return err;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("status", BAD_REQUEST.value());
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(CustomAuthException.class)
    public ResponseEntity<ApiResponse> handleCustomAuthException(CustomAuthException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new ApiResponse(e.getMessage(), null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        String message = "You are not authorized to access this resource.";
        return new ResponseEntity<>(message, FORBIDDEN);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleJsonParseError(HttpMessageNotReadableException e) {
        Map<String, Object> response = new HashMap<>();
        String message = "Malformed JSON request";
        String error = null;

        if (e.getCause() instanceof InvalidFormatException invalidFormatException) {
            if (invalidFormatException.getTargetType().isEnum()) {
                Class<?> enumType = invalidFormatException.getTargetType();
                Object[] enumConstants = enumType.getEnumConstants();
                String allowedValues = Arrays.stream(enumConstants)
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                String fieldPath = invalidFormatException.getPath().stream()
                        .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "")
                        .collect(Collectors.joining("."));

                String invalidValue = invalidFormatException.getValue().toString();

                message = String.format(
                        "Invalid value '%s' for field '%s'. Allowed values: %s",
                        invalidValue,
                        fieldPath,
                        allowedValues);

                error = "Enum validation error on field '" + fieldPath + "'";
            }
        }

        response.put("status", BAD_REQUEST.value());
        response.put("message", message);
        response.put("error", error != null ? error : e.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(response, BAD_REQUEST);
    }

    @ExceptionHandler(OtpVerificationException.class)
    public ResponseEntity<Object> handleOtpVerificationException(OtpVerificationException e) {
        Map<String, Object> response = new HashMap<>();
        HttpStatus httpStatus = BAD_REQUEST;

        if (e instanceof OtpNotFoundException) {
            httpStatus = NOT_FOUND;
        } else if (e instanceof OtpExpiredException) {
            httpStatus = GONE;
        } else if (e instanceof InvalidOtpException) {
            httpStatus = BAD_REQUEST;
        } else if (e instanceof OtpAlreadySentException) {
            httpStatus = CONFLICT;
        }

        response.put("status", httpStatus.value());
        response.put("message", e.getMessage());
        return new ResponseEntity<>(response, httpStatus);
    }
}
