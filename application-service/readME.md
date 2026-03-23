---

## 🧪 Application Service — Postman Test Data
```
// Apply Loan
POST http://localhost:9090/application/apply
Authorization: Bearer USER_TOKEN
{
  "username": "user1",
  "amount": 50000,
  "loanType": "HOME",
  "purpose": "Buying a house"
}

// Get All
GET http://localhost:9090/application/all
Authorization: Bearer USER_TOKEN

// Get by ID
GET http://localhost:9090/application/1
Authorization: Bearer USER_TOKEN

// Get by Username
GET http://localhost:9090/application/user/user1
Authorization: Bearer USER_TOKEN

// Get by Status
GET http://localhost:9090/application/status/PENDING
Authorization: Bearer ADMIN_TOKEN

// Update Status
PUT http://localhost:9090/application/status/1
Authorization: Bearer ADMIN_TOKEN
{
  "status": "APPROVED",
  "remarks": "Documents verified. Approved."
}

// Delete
DELETE http://localhost:9090/application/1
Authorization: Bearer ADMIN_TOKEN