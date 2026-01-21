## Test Execution Results

| Test Case | Status | Actual Result | Notes |
| -------- | ------ | ------------- | ----- |
| TC-REGISTER-001 | Pass | 200 - Response body contains success message and User DTO in the `data` field | - |
| TC-REGISTER-002A | Pass | 400 - validation error | Missing `@` |
| TC-REGISTER-002B | Pass | 200 - Response body contains success message and User DTO | `@Email` allows domain without TLD (RFC-compliant behavior) |
| TC-REGISTER-003 | Fail | 200 - Response body contains success message and User DTO |
| TC-REGISTER-004 | Pass | 409 - authentication error | Email already exists |
| TC-REGISTER-005 | Pass | 400 - validation error | Password less than the allowed minimum length |
| TC-REGISTER-006 | Pass | 400 - validation error | Password greater than the allowed maximum length |
| TC-REGISTER-007 | Pass | 400 - validation error | Empty or blank username |
| TC-REGISTER-008 | Pass | 400 - validation error | Empty or blank email |
| TC-REGISTER-009 | Pass | 400 - validation error | Empty or blank password |