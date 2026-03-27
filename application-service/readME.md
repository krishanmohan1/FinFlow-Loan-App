# Application Service - FinFlow Loan Lifecycle

The **Application Service** is the central repository and manager for all loan applications within the FinFlow ecosystem.

---

## 🚀 Role in System
*   **Loan Lifecycle Management**: Handles the creation, retrieval, and status updates of every loan application (e.g., SUBMITTED -> IN_REVIEW -> APPROVED).
*   **Persistent State Management**: Ensures that all loan details, including client information and financial parameters, are securely persisted in the Oracle DB.
*   **Event Generation**: When a loan is submitted, the system generates events pushed into **RabbitMQ** to notify the Document Service for further processing.

---

## 🛠️ Stack Breakdown
| Technology | Description |
| :--- | :--- |
| **Spring Boot 3.x** | Core microservice framework. |
| **Spring Data JPA** | Relational mapping to the `loan_applications` table. |
| **RabbitMQ** | Asynchronous message broker for cross-service events. |
| **Jackson** | Seamless JSON serialization and mapping between DTOs. |
| **Lombok** | Boilerplate reduction for entity and DTO classes. |

---

## 🔑 Primary API Endpoints
| HTTP Method | Action | Path | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/applications` | Submit Loan | Creates a new loan application and triggers a RabbitMQ event. |
| `GET` | `/applications` | List Applications | Retrieves all current loan records from the DB. |
| `PATCH` | `/applications/{id}/status` | Update Status | Used by admins to transition the loan through the workflow. |
| `PATCH` | `/applications/{id}/decision` | Sanction Details | Adds sanction amount and tenure to an approved loan. |

---

## ⚙️ Configuration Details
*   **Port**: `9092`
*   **Database Schema**: `loan_applications` (Oracle DB)
*   **Messaging Configuration**: RabbitMQ `loanQueue` used for asynchronous verification events.