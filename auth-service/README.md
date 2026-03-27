# Auth Service - FinFlow Identity Provider

The **Auth Service** is responsible for managing all user identities, issuing security credentials, and providing administrative control over user profiles within the FinFlow ecosystem.

---

## 🚀 Role in System
*   **Identity Provisioning**: Handles new user registration via secure password hashing (BCrypt).
*   **Authentication & Token Generation**: Validates login credentials and issues cryptographically signed **JWT tokens** carrying user role metadata (`USER` or `ADMIN`).
*   **profile Management**: Provides a secure interface for administrators to view, update roles, and deactivate user accounts.

---

## 🛠️ Stack Breakdown
| Technology | Description |
| :--- | :--- |
| **Spring Boot 3.x** | Core microservice framework. |
| **Spring Security** | Authentication orchestration and password encoding. |
| **JWT** | Secure token signature and claims management. |
| **Spring Data JPA** | Relational mapping to Oracle DB. |
| **Hibernate** | Object-Relational Mapping (ORM) engine. |

---

## 🔑 Primary API Endpoints
| HTTP Method | Action | Path | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | User Registration | Creates a new identity with the default `USER` role. |
| `POST` | `/auth/login` | User Authentication | Returns a signed JWT token on valid credentials. |
| `GET` | `/auth/users` | List All Users | (Admin Only) Retrieves all registered profiles. |
| `PATCH` | `/auth/users/{id}` | Update Account | (Admin Only) Modifies account role or active status. |

---

## ⚙️ Configuration Details
*   **Port**: `9091`
*   **Database Schema**: `users` (Oracle DB)
*   **Security Configuration**: Stateless authentication with JWT filtering and BCrypt encoding.
