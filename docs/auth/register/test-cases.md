## TC-REGISTER-001

**Scenario:** Valid Credential
**Endpoint:** POST /auth/register

**Preconditions:**

* User email should not exists in the system

**Input:**

```json
{
    "email": "kali@gmail.com",
    "password": "password",
    "username": "kali"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* Response body returns a success mesage
* Response body includes a User DTO in the `data` field with:
    - id (generated)
    - email matching the requeqst
    - username matching the request
    - profileImageUrl set to null
    - role set to USER
    - permission set to null
    - verified set to false

---

## TC-REGISTER-002

**Scenario:** Invalid email format

---

### TC-REGISTER-002A - Missing `@`

**Input:**

```json
{
    "email": "kaligmail.com",
    "password": "password",
    "username": "kali"
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating invalid email format

---

### TC-REGISTER-002B - Email domain missing top-level domain (TLD)

> **Top-Level Domain (TLD):**
> The last part of a domain name (e.g., `.com`, `.edu`, `.org`, `.dev`).

**Input:**

```json
{
    "email": "kali@gmail",
    "password": "password",
    "username": "kali"
}
```

**Expected Result:**

* HTTP Status: `200 OK`
* Response body returns a success message
* Response body includes User DTO in the `data` field with:
    - id (generated)
    - email matching the request
    - username matching the request
    - profileImageUrl set to null
    - role set to USER
    - permission set to null
    - verified set to false

**Notes:**

* The `@Email` constraint does not enforce the presence of a TLD.
& This behavior follows RFC-compliant email validation rules.

---

## TC-REGISTER-003

**Scenario:** Non-Gmail email domain

**Input:**

```json
{
    "email": "kali@yahoo.com",
    "password": "password",
    "username": "kali"
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Error message indicating unsupported email domain

---

## TC-REGISTER-004

**Scenario:** Existing email

**Preconditions:** 

* User email should already exists in the system

**Input:**

```json
{
    "email": "existing.user@gmail.com",
    "password": "password",
    "username": "username"
}
```

**Expected Result:**

* HTTP Status: `409 Conflict` 
* Error message indicating an account with that email already exists

---

## TC-REGISTER-005

**Scenario:** Password less than minimum length allowed

**Reference:** Validation Rules -> Register Endpoint -> Password

**Input:**

```json
{
    "email": "kali@gmail.com",
    "password": "1234567",
    "username": "kali"
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating password must be between **8 to 64 characters** long

---

## TC-REGISTER-006

**Scenario:** Password larger than maximum length allowed

**Reference:** Validation Rules -> Register Endpoint -> Password

**Input:**

```json
{
    "email": "kali@gmail.com",
    "password": "1234567890-1234567890-1234567890-1234567890-1234567890-1234567890",
    "username": "kali"
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating password must be between **8 to 64 characters** long

---

## TC-REGISTER-007

**Scenario:** Empty or blank username

**Input:**

```json
{
    "email": "kali@gmail.com",
    "password": "password",
    "username": ""
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating username cannot be empty or blank

---

## TC-REGISTER-008

**Scenario:** Empty or blank email

**Input:**

```json
{
    "email": "",
    "password": "password",
    "username": "username"
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating email cannot be empty or blank

---

## TC-REGISTER-009

**Scenario:** Empty or blank password

**Input:**

```json
{
    "email": "kali@gmail.com",
    "password": "",
    "username": "kali"
}
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating password cannot be empty or blank

---