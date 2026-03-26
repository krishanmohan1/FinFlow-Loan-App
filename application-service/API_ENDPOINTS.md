# Application Service API Documentation

This document outlines all the endpoints available in the **Application Service** and how to test them using Postman.

## 🔗 Ports Reference
- **API Gateway**: `http://localhost:9090` (Recommended)
- **Direct Service**: `http://localhost:9092`

---

## 🛠️ Testing via API Gateway (RECOMMENDED)

You must include the `Authorization: Bearer <TOKEN>` header obtained from the Auth Service `/auth/login` endpoint.

### 1. Apply for a Loan *(User)*
- **Method**: `POST`
- **URL**: `http://localhost:9090/application/apply`
- **Headers**: 
  - `Authorization`: `Bearer <USER_TOKEN>`
- **Body** (JSON):
  ```json
  {
      "amount": 50000,
      "loanType": "HOME",
      "purpose": "Buying a new house"
  }
  ```
- **Expected Response**: `200 OK` with the newly created LoanApplication object. This also triggers a RabbitMQ event automatically.

### 2. Get All Loans *(Admin/User)*
- **Method**: `GET`
- **URL**: `http://localhost:9090/application/all`
- **Headers**: 
  - `Authorization`: `Bearer <TOKEN>`
- **Expected Response**: Admin sees all loans. Regular user only sees their own loans.

### 3. Get Loan by ID
- **Method**: `GET`
- **URL**: `http://localhost:9090/application/{id}`
- **Headers**: 
  - `Authorization`: `Bearer <TOKEN>`

### 4. Get Loans by Status 
- **Method**: `GET`
- **URL**: `http://localhost:9090/application/status/{status}`
*(e.g., status = PENDING, APPROVED)*
- **Headers**: 
  - `Authorization`: `Bearer <TOKEN>`

---

## ⚠️ Testing Directly on the Service (Without Gateway)

When testing directly via `http://localhost:9092`, the API Gateway does NOT run. You must manually supply the `X-Auth-Username` and `X-Auth-Role` headers because the controller explicitly requests them.

### Example: Apply for a Loan Directly
- **Method**: `POST`
- **URL**: `http://localhost:9092/application/apply`
- **Headers MUST Include**:
  - `X-Auth-Username`: `user1`
  - `X-Auth-Role`: `USER`
- **Body** (JSON):
  ```json
  {
      "amount": 50000,
      "loanType": "HOME",
      "purpose": "Buying a new house"
  }
  ```

### Example: Get All Loans Directly (As Admin)
- **Method**: `GET`
- **URL**: `http://localhost:9092/application/all`
- **Headers MUST Include**:
  - `X-Auth-Username`: `admin1`
  - `X-Auth-Role`: `ADMIN`
