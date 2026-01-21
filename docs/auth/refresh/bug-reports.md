# BUG-REFRESH-001

## Title
Newly refresh token session created without user association

## Type
Bug / Defect

## Severity
High

## Priority
High

## Component / Module
Auth -> Refresh Token

## Environment
- OS: macOS 26.0.1
- Backend: AuthService v1.0.0 
- Database: H2
- Platform: Local
- Tool: HTTPie

## Description
When a new refresh token session is created, it doesn't include the user associated with the request.

## Related Test Case(s)
* TC-REFRESH-013

## Preconditions
* User is logged in
* A refresh token exists

## Steps to Reproduce
1. Log in to obtain a refresh token
2. Call the refresh endpoint using the refresh token
3. Call the refresh endpoint again using the new refresh token (immediately after step 2)

## Actual Result
* HTTP Status: `200 OK`
* Response body includes `data` object containing:
    - userDto
    - accessToken
    - refreshToken
* Previous session status: ROTATED
* New session status: ACTIVE
* New session's user reference: null

## Expected Result
* HTTP Status: `200 OK`
* Response body includes `data` object with:
    - userDto
    - accessToken
    - refreshToken
* Previous session status: ROTATED
* New session status: ACTIVE
* New session references the user associated with the refresh request

## Notes
* Issue occurs consistently on every refresh

## Possible Root Cause
* Session creation logic does not assign the user when creating a new session during refresh
* The `updateAndCreateNewSession()` method may not propagate the user reference to the new session