# Test Execution Results

| Test Case | Status | Actual Result | Notes |
| -------- | ------ | ------------- | ----- |
| TC-REFRESH-001 | Pass | HTTP 400 Bad Request returned with validation message: "Refresh token is required." | Missing `refreshToken` field in the Request Body |
| TC-REFRESH-002 | Pass | HTTP 401 Unauthorized returned with error message: "Invalid or corrupted token." | Tested using values that do not follow the JWT three-part structure (`header.payload.signature`) to trigger format-level validation before signature or expiration checks. |
| TC-REFRESH-003 | Pass | HTTP 401 Unauthorized returned with error message: "Invalid or corrupted token." | A **valid refresh token** was tampered by modifying **one character** in the signature part. |
| TC-REFRESH-004 | Pass | HTTP 401 Unauthorized returned with error message: "Token has expired. Please log in again." | - |
| TC-REFRESH-005 | Pass | HTTP 400 Bad Request returned with error message: "Provided token is not a refresh token." | Used a **valid access token** instead of a refresh token |
| TC-REFRESH-006 | P | P | P |
| TC-REFRESH-007 | P | P | P |
| TC-REFRESH-008 | - | - | - |
| TC-REFRESH-009 | Pass | HTTP 401 Unauthorized returned with error message: "Refresh token is no longer active. Please log in again." | - |
| TC-REFRESH-010 | Pass | 200 - OK returning a new access token  | - |
| TC-REFRESH-011 | Pass | 200 - OK returning a new refresh token | - |
| TC-REFRESH-012 | Pass | Previous token session status set to `ROTATED`, thus rejected when reuse | - |
| TC-REFRESH-013 | Pass | Newly refresh token session status set to `ACTIVE` | The new session token's relationship with user is not recorded (`null`) |
| TC-REFRESH-014 | Pass | returned a response body containing `data` object with `userDto` field | - |
| TC-REFRESH-015 | Pass | HTTP 401 Unauthorized returned with error message: "Refresh token is no longer active. Please log in again." | - |
| TC-REFRESH-016 | Pass | HTTP 400 Bad Request returned with error message: "Provided token is not a refresh token." | - |
| TC-REFRESH-017 | P | P | P |
