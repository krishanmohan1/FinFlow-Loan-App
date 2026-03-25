```

---

## Postman Test Data — Full Admin Service
```
// LOAN MANAGEMENT
GET  http://localhost:9090/admin/loans
GET  http://localhost:9090/admin/loans/1
GET  http://localhost:9090/admin/loans/status/PENDING
GET  http://localhost:9090/admin/loans/user/user1

// FULL DECISION WITH TERMS
POST http://localhost:9090/admin/loans/1/decision
Authorization: Bearer ADMIN_TOKEN
{
  "decision": "APPROVED",
  "remarks": "All documents verified",
  "interestRate": 8.5,
  "tenureMonths": 60,
  "sanctionedAmount": 48000
}

// QUICK APPROVE / REJECT
PUT http://localhost:9090/admin/loans/1/approve?remarks=Documents verified
PUT http://localhost:9090/admin/loans/1/reject?remarks=Insufficient credit score

// MARK UNDER REVIEW
PUT http://localhost:9090/admin/loans/1/review

// DELETE
DELETE http://localhost:9090/admin/loans/1

// DOCUMENT VERIFICATION
GET http://localhost:9090/admin/documents
GET http://localhost:9090/admin/documents/1
GET http://localhost:9090/admin/documents/loan/1
GET http://localhost:9090/admin/documents/user/user1

PUT http://localhost:9090/admin/documents/1/verify
Authorization: Bearer ADMIN_TOKEN
{
  "status": "VERIFIED",
  "remarks": "PAN card is valid and matches application"
}

// USER MANAGEMENT
GET http://localhost:9090/admin/users
GET http://localhost:9090/admin/users/1

PUT http://localhost:9090/admin/users/1
Authorization: Bearer ADMIN_TOKEN
{
  "role": "ADMIN",
  "active": true
}

PUT http://localhost:9090/admin/users/1/deactivate

// REPORTS
GET http://localhost:9090/admin/reports
GET http://localhost:9090/admin/reports/counts