## 🧪 Admin Service — Postman Test Data
```
// View all loans
GET http://localhost:9090/admin/loans
Authorization: Bearer ADMIN_TOKEN

// View loan by ID
GET http://localhost:9090/admin/loans/1
Authorization: Bearer ADMIN_TOKEN

// View PENDING loans
GET http://localhost:9090/admin/loans/status/PENDING
Authorization: Bearer ADMIN_TOKEN

// Approve loan (default remark)
PUT http://localhost:9090/admin/approve/1
Authorization: Bearer ADMIN_TOKEN

// Approve loan (custom remark)
PUT http://localhost:9090/admin/approve/1?remarks=All documents verified and approved
Authorization: Bearer ADMIN_TOKEN

// Reject loan
PUT http://localhost:9090/admin/reject/1?remarks=Insufficient credit score
Authorization: Bearer ADMIN_TOKEN

// Delete loan
DELETE http://localhost:9090/admin/delete/1
Authorization: Bearer ADMIN_TOKEN