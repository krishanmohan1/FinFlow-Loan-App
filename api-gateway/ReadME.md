🔥 1. TEST USERS (CREATE THESE)
👤 USER (normal)

Use register API:

POST http://localhost:9090/auth/register
{
  "username": "user1",
  "password": "123"
}

👉 Default role = USER

👑 ADMIN (manual DB update)

After registering:

UPDATE users SET role = 'ADMIN' WHERE username = 'user1';

👉 OR create separate:

{
  "username": "admin1",
  "password": "123"
}

Then:

UPDATE users SET role = 'ADMIN' WHERE username = 'admin1';
🔥 2. LOGIN REQUESTS
🔹 USER LOGIN
POST http://localhost:9090/auth/login
{
  "username": "user1",
  "password": "123"
}

👉 Save token → USER_TOKEN

🔹 ADMIN LOGIN
POST http://localhost:9090/auth/login
{
  "username": "admin1",
  "password": "123"
}

👉 Save token → ADMIN_TOKEN

🔥 3. FULL TEST MATRIX (VERY IMPORTANT)
❌ WITHOUT TOKEN
GET /auth/user/test
GET /auth/admin/test

👉 Expected:

401 Unauthorized
👤 USER TOKEN

Header:

Authorization: Bearer USER_TOKEN
Endpoint	Expected
/auth/user/test	✅ User Access
/auth/admin/test	❌ 403
👑 ADMIN TOKEN

Header:

Authorization: Bearer ADMIN_TOKEN
Endpoint	Expected
/auth/user/test	✅ User Access
/auth/admin/test	✅ Admin Access
📄 4. API GATEWAY README.md (COPY-PASTE)
# 🚪 API Gateway – FinFlow Microservices

## 📌 Overview

The **API Gateway** acts as the single entry point for all client requests.

It is responsible for:
- Routing requests to services
- JWT validation
- Role-based authorization

---

## 🏗️ Architecture


Client → API Gateway → Auth Service
↓
JWT Validation + Role Check


---

## ⚙️ Tech Stack

- Spring Boot 3.5.x
- Spring Cloud Gateway (Reactive)
- Eureka Client
- JWT (jjwt)

---

## 🔐 Responsibilities

- Validate JWT token
- Extract user role
- Restrict access based on role
- Route requests to services

---

## 📡 Routes


/auth/** → AUTH-SERVICE


---

## 🔑 Security Rules

| Endpoint | Access |
|--------|--------|
| /auth/login | Public |
| /auth/register | Public |
| /auth/user/** | USER + ADMIN |
| /auth/admin/** | ADMIN only |

---

## 🧪 Testing

---

### 🔹 Step 1 — Register User


POST http://localhost:9090/auth/register


```json
{
  "username": "user1",
  "password": "123"
}
🔹 Step 2 — Make Admin (DB)
UPDATE users SET role = 'ADMIN' WHERE username = 'admin1';
🔹 Step 3 — Login
POST http://localhost:9090/auth/login
🔹 Step 4 — Use JWT

Header:

Authorization: Bearer YOUR_TOKEN
🧪 Test Cases
❌ Without Token
GET /auth/user/test
GET /auth/admin/test

Response:

401 Unauthorized
👤 USER Access
Endpoint	Result
/auth/user/test	✅ Allowed
/auth/admin/test	❌ 403
👑 ADMIN Access
Endpoint	Result
/auth/user/test	✅ Allowed
/auth/admin/test	✅ Allowed
⚠️ Common Issues
Problem	Reason	Fix
401	Missing token	Add Authorization header
403	Role mismatch	Check DB role
404	Wrong path	Use /auth/... prefix
JWT not working	Secret mismatch	Use same key
🚀 Run Gateway
mvn spring-boot:run
🌐 Ports
Service	Port
Gateway	9090
Auth Service	9091
Eureka	8761
🧠 Key Learnings
JWT must be validated at Gateway
Services should not handle authentication
Role-based access is critical
Gateway centralizes security
👨‍💻 Author

Krishan Mohan


---

# 🧠 Final Reality Check

You now have:

✔ Authentication  
✔ Gateway security  
✔ Role-based authorization  
✔ Clean architecture  

👉 This is **strong backend foundation**
