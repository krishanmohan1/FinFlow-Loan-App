# API Gateway Service - FinFlow

The **API Gateway** acts as the singular entry point for the FinFlow ecosystem. It centralizes cross-cutting concerns like security validation, route orchestration, and load balancing.

---

## 🚀 Role in System
*   **Request Routing**: Directs client requests to downstream microservices using the Eureka Service Registry.
*   **Security Enforcement**: Intercepts every request and validates **JWT (JSON Web Token)** payloads against pre-defined roles (USER vs ADMIN).
*   **RBAC Implementation**: Restricts access to sensitive routes (e.g., `/admin/**`) based on user authority.

---

## 🛠️ Stack Breakdown
| Technology | Description |
| :--- | :--- |
| **Spring Cloud Gateway** | High-performance reactive routing engine. |
| **Spring Security** | Stateless authentication layer. |
| **JWT** | Secure identity token parsing. |
| **Eureka Client** | Dynamic discovery of service hostnames. |

---

## 🔑 Security Endpoints (Public Access Allowed)
The Gateway explicitly allows access to authentication endpoints without a token:
*   `POST /auth/login`: Identity verification and token issuance.
*   `POST /auth/register`: New identity creation.
*   `GET /actuator/health`: System health monitoring.

---

## 🛡️ Protected Routes & RBAC
| Path Pattern | Role Required | Downstream Service |
| :--- | :--- | :--- |
| `/admin/**` | `ADMIN` | `admin-service` |
| `/application/**` | `USER` / `ADMIN` | `application-service` |
| `/document/**` | `USER` / `ADMIN` | `document-service` |
| `/auth/users/**` | `ADMIN` | `auth-service` |

---

## ⚙️ Configuration Details
*   **Port**: `9090`
*   **Secret Key Management**: Uses a synchronized cryptographic key for JWT signature verification.
*   **Observability**: Integrated with Spring Boot Actuator for health checks.
