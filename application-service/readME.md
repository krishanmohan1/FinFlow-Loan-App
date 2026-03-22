

---

# 📄 ✅ APPLICATION SERVICE — README.md 

```md
# 📄 Application Service – FinFlow Loan Management

## 📌 Overview

The **Application Service** is the core business service responsible for handling **loan applications**.

It provides APIs to:
- Apply for a loan
- Fetch loan applications
- Approve / Reject loans (Admin only)
- Delete applications (Admin only)

---

## 🏗️ Architecture

```

Client → API Gateway → Application Service → Oracle DB

```

- All requests pass through **API Gateway**
- JWT is validated at Gateway
- Role-based access is enforced at Gateway

---

## ⚙️ Tech Stack

- Spring Boot 3.5.x
- Spring Data JPA
- Oracle Database (XE)
- Eureka Client
- Lombok

---

## 📁 Project Structure

```

com.finflow.application
│
├── controller        → REST APIs
├── service           → Business logic
├── repository        → DB access
├── entity            → Database model
├── exception         → Global error handling

```

---

## 🗃️ Database Table

### `loan_applications`

| Column   | Type        |
|----------|------------|
| id       | NUMBER (PK)|
| username | VARCHAR2   |
| amount   | NUMBER     |
| status   | VARCHAR2   |

---

## 🔐 Security

Handled at **API Gateway**

| Endpoint | Access |
|--------|--------|
| /application/apply | USER + ADMIN |
| /application/all | USER + ADMIN |
| /application/{id} | USER + ADMIN |
| /application/status/{id} | ADMIN only |
| /application/{id} (DELETE) | ADMIN only |

---

## 📡 API Endpoints

---

### 🔹 1. Apply for Loan

```

POST /application/apply

```

#### Header
```

Authorization: Bearer USER_TOKEN

````

#### Body
```json
{
  "username": "user1",
  "amount": 5000
}
````

#### Response

```json
{
  "id": 1,
  "username": "user1",
  "amount": 5000,
  "status": "PENDING"
}
```

---

### 🔹 2. Get All Applications

```
GET /application/all
```

#### Header

```
Authorization: Bearer TOKEN
```

#### Response

```json
[
  {
    "id": 1,
    "username": "user1",
    "amount": 5000,
    "status": "PENDING"
  }
]
```

---

### 🔹 3. Get Application by ID

```
GET /application/{id}
```

#### Example

```
GET /application/1
```

---

### 🔹 4. Update Loan Status (ADMIN ONLY)

```
PUT /application/status/{id}?status=APPROVED
```

#### Header

```
Authorization: Bearer ADMIN_TOKEN
```

#### Response

```json
{
  "id": 1,
  "username": "user1",
  "amount": 5000,
  "status": "APPROVED"
}
```

---

### 🔹 5. Delete Application (ADMIN ONLY)

```
DELETE /application/{id}
```

#### Response

```
Deleted Successfully
```

---

## 🧪 Testing Flow

---

### 👤 USER FLOW

1. Login → Get USER_TOKEN
2. Apply loan
3. Fetch all loans

---

### 👑 ADMIN FLOW

1. Update role in DB:

```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'user1';
```

2. Login again → Get ADMIN_TOKEN
3. Approve loan
4. Delete loan

---

## ⚠️ Common Issues

| Problem             | Reason                | Fix                      |
| ------------------- | --------------------- | ------------------------ |
| 401 Unauthorized    | Missing token         | Add Authorization header |
| 403 Forbidden       | Role mismatch         | Check JWT role           |
| Table not found     | Oracle issue          | Create table manually    |
| ID generation error | Oracle 11g limitation | Use SEQUENCE             |

---

## 🚀 Run Service

```
mvn spring-boot:run
```

---

## 🌐 Ports

| Service             | Port |
| ------------------- | ---- |
| Application Service | 9092 |
| API Gateway         | 9090 |
| Eureka              | 8761 |

---

## 🧠 Key Learnings

* Business logic should be separate from authentication
* Gateway handles security
* Application service handles domain logic
* Role-based access improves system control
* Oracle requires sequence-based ID generation

---

## 👨‍💻 Author

Krishan Mohan

````

