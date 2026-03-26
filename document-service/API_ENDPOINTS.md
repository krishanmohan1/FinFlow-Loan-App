# Document Service API Documentation

This document outlines all the endpoints available in the **Document Service** and how to test them using Postman.

## 🔗 Ports Reference
- **API Gateway**: `http://localhost:9090` (Recommended)
- **Direct Service**: `http://localhost:9093`

---

## 🛠️ Testing via API Gateway (RECOMMENDED)

You must include the `Authorization: Bearer <TOKEN>` header obtained from the Auth Service `/auth/login` endpoint.

### 1. Upload a Document *(User)*
- **Method**: `POST`
- **URL**: `http://localhost:9090/document/upload`
- **Headers**: 
  - `Authorization`: `Bearer <USER_TOKEN>`
- **Body** (form-data):
  - Key `file` (Type File): *Choose a file*
  - Key `loanId` (Type Text): `1`
  - Key `documentType` (Type Text): `ID_PROOF`
- **Expected Response**: `200 OK` with the created Document object.

### 2. Update a Document *(User)*
- **Method**: `PUT`
- **URL**: `http://localhost:9090/document/update/{id}`
- **Headers**: 
  - `Authorization`: `Bearer <USER_TOKEN>`
- **Body** (form-data):
  - Key `file` (Type File): *Choose a new file*

### 3. Get All Documents *(Admin Only)*
- **Method**: `GET`
- **URL**: `http://localhost:9090/document/all`
- **Headers**: 
  - `Authorization`: `Bearer <ADMIN_TOKEN>`

### 4. Get My Documents *(User)*
- **Method**: `GET`
- **URL**: `http://localhost:9090/document/my`
- **Headers**: 
  - `Authorization`: `Bearer <USER_TOKEN>`

### 5. Verify/Reject a Document *(Admin Only)*
- **Method**: `PUT`
- **URL**: `http://localhost:9090/document/verify/{id}`
- **Headers**: 
  - `Authorization`: `Bearer <ADMIN_TOKEN>`
- **Body** (JSON):
  ```json
  {
      "status": "VERIFIED",
      "remarks": "Document looks clear and valid."
  }
  ```

---

## ⚠️ Testing Directly on the Service (Without Gateway)

When hitting `http://localhost:9093` directly, bypass the standard `Bearer <token>` and inject the `X-Auth-*` headers manually.

### Example: Upload a Document Directly
- **Method**: `POST`
- **URL**: `http://localhost:9093/document/upload`
- **Headers MUST Include**:
  - `X-Auth-Username`: `user1`
  - `X-Auth-Role`: `USER`
- **Body** (form-data):
  - Key `file` (Type File)
  - Key `loanId` = `1`
  - Key `documentType` = `SALARY_SLIP`
