## TC-LOGIN-001

**Scenario:**  Valid login
**Endpoint:** POST /auth/login

**Input:**

```json
    {
        "email": "kali@gmail.com",
        "password": "12345678"
    }
```

**Expected Result:**

* HTTP Status: `200 OK`
* Response body returns a success message
* Response body includes a `data` object containing:
    - userDto with:
        * id
        * email matching the request
        * username
        * profileImageUrl
        * role
        * permissions
        * verified set to TRUE
    - accessToken
    - refreshToken

---

## TC-LOGIN-002

**Scenario:** Invalid email format

---

### TC-LOGIN-002A - Missing `@`

**Input:**

```json
    {
        "email": "kaligmail.com",
        "password": "password"
    }
```
**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating invalid email format

---

### TC-LOGIN-002B - Missing domain

**Input:**

```json
    {
        "email": "kali@",
        "password": "password"
    }
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating invalid email format

---

### TC-LOGIN-002C - Missing username

**Input:**

```json
    {
        "email": "@gmail.com",
        "password": "password"
    }
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating invalid email format

---

### TC-LOGIN-002D - Multiple `@`
**Input:**

```json
    {
        "email": "kali@@",
        "password": "password"
    }
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating invalid email format

---

## TC-LOGIN-003

**Scenario:** Invalid password

**Input:**

```json
    {
        "email": "kali@gmail.com",
        "password": "123"
    }
```

**Expected Result:**

* HTTP Status: `401 Unauthorized`
* Error message indicating invalid email or password

---

## TC-LOGIN-004

**Scenario:** Unregistered email

**Input:**

```json
    {
        "email": "unknown@gmail.com",
        "password": "123"
    }
```

**Expected Result:**

* HTTPStatus: `401 Unauthorized`
* Error message indicating invalid email or password

**Notes:**

* The system intentionally returns a generic authentication error to prevent user enumaration attacks or the act of attempting to discover valid usernames or user accounts within a system.

---

## TC-LOGIN-005

**Scenario:** Missing required field(s)

**Input:**

```json
    {
        "email": "kali@gmail.com"
    }
```

**Expected Result:**
    
* HTTP Status: `400 Bad Request`
* Validation error indicating missing required field (`password`)

---

## TC-LOGIN-006

**Scenario:** Empty/Blank fields

**Input:**

```json
    {
        "email": "",
        "password": ""
    }
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Validation error indicating empty or blank fields

**Notes:**

* Validation fails due to `@NotBlank` constraint.

---

## TC-LOGIN-007

**Scenario:** Empty Request Body

**Input:**

```json
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Error message indicating required request body is missing

**Notes:**
 * Request body is completely absent, not malformed JSON.

---

## TC-LOGIN-008

**Scenario:** Malformed JSON

**Input:**

```json
{
    "email": "kali@gmail.com",
    "password": "12345678"
```

**Expected Result:**

* HTTP Status: `400 Bad Request`
* Error message indicating Malformed JSON request
* Request fails during JSON parsing (controller not reached)

**Notes:**
* **Malfromed JSON** is purely on syntax problem, meaning the text cannot be parsed at all.
* Exception thrown by HTTP message converter (e.g., `HttpMessageNotReadableException`)

---

## TC-LOGIN-009

**Scenario:** Unverified account

**Preconditions:**

* User account exists in the system
* Account email is **not verified** 
* Valid credentials are stored for the account

**Input:**

```json
{
    "email": "new_user@gmail.com",
    "password": "password"
}
```

**Expected Result:**

* HTTP Status: `403 Forbidden`
* Error message indicating account not verified
* No authentication token is issued

**Notes:**

* Ensure the account is a valid email (gmail to be specific) because it sends a email OTP via GMAIL SMTP server

---