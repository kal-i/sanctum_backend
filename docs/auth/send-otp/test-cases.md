## TC-SEND-OTP-001

**Scenario:** OTP purpose is missing
**Endpoint:** POST /auth/send-otp

**Reference:** Validation Rules -> OTP Purpose 

**Input:**

```json
{
    "email": "kali@gmail.com"
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating to specify the OTP purpose

---

## TC-SEND-OTP-002

**Scenario:** OTP purpose is invalid

**Input:**
```json
{
    "purpose": "AUTH",
    "email": "kali@gmail.com"
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating invalid value for field `purpose`

---

## TC-SEND-OTP-003

**Scenario:** OTP not sent when Email is not registered (for any OTP purpose)

**Preconditions:** Email does not exist in the system

**Input:**

```json
{
    "purpose": "VERIFICATION",
    "email": "unregistered@gmail.com"
}
```

**Expected Result:**

* HTTP Status: `404 Not Found`
* Error message indicating user email is not registered

---

## TC-SEND-OTP-004

**Scenario:** OTP sent when email is registered and no active OTP exists (for any OTP purpose)

**Input:**

```json
{
    "purpose": "VERIFICATION",
    "email": "kali@gmail.com"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* Response body returns a success message indicating OTP was sent

---

## TC-SEND-OTP-005

**Scenario:** Expired OTP replaced with a new OTP (for the same purpose)

**Preconditions:**

* An OTP exists for the email
* OTP expiration time is **before** current time

**Input:**

```json
{
    "purpose": "VERIFICATION",
    "email": "kali@gmail.com"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* Response body returns a success message indicating new OTP was sent

---

## TC-SEND-OTP-006

**Scenario:** Active OTP prevents sending a new OTP (for the same purpose)

**Preconditions:** An active OTP for that email must exists

**Input:**

```json
{
    "purpose": "VERIFICATION",
    "email": "kali@gmail.com"
}
```

**Expected Result:**

* HTTP Status: `409 Conflict`
* Error message indicating an OTP was already sent

---

## TC-SEND-OTP-007

**Scenario:** OTP expiration time is enforced

**Preconditions:** There must be an existing OTP that is expired

**Input:**

```json
{
    "purpose": "VERIFICATION",
    "email": "kali@gmail.com"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* Existing expired OTP is ignored
* New OTP is generated and stored

---

## TC-SEND-OTP-008

**Scenario:** OTP generated and stored with correct purpose

**Input:**

```json
{
    "purpose": "PASSWORD_RESET",
    "email": "kali@gmail.com"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* OTP record is created in the database
* Stored OTP record has `purpose = PASSWORD_RESET`

---

## TC-SEND-OTP-009

**Scenario:** Email subject matches the OTP purpose

**Input:**

```json
{
    "purpose": "VERIFICATION",
    "email": "kali@gmail.com"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* OTP email subject reflects the OTP purpose (e.g., "Account Verification" for VERIFICATION)

---

## TC-SEND-OTP-010

**Scenario:** OTP email is sent to normalized email address

**Input:**

```json
{
    "purpose": "VERIFICATION",
    "email": "Kali@gmail.com"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* OTP email is sent to `kali@gmail.com` (lowercase)
* Email address is normalized before sending

---

## TC-SEND-OTP-011

**Scenario:** OTP is not persisted when email delivery fails

**Preconditions:**

* Email exists in the system
* Email delivery service is unavailable (SMTP failure)

**Input:**

```json
{
    "purpose": "VERIFICATION",
    "email": "kali@gmail.com"
}
```

**Expected Result:**

* HTTP Status: `500 Internal Server Error`
* Error message indicating OTP could not be sent
* **No OTP record is saved in the database for the email**

---

## TC-SEND-OTP-012

**Scenario:** OTP value is not exposed in API responses

**Input:**

```json
{
    "purpose": "VERIFICATION",
    "email": "kali@gmail.com"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* Response body contains only a success message
* Response body does **not** OTP value or sensitive fields

---