# Document Service - FinFlow Async Content Repository

The **Document Service** handles all asynchronous document management tasks, ensuring that the heavy lifting of processing and storing files does not block the main user experience.

---

## 🚀 Role in System
*   **Asynchronous Verification**: Listens for loan submission events on **RabbitMQ** to automatically trigger document verification workflows.
*   **Document Management**: Handles the upload, storage, and retrieval of client documentation (e.g., ID Proofs, Income Statements).
*   **Decoupled Architecture**: By processing loan documents asynchronously, the service ensures that the Application Service remains highly responsive during submission.

---

## 🛠️ Stack Breakdown
| Technology | Description |
| :--- | :--- |
| **Spring Boot 3.x** | Core microservice framework. |
| **Spring AMQP (RabbitMQ)** | Message consumer for `loanQueue` events. |
| **Spring Data JPA** | Mapping metadata for documentation storage records. |
| **Hibernate** | Object-Relational Mapping (ORM) engine. |

---

## 🔑 Primary API Endpoints
| HTTP Method | Action | Path | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/documents/upload` | Upload File | Receives and stores client documentation files. |
| `GET` | `/documents/download/{id}` | Download File | Securely retrieves stored documentation files. |
| `PATCH` | `/documents/{id}/verify` | Verify Status | (Admin Only) Sets the verification status for a document. |
| `GET` | `/documents/loan/{loanId}` | List by Loan | Fetches all documents associated with a specific loan ID. |

---

## ⚙️ Configuration Details
*   **Port**: `9093`
*   **Database Schema**: Document metadata in Oracle DB.
*   **Storage Configuration**: External volume `finflow-uploads` is mapped in Docker to store the physical files outside the container.
