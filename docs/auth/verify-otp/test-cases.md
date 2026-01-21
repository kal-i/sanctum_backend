## TC-VERIFY-OTP-001

**Scenario:** Email not registered
**Endpoint:** POST /auth/verify-otp

**Reference:** Validation Rules -> OTP Verification

**Input:**

```json
{
    "email": "unregistered@gmail.com",
    "otpCode": "0000"
}
```

**Expected Result:**

* HTTP Status: `404 Not Found`
* Error message indicating user email is not registered

---

## TC-VERIFY-OTP-002

**Scenario:** Rejected when no OTP exists for the email

**Preconditions:** 

* No OTP exists for the email

**Input:**

```json
{
    "email": "kali@gmail.com",
    "otpCode": "0000"
}
```

**Expected Result:**

* HTTP Status: `404 Not Found`
* Error message indicating no OTP code generated for this user 

---

## TC-VERIFY-OTP-003

**Scenario:** Rejected when OTP is expired

**Preconditions:** 

* An expired OTP must exist for the email

**Input:**

```json
{
    "email": "kali@gmail.com",
    "otpCode": "<OTP code from GMAIL>"
}
```

**Expected Result:**

* HTTP Status: `410 Gone`
* Error message indicating OTP code expired

---

## TC-VERIFY-OTP-004

**Scenario:**  Rejected when OTP code is incorrect

**Preconditions:**

* An active OTP must exist for the email

**Input:**

```json
{
    "email": "kali@gmail.com",
    "otpCode": "<Random OTP code>"
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Error message indicating invalid OTP code

---

## TC-VERIFY-OTP-005

**Scenario:** OTP successfully validated with correct OTP code

**Preconditions:**

* An active OTP must exist for the email

**Input:**

```json
{
    "email": "kali@gmail.com",
    "otpCode": "<OTP code from GMAIL>"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* Response body returns a success message indicating OTP verified

--- 

## TC-VERIFY-OTP-006

**Scenario:** User verification status updated after successful OTP validation

**Preconditions:**

* An active OTP must exist for the email

**Input:**

```json
{
    "email": "kali@gmail.com",
    "otpCode": "<OTP code for GMAIL>"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* Response body returns a success message indicating OTP verified
* User verified status set to TRUE

---

## TC-VERIFY-OTP-007

**Scenario:** OTP deleted after successful verification

**Preconditions:**

* OTP was successfully verified for the email

**Expected Result:**

* OTP record no longer exists in the database

---

## TC-VERIFY-OTP-008

**Scenario:** OTP cannot be reused after successful verification

**Preconditions:**

* OTP was successfully verified for the email

**Input:**

```json
{
    "email": "kali@gmail.com",
    "otpCode": "<OTP code from GMAIL>"
}
```

**Expected Result:**

* HTTP Status: `404 Not Found`
* Error message indicating no OTP code generated for this user

---

## TC-VERIFY-OTP-009

**Scenario:** OTP verification fails when OTP belongs to a different user

**Preconditions:**

* User A has an active OTP
* User B exists and has no OTP

**Input:**

```json
{
    "email": "<User B email>",
    "otpCode": "<User A OTP>"
}
```

**Expected Result:**

* HTTP Status: `404 Not Found`
* Error message indicating no OTP generated for this user

---

## TC-VERIFY-OTP-010

**Scenario:** OTP verification succeeds regardless of OTP purpose

**Preconditions:**

* An active OTP must exist for the email

**Input:**

```json
{
    "email": "kali@gmail.com",
    "otpCode": "<OTP code from GMAIL>"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* Response body returns a successful message indicating OTP verified

**Notes:**

* Current implementation does not validate OTP purpose.

---

## TC-VERIFY-OTP-011

**Scenario:** OTP verification updates user verification status even when purpose is PASSWORD_RESET

**Preconditions:**

* An active OTP must exist for the email

**Input:**

```json
{
    "email": "kali@gmail.com",
    "otpCode": "<OTP code from GMAIL>"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* Response body returns a successful message indicating OTP verified
* User verification set to TRUE

**Notes:**

* This scenario documents existing behavior and exposes a design flaw.

---

## TC-VERIFY-OTP-012

**Scenario:** OTP verification for already verified user

**Preconditions:**

* User is already verified
* An active OTP must exist for the email

**Input:**

```json
{
    "email": "kali@gmail.com",
    "otpCode": "<OTP code from GMAIL>"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* Response body returns a successful message indicating OTP verified
* User remains verified
* OTP record no longer exist in the database

---
