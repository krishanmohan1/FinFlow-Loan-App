# Admin Service - FinFlow Orchestrator

The **Admin Service** is a centralized orchestration layer designed to coordinate high-level administrative tasks across the FinFlow ecosystem.

---

## 🚀 Role in System
*   **System Orchestration**: Coordinates approval workflows by interacting with other distributed services (Auth, Application, Document) via declarative REST clients.
*   **Approval & Decision Logic**: Provides the final interface for administrators to approve, reject, or request more information on loan applications.
*   **Unified Admin Control**: Instead of accessing multiple service endpoints, administrators use this service as a single point of truth for loan status transitions and user profile modification.

---

## 🛠️ Stack Breakdown
| Technology | Description |
| :--- | :--- |
| **Spring Cloud OpenFeign** | Declarative REST client for inter-service communication. |
| **Spring Boot 3.x** | Core microservice framework. |
| **Jackson** | Seamless JSON serialization and mapping between DTOs. |
| **Eureka Client** | Dynamic discovery of downstream services. |

---

## 🔑 Primary API Endpoints
| HTTP Method | Action | Path | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/admin/applications` | List All Loans | Fetches all pending/submitted applications from the Application service. |
| `PATCH` | `/admin/applications/{id}/status` | Update Loan | Triggers status transitions (e.g., IN_REVIEW, APPROVED). |
| `POST` | `/admin/applications/{id}/decision` | Final Verdict | Sets sanctioned amount, tenure, and final approval/rejection. |
| `PATCH` | `/admin/users/{id}` | Update Account | Accesses the Auth service to modify user roles or status. |

---

## ⚙️ Configuration Details
*   **Port**: `9094`
*   **Clients**: Feign Clients configured for `auth-service`, `application-service`, and `document-service`.
*   **Security Configuration**: Strictly requires `ADMIN` role validated at the API Gateway level.
