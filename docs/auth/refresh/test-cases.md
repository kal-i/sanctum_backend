## TC-REFRESH-001

**Scenario:** Refresh token missing
**Endpoint:** POST /auth/refresh/

**Reference:**

**Input:**

```json
{}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating `refreshToken` is required

---

## TC-REFRESH-002

**Scenario:** Token format invalid

**Input:**

```json
{
    "refreshToken": "<not-a-jwt>"
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Error message indicating invalid token format

---

## TC-REFRESH-003

**Scenario:** Signature is invalid / corrupted

**Input:**

```json
{
    "refreshToken": "<corrupted_jwt>"
}
```

**Expected Result:**

* HTTP Status: `401 Unauthorized`
* Error message indicating invalid or corrupted token

---

## TC-REFRESH-004

**Scenario:** Token expired (JWT expiration)

**Preconditions:**

* JWT refresh token is expired (`exp` claim)

**Input:**

```json
{
    "refreshToken": "<expired_refresh_token>"
}
```

**Expected Result:**

* HTTP Status: `401 Unauthorized`
* Error message indicating token has expired

---

## TC-REFRESH-005

**Scenario:** Token is not a refresh token

**Preconditions:**

* Valid **access token** exists

**Input:**

```json
{
    "refreshToken": "<valid_access_token>"
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Error message indicating provided token is not a refresh token

---

## TC-REFRESH-006

**Scenario:** Absolute lifetime ended

**Preconditions:**

* Refresh token JWT is valid
* `absoluteExpiry` < current system time

**Input:**

```json
{
    "refreshToken": "<refresh_token_with_expired_absolute_lifetime>"
}
```

**Expected Result:**

* HTTP Status: `401 Unauthorized`
* Error message indicating refresh token absolute lifetime has ended
* Refresh token session marked `Expired`

--- 

## TC-REFRESH-007

**Scenario:** Session marked EXPIRED after absolute expiry

**Preconditions:**

* Same as TC-REFRESH-006

**Expected Result:**

* Session status in the database is updated to `EXPIRED`

---

## TC-REFRESH-008

**Scenario:** Sesion does not exist

**Preconditions:**

* Refresh token is structurally valid
* No session exists for the refresh token

**Input:**

```json
{
    "refresh_token": "<valid_refresh_token_not_in_db>"
}
```

**Expected Result:**

* HTTP Status: `401 Unauthorized`
* Error message indicating refresh token is no longer active

---

## TC-REFRESH-009

**Scenario:** Refresh token status not ACTIVE

**Preconditions:**

* Refresh token exists in DB
* Token status is `EXPIRED` / `REVOKED` / `ROTATED`

**Input:**

```json
{
    "refreshToken": "<inactive_refresh_token>"
}
```

**Expected Result:**

* HTTP Status: `401 Unauthorized`
* Error message indicating refresh token is no longer active

---

## TC-REFRESH-010

**Scenario:** New access token generated

**Preconditions:**

* Valid refresh token exists
* Token status is ACTIVE

**Input:**

```json
{
    "refreshToken": "<valid_refresh_token>"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* Response body contains a new access token
* Access token differs from previous access token

---

## TC-REFRESH-011

**Scenario:** New refresh token generated

**Preconditions:**

* Same as TC-REFRESH-10

**Expected Result:**

* HTTP Status: `200 OK`
* Response body contains a new refresh token
* New refresh token != old refresh token

---

## TC-REFRESH-012

**Scenario:** Old refresh token is invalidated

**Preconditions:**

* Refresh operation completed successfully

**Expected Result:**

* Old refresh token session marked `EXPIRED` or `ROTATED`
* Old refresh token rejected on reuse

---

## TC-REFRESH-013

**Scenario:** New refresh token session ACTIVE

**Preconditions:**

* Refresh operation completed successfully

**Expected Result:**

* New refresh token stored in database
* Session status = `ACTIVE`

---

## TC-REFRESH-014

**Scenario:** User info returned

**Preconditions:**

* Valid refresh token

**Expected Result:**

* Response body contains user DTO
* User fields match authenticated user

---

## TC-REFRESH-015

**Scenario:** Old refresh token reuse blocked

**Preconditions:**

* Refresh already performed once

**Input:**

```json
{
    "refreshToken": "<old_refresh_token>"
}
```

**Expected Result:**

* HTTP Status: `401 Unauthorized`
* Error message indicating refresh token is no longer active

---

## TC-REFRESH-016

**Scenario:** Access token cannot refresh

**Preconditions:**

* Valid access token exists

**Input:**

```json
{
    "refreshToken": "<access_token>"
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Error message indicating token is not a refresh token

---

## TC-REFRESH-017

**Scenario:** Refresh token after logout

**Preconditions:**

* User logged out
* Refresh token session marked `REVOKED` or `EXPIRED`

**Input:**

```json
{
    "refreshToken": "<logged_out_refresh_token>"
}
```

**Expected Result:**

* HTTP Status: `401 Unauthorized`
* Error message indicating refresh token is no longer active

---

