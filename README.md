# FinFlow: Enterprise Microservices Loan Lifecycle Management System

FinFlow is a state-of-the-art, distributed microservices ecosystem designed to handle end-to-end loan onboarding, verification, and approval processes. Built with scalability and security at its core, FinFlow utilizes a modern architectural pattern to ensure high availability and seamless data flow across heterogeneous services.

---

## 🏗️ System Architecture

FinFlow follows a **decentralized microservices architecture** where each service is isolated, domain-specific, and communicates through a combination of synchronous REST APIs and asynchronous message queues.

### **Core Service Ecosystem**
*   **API Gateway (Port 9090)**: The unified entry point. Handles routing, load balancing, and strictly enforces **JWT-based Role-Based Access Control (RBAC)**.
*   **Eureka Server (Port 8761)**: The service registry enabling dynamic service discovery and inter-service health monitoring.
*   **Auth Service (Port 9091)**: Handles identity management, user registration, and cryptographically signs security tokens.
*   **Admin Service (Port 9094)**: Orchestrates entire approval workflows, interacting with Auth, Applications, and Documents.
*   **Application Service (Port 9092)**: Manages the loan application lifecycle and persistent state transitions.
*   **Document Service (Port 9093)**: Manages secure document uploads and processes verification events asynchronously via **RabbitMQ**.

---

## 🛠️ Technology Stack

| Category | Technology |
| :--- | :--- |
| **Framework** | Spring Boot 3.x, Spring Cloud (2025.x), Spring Rest |
| **Security** | Spring Security, JWT (Json Web Token), CORS, CSRF Protection |
| **Database** | Oracle DB XE 21c (Relational Persistence) |
| **Messaging** | RabbitMQ (Message Broker for Async Tasks) |
| **Persistence** | Spring Data JPA (Hibernate), Model Mapping |
| **Observability** | **Zipkin** (Distributed Tracing), Spring Boot Actuator |
| **Testing** | **JUnit 5**, **Mockito** (Isolated Unit Testing) |
| **Documentation** | OpenAPI 3.0 (Swagger UI) |
| **Infrastructure** | Docker, Docker Compose, Multi-stage builds |

---

## 🚀 Key Features & Implementation Details

### **1. Secure Identity & Access (IAM)**
*   Implements **JWT-based Security Filters** at the Gateway level to intercept and validate identities before they reach downstream services.
*   Secrets management ensures sensitive credentials (DB passwords, Signing keys) are handled via environment-specific configurations.

### **2. Asynchronous Processing**
*   Decoupled document verification using **RabbitMQ**. When a loan is submitted, the system triggers verification events that are consumed and processed independently, ensuring high responsiveness.

### **3. Enterprise Service Communication**
*   **OpenFeign**: Used for declarative REST client communication between services (e.g., Admin -> Auth).
*   **Client-Side Load Balancing**: Eureka integrates with Feign to provide smart routing and fault tolerance.

### **4. Observability & Tracing**
*   Integrated with **Zipkin** to visualize request spans across multiple services, enabling rapid bottleneck detection and performance profiling.

### **5. Quality Assurance**
*   **Unit & Integration Tests**: Leverages JUnit and Mockito to ensure code reliability and contract compliance.
*   **Clean Code & Architecture**: Strictly follows Layered Architecture (Controller -> Service -> Repository) and SOLID principles for maintainable code.

---

## 📦 Deployment & Execution

The entire ecosystem is containerized for seamless "one-click" deployment.

### **Prerequisites**
*   Docker & Docker Compose
*   Java 17+ (for local development)
*   Maven

### **Startup Commands**
```bash
# Clone the repository
git clone https://github.com/your-username/FinFlow.git

# Navigate to root and spin up services
# (Note: Oracle DB and RabbitMQ containers will be provisioned automatically)
docker-compose up -d --build
```
*   **Services Dashboard**: [http://localhost:8761](http://localhost:8761)
*   **Swagger API Docs**: [http://localhost:9090/swagger-ui.html](http://localhost:9090/swagger-ui.html) (via Gateway)

---

## 📑 Project Metadata
*   **Developer**: [Your Name/Team]
*   **Sprint Status**: Sprint-1 Implementation Completed
*   **Architecture Pattern**: Microservices / Domain-Driven Design (DDD)
*   **Repository License**: MIT License
