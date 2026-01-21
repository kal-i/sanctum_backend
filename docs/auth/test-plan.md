# Test Plan - Authentication API

## Objective
To verify the correctness, security, and reliability of the authentication API endpoints.

## Scope
- Login
- Register
- Send OTP
- Verify OTP
- Token refresh

## Out of Scope
- UI testing
- Performance testing
- Load / stress testing

## Test Type
- Manual API testing

## Tools
- Postman
- HTTPie

## Validation Rules / Requirements Under Test

### Register Endpoint

* **Email**
    * Must follow RFC-compliant email format
    * Must be unique

* **Password**
    * Minimum length: **8 characters**
    * Maximum length: **64 characters**

* **Username**
    * Must be non-empty

### Login Endpoint**

* Email must exist in the system
* Password must match the registered credentials

### OTP Purpose
- Mandatory field
- Must match one of the allowed enum values:
    - VERIFICATION
    - PASSWORD_RESET
- Case-sensitive
- Invalid values must result in request rejection

### OTP Verification
- Email must be registered
- OTP must exist for the email
- OTP must not be expired
- OTP code must match exactly
- OTP is single-use and deleted after successful verification

## Risks
- Validation behavior may differ from real-world business expectations due to the use of RFC-compliant constraints.

## Known Limitations & Assumptions

### Email Validation (RFC-Compliant Behavior)

The authentication endpoints use the standard `@Email` constraint provided by Hibernate Validator.

This validation follows RFC-compliant email standards and does not enforce the presence of a top-level domain (TLD). As a result, email addresses such as `user@localhost` are considered valid by the system.

This behavior is intentional and aligns with email standards.
