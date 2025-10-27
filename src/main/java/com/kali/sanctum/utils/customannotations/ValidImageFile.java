package com.kali.sanctum.utils.customannotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidImageFileValidator.class)
public @interface ValidImageFile {
    String message() default "Invalid file type";
    long maxSize() default 5 * 1024 * 1024; // default 5MB
    String[] allowedTypes() default { "image/png", "image/jpeg" };
}
