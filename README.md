# 🏦 FinFlow — Loan Management Microservices System

> A production-grade microservices backend for managing loan applications,
> built with Spring Boot, Spring Cloud, RabbitMQ, and Oracle DB.

---

## 📌 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Services](#services)
- [Tech Stack](#tech-stack)
- [Workflow](#workflow)
- [API Endpoints](#api-endpoints)
- [Security Model](#security-model)
- [RabbitMQ Event Flow](#rabbitmq-event-flow)
- [Logging](#logging)
- [How to Run](#how-to-run)
- [Future Enhancements](#future-enhancements)

---

## 📖 Overview

FinFlow is a microservices-based Loan Management System where:

- Users can register, login, and apply for loans
- Admins can approve or reject loan applications
- Every loan event is automatically recorded via RabbitMQ
- All services are secured by JWT at the API Gateway level
- Each service is independently deployable and registered with Eureka

---

## 🏗️ Architecture
```
Client (Postman / Browser)
         │
         ▼  JWT in Header
┌─────────────────────────┐
│      API Gateway        │  port 9090
│  JWT Validate · Route   │
│  Role Check · Headers   │
└─────┬──────┬──────┬─────┘
      │      │      │
      ▼      ▼      ▼
  Auth   Application  Admin
  9091    9092        9094
   │       │    ◄──Feign──┘
   │       │ (publish)
   ▼       ▼
Oracle   RabbitMQ (loanQueue)
           │ (consume)
           ▼
      Document Service
           9093
           │
           ▼
         Oracle
```

---

## ⚙️ Services

| Service              | Port | Responsibility                                      |
|----------------------|------|-----------------------------------------------------|
| Eureka Server        | 8761 | Service discovery and registry                      |
| API Gateway          | 9090 | JWT validation, routing, role-based access control  |
| Auth Service         | 9091 | Register, Login, JWT token generation               |
| Application Service  | 9092 | Loan apply, view, update status, RabbitMQ producer  |
| Document Service     | 9093 | RabbitMQ consumer, auto-saves loan events to DB     |
| Admin Service        | 9094 | Approve/reject loans via Feign to Application       |

---

## 🔧 Tech Stack

| Layer              | Technology                        |
|--------------------|-----------------------------------|
| Language           | Java 17                           |
| Framework          | Spring Boot 3.5.12                |
| Cloud              | Spring Cloud 2025.0.1             |
| API Gateway        | Spring Cloud Gateway (Reactive)   |
| Service Discovery  | Netflix Eureka                    |
| Inter-service      | OpenFeign                         |
| Messaging          | RabbitMQ (AMQP)                   |
| Database           | Oracle XE                         |
| ORM                | Spring Data JPA + Hibernate       |
| Auth               | JWT (jjwt 0.11.5) + BCrypt        |
| Logging            | SLF4J + Logback (rolling file)    |
| Build              | Maven                             |

---

## 🔄 Workflow

### 👤 User Flow
```
1. POST /auth/register        → Account created, JWT returned
2. POST /auth/login           → JWT token returned
3. POST /application/apply    → Loan submitted (status = PENDING)
                                 ↓
                          RabbitMQ publishes LOAN_APPLIED
                                 ↓
                     Document Service saves event record
4. GET  /application/user/{username}  → View my loans
5. GET  /application/{id}             → View specific loan
```

### 👑 Admin Flow
```
1. POST /auth/login (ADMIN)   → ADMIN JWT token returned
2. GET  /admin/loans          → View all loan applications
3. GET  /admin/loans/status/PENDING  → Filter pending loans
4. PUT  /admin/approve/{id}?remarks=Verified
                                 ↓
                     Feign → Application Service updates status
                                 ↓
                     RabbitMQ publishes LOAN_STATUS_UPDATED
                                 ↓
                     Document Service saves update record
5. PUT  /admin/reject/{id}?remarks=Low credit score
6. DELETE /admin/delete/{id}  → Loan removed
```

---

## 📡 API Endpoints

### 🔐 Auth Service — `/auth`

| Method | Endpoint          | Access  | Description             |
|--------|-------------------|---------|-------------------------|
| POST   | /auth/register    | Public  | Register new user       |
| POST   | /auth/login       | Public  | Login and get JWT       |
| GET    | /auth/test        | Any     | Health check            |
| GET    | /auth/user/test   | USER+   | User access test        |
| GET    | /auth/admin/test  | ADMIN   | Admin access test       |

**Register body:**
```json
{ "username": "user1", "password": "password123" }
```
**Login response:**
```json
{
  "token": "eyJhbGci...",
  "username": "user1",
  "role": "USER",
  "message": "Login successful"
}
```

---

### 📄 Application Service — `/application`

| Method | Endpoint                     | Access     | Description              |
|--------|------------------------------|------------|--------------------------|
| POST   | /application/apply           | USER+ADMIN | Submit loan application  |
| GET    | /application/all             | USER+ADMIN | Get all applications     |
| GET    | /application/{id}            | USER+ADMIN | Get by ID                |
| GET    | /application/user/{username} | USER+ADMIN | Get loans by user        |
| GET    | /application/status/{status} | USER+ADMIN | Filter by status         |
| PUT    | /application/status/{id}     | ADMIN      | Update status + remarks  |
| DELETE | /application/{id}            | ADMIN      | Delete application       |

**Apply body:**
```json
{
  "username": "user1",
  "amount": 50000,
  "loanType": "HOME",
  "purpose": "Buying a new house"
}
```

---

### 👑 Admin Service — `/admin`

| Method | Endpoint                      | Access | Description             |
|--------|-------------------------------|--------|-------------------------|
| GET    | /admin/loans                  | ADMIN  | View all loans          |
| GET    | /admin/loans/{id}             | ADMIN  | View loan by ID         |
| GET    | /admin/loans/status/{status}  | ADMIN  | Filter by status        |
| PUT    | /admin/approve/{id}           | ADMIN  | Approve with remarks    |
| PUT    | /admin/reject/{id}            | ADMIN  | Reject with remarks     |
| DELETE | /admin/delete/{id}            | ADMIN  | Delete application      |

---

### 📁 Document Service — `/document`

| Method | Endpoint                       | Access     | Description             |
|--------|--------------------------------|------------|-------------------------|
| GET    | /document/all                  | USER+ADMIN | All event records       |
| GET    | /document/{id}                 | USER+ADMIN | Record by ID            |
| GET    | /document/user/{username}      | USER+ADMIN | Events by username      |
| GET    | /document/loan/{loanId}        | USER+ADMIN | Events by loan ID       |
| GET    | /document/event/{eventType}    | USER+ADMIN | Filter by event type    |

---

## 🔐 Security Model
```
Gateway enforces:

Public (no token needed):
  POST /auth/register
  POST /auth/login

USER or ADMIN:
  /application/**
  /document/**
  /auth/user/**

ADMIN only:
  /admin/**
  /auth/admin/**
```

Gateway injects downstream headers on every verified request:
- `X-Auth-Username` → username from token
- `X-Auth-Role`     → role from token

---

## 🐇 RabbitMQ Event Flow
```
Queue name: loanQueue (durable)

Events published by Application Service:
  LOAN_APPLIED         → on new loan submission
  LOAN_STATUS_UPDATED  → on approve / reject

Message format:
  "LOAN_APPLIED | id=5 | user=user1 | amount=50000.00 | type=HOME | purpose=Buying a house"
  "LOAN_STATUS_UPDATED | id=5 | user=user1 | status=APPROVED | remarks=All documents verified"

Document Service consumes → parses → saves to Oracle
```

---

## 📋 Logging

Every service uses SLF4J + Logback:

- Console output at runtime
- Rolling file: `logs/<service-name>.log`
- Daily rollover, 7 days retained

| Service              | Log File                          |
|----------------------|-----------------------------------|
| Eureka Server        | logs/eureka-server.log            |
| API Gateway          | logs/api-gateway.log              |
| Auth Service         | logs/auth-service.log             |
| Application Service  | logs/application-service.log      |
| Document Service     | logs/document-service.log         |
| Admin Service        | logs/admin-service.log            |

---

## 🚀 How to Run

### Prerequisites

- Java 17
- Maven
- Oracle XE running on port 1521
- RabbitMQ running on port 5672

### Start Order (important)
```bash
# 1. Eureka Server (must start first)
cd eureka-server && mvn spring-boot:run

# 2. API Gateway
cd api-gateway && mvn spring-boot:run

# 3. Auth Service
cd auth-service && mvn spring-boot:run

# 4. Application Service
cd application-service && mvn spring-boot:run

# 5. Document Service
cd document-service && mvn spring-boot:run

# 6. Admin Service
cd admin-service && mvn spring-boot:run
```

### Verify all services registered

Open: http://localhost:8761

---

## 🔮 Future Enhancements

| Feature                        | Description                                              |
|--------------------------------|----------------------------------------------------------|
| Refresh Tokens                 | JWT refresh without re-login                             |
| Admin Audit Log Service        | Track every admin action with timestamp in its own DB    |
| Notification Service           | Email/SMS alerts on loan status change via RabbitMQ      |
| Config Server                  | Centralized configuration for all services               |
| Circuit Breaker                | Resilience4j fallback for Feign failures                 |
| Rate Limiting                  | Throttle requests at API Gateway                         |
| Docker + Docker Compose        | Containerize all services                                |
| Kubernetes Deployment          | Orchestrate with K8s + Helm charts                       |
| API Documentation              | Swagger / OpenAPI 3 for all services                     |
| Frontend UI                    | React dashboard for users and admins                     |

---

## 👨‍💻 Author

**Krishan Mohan**
Backend Developer · Java · Spring Boot · Microservices

---

## 📌 Ports Quick Reference

| Service              | Port |
|----------------------|------|
| Eureka Server        | 8761 |
| API Gateway          | 9090 |
| Auth Service         | 9091 |
| Application Service  | 9092 |
| Document Service     | 9093 |
| Admin Service        | 9094 |
| RabbitMQ             | 5672 |
| Oracle XE            | 1521 |
