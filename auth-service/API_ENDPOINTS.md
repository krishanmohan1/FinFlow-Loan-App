# Auth Service API Documentation

This document outlines all the endpoints available in the **Auth Service** and how to test them using Postman.

## 🔗 Ports Reference
- **API Gateway**: `http://localhost:9090` (Recommended)
- **Direct Service**: `http://localhost:9091`

---

## 🛠️ Testing via API Gateway (RECOMMENDED)

When testing through the API Gateway, the gateway automatically intercepts the `Authorization: Bearer <token>` header, validates the JWT, and forwards it to the service.

### 1. Register User
- **Method**: `POST`
- **URL**: `http://localhost:9090/auth/register`
- **Body** (JSON):
  ```json
  {
      "username": "user1",
      "password": "password123"
  }
  ```
- **Expected Response**: `200 OK` with UserResponse object (id, username, role, active).

### 2. Login
- **Method**: `POST`
- **URL**: `http://localhost:9090/auth/login`
- **Body** (JSON):
  ```json
  {
      "username": "user1",
      "password": "password123"
  }
  ```
- **Expected Response**: `200 OK` with AuthResponse containing a JWT `token`. **Save this token for other requests.**

### 3. Get All Users *(Admin)*
- **Method**: `GET`
- **URL**: `http://localhost:9090/auth/users/all`
- **Headers**:
  - `Authorization`: `Bearer <ADMIN_TOKEN>`
- **Expected Response**: List of all users.

### 4. Update User Role/Status *(Admin)*
- **Method**: `PUT`
- **URL**: `http://localhost:9090/auth/users/{id}`
- **Headers**:
  - `Authorization`: `Bearer <ADMIN_TOKEN>`
- **Body** (JSON):
  ```json
  {
      "role": "ADMIN",
      "active": true
  }
  ```

### 5. Deactivate User *(Admin)*
- **Method**: `PUT`
- **URL**: `http://localhost:9090/auth/users/{id}/deactivate`
- **Headers**:
  - `Authorization`: `Bearer <ADMIN_TOKEN>`

---

## ⚠️ Testing Directly on the Service (Without Gateway)

If you test directly against `http://localhost:9091`, the **API Gateway filter is bypassed**. This means you don't use the `Bearer <token>`. Instead, you must manually inject the headers that the Gateway usually adds.

### Example: Get All Users Directly
- **Method**: `GET`
- **URL**: `http://localhost:9091/auth/users/all`
- **Headers Needed**: None required by the controller for this specific one, but good to understand the pattern.

### Example: Test User Endpoint Directly
- **Method**: `GET`
- **URL**: `http://localhost:9091/auth/user/test`
- No Auth headers needed natively if Spring Security is not blocking it.
