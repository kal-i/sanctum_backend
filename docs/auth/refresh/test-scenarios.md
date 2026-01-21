# Test Scenarios - Refresh Endpoint

1. Refresh request is rejected when refresh token is missing
2. Refresh request is rejected when token format is invalid
3. Refresh request is rejected when signature is invalid / corrupted
4. Refresh request is rejected when token is expired
5. Refresh request is rejected when token is not a refresh token
6. Refresh request is rejected when refresh token absolute lifetime has ended
7. Refresh token session is marked EXPIRED when absolute lifetime is exceeded
8. Refresh request is rejected when refresh token session does not exist
9. Refresh request is rejected when refresh token status is not ACTIVE
    * EXPIRED
    * REVOKED
    * INVALIDATED
10. Refresh token generates a new access token
11. Refresh token generates a new refresh token
12. Old refresh token is invalidated after refresh
13. New refresh token session is created and marked ACTIVE
14. User information is returned in the response
15. Old refresh token cannot be reused after rotation
16. Access token cannot be used as a refresh token
17. Refresh token cannot be used after logout