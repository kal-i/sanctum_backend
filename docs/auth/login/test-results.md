## Test Execution Results

| Test Case | Status | Actual Result | Notes |
| -------- | ------ | ------------- | ----- |
| TC-LOGIN-001 | Pass | 200 - Success message returned with userDto, access token, and refresh token | - |
| TC-LOGIN-002A | Pass | 400 - validation error | Missing `@` |
| TC-LOGIN-002B | Pass | 400 - validation error | Missing domain |
| TC-LOGIN-002C | Pass | 400 - validation error | Missing username |
| TC-LOGIN-002D | Pass | 400 - validation error | Multiple `@` |
| TC-LOGIN-003 | Pass | 401 - invalid credentials | Invalid password |
| TC-LOGIN-004 | Pass | 401 - invalid credentials | Generic error returned to prevent user enumeration |
| TC-LOGIN-005 | Pass | 400 - validation error | Missing required field |
| TC-LOGIN-006 | Pass | 400 - validation error | Empty/blank fields |
| TC-LOGIN-007 | Pass | 400 - parsing error | Missing request body
| TC-LOGIN-008 | Pass | 400 - parsing error | Malformed JSON |