# 🔐 Auth Service – FinFlow Microservices

## 📌 Overview

The **Auth Service** is responsible for handling user authentication and authorization in the FinFlow microservices architecture.

It provides:

* User Registration
* User Login
* JWT Token Generation
* Password Encryption (BCrypt)

This service is integrated with:

* **Eureka Server** (Service Discovery)
* **API Gateway** (Routing Layer)

---

## 🏗️ Project Structure

```
src/main/java/com/finflow/auth
│
├── controller        # Handles HTTP requests
│     └── AuthController.java
│
├── service           # Business logic
│     └── AuthService.java
│
├── repository        # Database access layer
│     └── UserRepository.java
│
├── entity            # Database entity
│     └── User.java
│
├── security          # JWT utility
│     └── JwtUtil.java
│
├── config            # Security configuration
│     └── SecurityConfig.java
│
└── AuthServiceApplication.java
```

---

## ⚙️ Tech Stack

* Java 17
* Spring Boot 3.5.x
* Spring Security
* Spring Data JPA
* Oracle Database
* JWT (io.jsonwebtoken)
* Lombok
* Eureka Client

---

## 🚀 How It Works (Flow)

```
Client → API Gateway → Auth Service → Database
                          ↓
                    JWT Token Generated
```

### 🔹 Step-by-step Flow

1. User registers → data saved in DB (password encrypted)
2. User logs in → credentials validated
3. JWT token is generated
4. Token is used for accessing protected APIs

---

## 🔐 Security Features

* ✅ Password encryption using BCrypt
* ✅ JWT-based authentication
* ❌ (Phase 1) No role-based authorization yet
* ❌ (Phase 1) JWT validation still inside service (will move to Gateway later)

---

## 📡 API Endpoints

### 🔹 1. Test Endpoint

**GET**

```
/auth/test
```

**Description:**
Check if service is running (Protected)

---

### 🔹 2. Register User

**POST**

```
/auth/register
```

**Request Body:**

```json
{
  "username": "krish",
  "password": "123"
}
```

**Response:**

```
krish
```

---

### 🔹 3. Login User

**POST**

```
/auth/login
```

**Request Body:**

```json
{
  "username": "krish",
  "password": "123"
}
```

**Response:**

```
JWT_TOKEN
```

---

## 🔑 How to Use JWT

After login, include the token in request headers:

```
Authorization: Bearer YOUR_TOKEN
```

---

## 🧪 Testing the Service

---

### ✅ 1. Register User

```
POST http://localhost:9090/auth/register
```

---

### ✅ 2. Login

```
POST http://localhost:9090/auth/login
```

👉 Copy the token from response

---

### ❌ 3. Access without Token

```
GET http://localhost:9090/auth/test
```

Response:

```
401 Unauthorized
```

---

### ✅ 4. Access with Token

Header:

```
Authorization: Bearer YOUR_TOKEN
```

```
GET http://localhost:9090/auth/test
```

Response:

```
Auth Service Running
```

---

## 🌐 Ports Used

| Service       | Port |
| ------------- | ---- |
| Auth Service  | 9091 |
| API Gateway   | 9090 |
| Eureka Server | 8761 |

---

## 🛠️ Run the Service

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

---

## ⚠️ Common Issues Faced

| Issue            | Reason                  | Fix                       |
| ---------------- | ----------------------- | ------------------------- |
| 401 Unauthorized | Missing JWT             | Add Authorization header  |
| 403 Forbidden    | Authentication not set  | Fixed via SecurityContext |
| ORA-00903        | Reserved keyword `user` | Renamed table to `users`  |
| Port conflict    | Port already in use     | Changed port to 9091      |

---

## 📈 Current Status

* ✅ Registration working
* ✅ Login working
* ✅ JWT generation working
* ✅ Password encryption working
* ✅ API protection working

---

## 🚧 Future Improvements

* Move JWT validation to API Gateway
* Add role-based authorization (ADMIN/USER)
* Implement refresh tokens
* Add logout mechanism
* Add global exception handling

---

## 🧠 Key Learnings

* Version compatibility is critical in Spring Boot + Cloud
* JWT alone is useless without validation
* Security must be centralized (Gateway level)
* Minimal configuration > overengineering

---

## 👨‍💻 Author

**Krishan Mohan**
Backend Developer | Java | Spring Boot | Microservices

---
