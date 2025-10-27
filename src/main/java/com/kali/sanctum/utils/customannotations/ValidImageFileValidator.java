package com.kali.sanctum.utils.customannotations;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.lang.Arrays;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidImageFileValidator implements ConstraintValidator<ValidImageFile, MultipartFile> {
    private long maxSize;
    private List<String> allowedTypes;

    @Override
    public void initialize(ValidImageFile constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.allowedTypes = Arrays.asList(constraintAnnotation.allowedTypes());
    }
    
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        if (file.getSize() > maxSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File size exceeds limit.")
            .addConstraintViolation();
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid file type.")
            .addBeanNode();
            return false;
        }

        return true;
    }
}
