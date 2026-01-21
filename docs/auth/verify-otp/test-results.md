# Test Execution Results

| Test Case | Status | Actual Result | Notes |
| -------- | ------ | ------------- | ----- |
| TC-VERIFY-OTP-001 | Pass | HTTP 404 Not Found returned with error message: "User email is not registered" | - |
| TC-VERIFY-OTP-002 | Pass | HTTP 404 Not Found returned with error message: "No OTP code generated for this user" | - |
| TC-VERIFY-OTP-003 | Pass | HTTP 410 Gone returned with error message: "OTP code expired" | - |
| TC-VERIFY-OTP-004 | Pass | HTTP 400 Bad Response returned with error message: "Invalid OTP code" | - |
| TC-VERIFY-OTP-005 | Pass | 200 - indicating OTP verified | - |
| TC-VERIFY-OTP-006 | Pass | 200 - OTP verified, set user verification status to TRUE | - |
| TC-VERIFY-OTP-007 | Pass | OTP verification returned HTTP 200 OK; OTP record was deleted from the database | - |
| TC-VERIFY-OTP-008 | Pass | HTTP 404 Not Found returned with error message: "No OTP code generated for this user" | OTP record was removed after successful OTP verification |
| TC-VERIFY-OTP-009 | Pass | HTTP 404 Not Found returned with error message: "No OTP code generated for this user" | - |
| TC-VERIFY-OTP-010 | Pass | 200 - OK indicating OTP verified | OTP purpose is not validated during verification; OTP is accepted regardless of purpose (design flaw) |
| TC-VERIFY-OTP-011 | Pass | 200 - OK indicating OTP verified and user status set to TRUE | - |
| TC-VERIFY-OTP-012 | Pass | 200 - OK indicating OTP verified, user remains verified, and OTP record deleted from the database | - |