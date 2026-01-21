# Test Scenarios - Send OTP Endpoint

1. Verify request is rejected when OTP purpose is missing
2. Verify request is rejected when OTP purpose is invalid
3. Verify OTP is not sent when email is not registered
4. Verify OTP is sent when email is registered and no active OTP exists
5. Verify expired OTP is replaced with a new OTP for the same purpose
6. Verify active OTP prevents sending a new OTP for the same purpose
7. Verify OTP expiration time is enforced
8. Verify OTP is generated and stored with the correct purpose
9. Verify OTP email subject matches the OTP purpose
10. Verify OTP email is sent to normalized email address
11. Verify OTP is not persisted when email delivery fails
12. Verify OTP value is not exposed in API responses