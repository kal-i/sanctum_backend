# Test Scenarios - Verify OTP Endpoint

1. Verify request is rejected when email is not registered
2. Verify request is rejected when no OTP exists for the email
3. Verify request is rejected when OTP is expired
4. Verify request is rejected when OTP code is incorrect
5. Verify OTP is successfully validated with correct OTP code
6. Verify user verification status is updated after successful OTP validation
7. Verify OTP is deleted after successful verification
8. Verify OTP cannot be reused after successful verification
9. Verify OTP verification fails when OTP belongs to a different user
10. Verify OTP verification succeeds regardless of OTP purpose
11. Verify OTP verification updates user verification status even when purpose is PASSWORD_RESET
12. Verify OTP verification for already verified user