# FinFlow API Documentation and Testing Guide

This document provides a comprehensive guide for testing the FinFlow microservices ecosystem using Postman. It includes endpoint specifications, dummy request data, and expected response structures.

---

## Postman Testing Setup

### Base URL
All requests should be routed through the API Gateway:
`http://localhost:9090`

### Headers
For protected endpoints, include the following header:
`Authorization: Bearer <your_jwt_token>`

---

## 1. Authentication Service (Service Port: 9091)

### User Registration
- **Method**: POST
- **Endpoint**: `/auth/register`
- **Description**: Creates a new user identity.
- **Dummy Data**:
```json
{
  "username": "tester01",
  "password": "password123"
}
```
- **Expected Response**:
```json
{
  "token": "eyJhbG...",
  "username": "tester01",
  "role": "USER",
  "message": "Registration successful"
}
```

### User Login
- **Method**: POST
- **Endpoint**: `/auth/login`
- **Description**: Authenticates user and returns a JWT token.
- **Dummy Data**:
```json
{
  "username": "tester01",
  "password": "password123"
}
```
- **Expected Response**:
```json
{
  "token": "eyJhbG...",
  "username": "tester01",
  "role": "USER",
  "message": "Login successful"
}
```

---

## 2. Application Service (Service Port: 9092)

### Submit Loan Application
- **Method**: POST
- **Endpoint**: `/application/applications` (Access via Gateway /application/ prefix)
- **Description**: Submits a new loan request.
- **Dummy Data**:
```json
{
  "applicantName": "John Doe",
  "loanAmount": 50000.0,
  "loanPurpose": "Home Improvement",
  "applicantEmail": "john.doe@example.com"
}
```
- **Expected Response**:
```json
{
  "id": 1,
  "applicantName": "John Doe",
  "loanAmount": 50000.0,
  "status": "SUBMITTED",
  "createdAt": "2026-03-27T..."
}
```

### List All Applications (Admin Only)
- **Method**: GET
- **Endpoint**: `/application/applications`
- **Expected Response**: Array of loan application objects.

---

## 3. Document Service (Service Port: 9093)

### Upload Document
- **Method**: POST
- **Endpoint**: `/document/documents/upload`
- **Body**: form-data
- **Key**: `file` (File type), `loanId` (Text type, e.g., "1")
- **Expected Response**:
```json
{
  "id": 101,
  "fileName": "id_proof.pdf",
  "loanId": 1,
  "status": "PENDING_VERIFICATION"
}
```

---

## 4. Admin Service (Service Port: 9094)

### Approve/Reject with Financials
- **Method**: POST
- **Endpoint**: `/admin/applications/{id}/decision`
- **Description**: Final administrative verdict on a loan.
- **Dummy Data**:
```json
{
  "decision": "APPROVED",
  "remarks": "Credit score verified.",
  "interestRate": 7.5,
  "tenureMonths": 48,
  "sanctionedAmount": 45000.0
}
```
- **Expected Response**:
```json
{
  "id": 1,
  "status": "APPROVED",
  "sanctionedAmount": 45000.0,
  "tenureMonths": 48,
  "remarks": "Credit score verified."
}
```

---

## 5. Observability (Zipkin)

### Checking Traces
- **URL**: `http://localhost:9411`
- **Steps**:
  1. Perform a Login or Submit Application request.
  2. Open Zipkin UI.
  3. Click "Run Query".
  4. Expand the latest trace to see the waterfall diagram and inter-service latencies.

## 6. Global Health Check
- **URL**: `http://localhost:8761` (Eureka Dashboard)
- **Status**: Ensure all services (AUTH-SERVICE, APPLICATION-SERVICE, etc.) are listed as UP.
