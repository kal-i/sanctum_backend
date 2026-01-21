# 🕊️ Sanctum - Emotional Landscape Backend
Sanctum is a backend platform designed for emotional self-reflection. It allows users to log daily moods, write reflections, and receive **AI-generated contextual reflection prompts and insights** based on their emotional history.

While Sanctum includes a full authentication and authorization system, its core purpose is to help users **understand emotional patterns, triggers, and growth over time** through thoughtful reflection.

---

## ✨ Core Capabilities

### Emotional Logging & Reflection
* Daily mood logging (one entry per day)
* Optional Three-word summary (short mood descriptors)
* Journal-style reflection entries
* Mood-based reflection prompts
* Historical mood aggregation (weekly / monthly / yearly)

### AI-Powered Insights
* Contextual reflection prompts based on:
    * Current mood
    * Recent mood trends
    * Common emotional triggers
    * Previous reflections
* Weekly emotional insight summaries
* Trigger pattern analysis
* Reflection summaries highlighting emotional growth
* Designed to be empathetic, supportive, and non-clinical

### Pluggable AI Prompt Service
* AI integration via a service interface (`IAiPromptService`)
* Current implementation uses OpenAI
* Architecture allows easy replacement with other LLM providers or mock services
* Business logic is decoupled from AI vendor implementation

---

## 🔐 Authentication & Security
* Email + password authentication
* OTP-based email verification (Gmail SMTP)
* Password reset flows
* JWT access tokens
* Refresh tokens with:
    * Rotation
    * Sliding expiration
    * Absolute lifetime enforcement
* Database-backed session tracking
* Refresh token hashing for storage security
* No logout endpoint (session lifecycle enforced via token status)

---

## 🧭 Authorization Model
Sanctum supports **RBAC and PBAC**:

### Roles
* Super Admin
* Admin
* User

### Permissions
* Permissions are **not limited to roles only**, but can be **assigned, revoked, and evaluated individually** to control access at specific action or resource level.
* Runtime permission granting and revocation
* Role and permission checks are enforced at the controller layer using Spring Security. Additional service-level checks are applied where required by business rules, particularly for ownership-based access control.

For example, a user may have permission to update a profile, but service-level validation ensures that the user can only update their own profile unless they possess elevated administrative permissions.

---

## 🧾 Audit Logging
* Centralized audit log service
* Tracks:
    * Authentication events
    * User creation and updates
    * Role and permission changes
    * Mood and reflection-related administrative actions
* Designed for traceability and system transparency

---

## 🧠 Architecture & Design
* Spring Boot-based backend
* Service-oriented architecture with clear separation of responsibilities across controllers, services, and infrastructure layers (database, email, AI, storage, and security integrations)
* Heavy use of interfaces to:
    * Improve testability
    * Encourage extensibility
    * Reduce coupling
* Dependency inversion for AI services and storage providers
* Defensive validation at the service layer to protect business rules and handle nullable and cross-field constraints consistently.
* Explicit domain modeling, where core business concepts are represented as first-class domain entities rather than generic data structures:
    * Mood
    * Reflection
    * Triggers
    * Sessions
    * Audit logs

---

## 🛠️ Tech Stack
* **Language:** Java
* **Framework:** Spring Boot
* **Security:** Spring Security, JWT
* **Database:** JPA / Hibernate (configurable)
* **AI Integration:** OpenAI (via pluggable service inteface)
* **Email**: Gmail SMTP
* **Storage**: Local file system (pluggable storage service)
* **Build Tool:** Maven
* **Testing:** Manual API Testing (HTTPie / Postman)

---

## 🚀 Project Setup & Running Locally
Sanctum is designed to run locally with minimal configuration, requiring only a small set of secrets.

---

### 📋 Requirements
* **Java:** 21 (configurable)
* **Build Tool:** Maven
* **Database:** H2 (default, in-memory)
* **Email:** Gmail SMTP credentials
* **AI Provider:** OpenAI API key

---

### 🔐 Required Environment Variables
Sanctum uses environment variables for sensitive credentials.

You must define the following:

```bash
JWT_SECRET=your_jwt_secret_key
MAIL_USERNAME=your_gmail_address
MAIL_PASSWORD=your_gmail_app_password
OPENAI_API_KEY=your_openai_api_key
```

> **⚠️ Important:**
> Gmail requires an **App Password**, not your normal account password.

---

### Configuration Overview
The application uses `application.properties` for configuration.

#### Authentication
* JWT-based access & refresh tokens
* Token rotation and absolute expiration enforced
* Secrets injected via environment variables

#### Email (SMTP)
* Gmail SMTP with STARTTLS
* Used for OTP verification and password resets

#### Database
* H2 in-memory database (default)
* H2 console enabled at `/h2-console`

#### AI Integration
* OpenAI used via a pluggable service interface
* API key injected via environment variable

---

### ▶️ Running the Application
Once environment variables are set, start the application:

```bash
mvn spring-boot:run
```

The backend will start on the default Spring Boot port (`8080`).

---

### 🗄️ H2 Database Console (Optional)
If enabled, access the H2 console at:

```bash
http://localhost:8080/h2-console
```

Use the following:
* **JDBC URL:** `jdbc:h2:mem:sanctum_db`
* **Username:** `sanctum`
* **Password:** *(empty)*

---

## 🧪 Testing & QA

Sanctum was tested primarily through **manual API testing** during development, with a strong focus on authentication, authorization, and security-sensitive flows.

Testing efforts includedL
* Authentication and session lifecycle testing (login, refresh, rotation, expiration)
* Role and permission enforcement validation (partially verified during development)
* Ownership-based access control scenarios (validated through development-time checks)
* Negative and edge-case testing (invalid tokens, expired sessions, malformed requests)
* Configuration validation across different environment (planned)
* Bug reporting with reproducible steps and root-cause analysis

Manual testing was performed using tools such as **HTTPie** and **Postman**. The architecture and heavy use of interfaces are designed to support future automated testing and test coverage expansion.
