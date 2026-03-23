---

## 🧪 Document Service — Postman Test Data
```
// Get all documents (auto-created by RabbitMQ consumer)
GET http://localhost:9090/document/all
Authorization: Bearer ADMIN_TOKEN

// Get document by ID
GET http://localhost:9090/document/1
Authorization: Bearer ADMIN_TOKEN

// Get documents by username
GET http://localhost:9090/document/user/user1
Authorization: Bearer USER_TOKEN

// Get documents linked to a loan
GET http://localhost:9090/document/loan/1
Authorization: Bearer USER_TOKEN

// Get documents by event type
GET http://localhost:9090/document/event/LOAN_APPLIED
Authorization: Bearer ADMIN_TOKEN

GET http://localhost:9090/document/event/LOAN_STATUS_UPDATED
Authorization: Bearer ADMIN_TOKEN