# Admin Service API Documentation

This document outlines all the endpoints available in the **Admin Service** and how to test them using Postman.
*Note: The Admin Service utilizes Feign Clients to speak to other services and compile responses.*

## 🔗 Ports Reference
- **API Gateway**: `http://localhost:9090` (Recommended)
- **Direct Service**: `http://localhost:9094`

---

## 🛠️ Testing via API Gateway (RECOMMENDED)

You **MUST** use an Admin token for these routes. Obtain it by logging in as a user who has the `ADMIN` role.

### 1. View All Loan Applications
- **Method**: `GET`
- **URL**: `http://localhost:9090/admin/loans`
- **Headers**: 
  - `Authorization`: `Bearer <ADMIN_TOKEN>`

### 2. Make a Decision on a Loan
- **Method**: `POST`
- **URL**: `http://localhost:9090/admin/loans/{id}/decision`
- **Headers**: 
  - `Authorization`: `Bearer <ADMIN_TOKEN>`
- **Body** (JSON):
  ```json
  {
      "decision": "APPROVED",
      "remarks": "Verified all uploaded documents.",
      "interestRate": 8.5,
      "tenureMonths": 60,
      "sanctionedAmount": 48000
  }
  ```

### 3. Quick Approve/Reject a Loan
- **Method**: `PUT`
- **URL**: `http://localhost:9090/admin/loans/{id}/approve?remarks=Approved+by+admin`
- **Headers**: 
  - `Authorization`: `Bearer <ADMIN_TOKEN>`

### 4. Verify a Document
- **Method**: `PUT`
- **URL**: `http://localhost:9090/admin/documents/{id}/verify`
- **Headers**: 
  - `Authorization`: `Bearer <ADMIN_TOKEN>`
- **Body** (JSON):
  ```json
  {
      "status": "VERIFIED"
  }
  ```

### 5. Generate Full Report 
- **Method**: `GET`
- **URL**: `http://localhost:9090/admin/reports`
- **Headers**: 
  - `Authorization`: `Bearer <ADMIN_TOKEN>`
- **Response**: Aggregated JSON payload containing Loans, Documents, and Users.

### 6. Get Loan Statistics
- **Method**: `GET`
- **URL**: `http://localhost:9090/admin/reports/counts`
- **Headers**: 
  - `Authorization`: `Bearer <ADMIN_TOKEN>`

---

## ⚠️ Testing Directly on the Service (Without Gateway)

When calling `http://localhost:9094` directly, the calls will often fail if they pass down the chain without specific headers, however since the `admin-service` acts as an aggregator that does Feign calls, it actually routes through Eureka.

Testing `admin-service` locally by skipping the gateway is generally discouraged as it relies heavily on Gateway token extraction, but you would normally hit:
- `http://localhost:9094/admin/loans` etc.