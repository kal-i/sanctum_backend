# Test Execution Results

| Test Case | Status | Actual Result | Notes |
| ------- | ------- | ------------ | ------ |
| TC-SEND-OTP-001 | Pass | 400 - validation error for missing field `purpose`| Request rejected as expected |
| TC-SEND-OTP-002 | Pass | 400 - validation error | Enum constraint enforced |
| TC-SEND-OTP-003 | Pass | 404 - Response body returned an error message "User email is not registered" | OTP was not sent |
| TC-SEND-OTP-004 | Pass | 200 - Response body returns a success message indicating OTP was sent | Email received successfully |
| TC-SEND-OTP-005 | Pass | 200 - Response body returns a success message indicating OTP was sent | Expired OTP replaced with new OTP |
| TC-SEND-OTP-006 | Pass | 409 - Response body returns an error message indicating OTP already sent | Active OTP correctly blocked request |
| TC-SEND-OTP-007 | Pass | 200 - new OTP generated  | Expired OTP was override |
| TC-SEND-OTP-008 | Pass | 200 - Response body returns a success message | OTP record verified in database with correct purpose |
| TC-SEND-OTP-009 | Pass | 200 - Response body returns a success message | Email subject matches OTP purpose |
| TC-SEND-OTP-010 | Fail | 404 - not found | Email normalization not applied before lookup |
| TC-SEND-OTP-011 | [P] | [P] | SMTP failure |
| TC-SEND-OTP-012 | Pass | 200 - Response body returns with a success message only | No OTP value exposed in response | 