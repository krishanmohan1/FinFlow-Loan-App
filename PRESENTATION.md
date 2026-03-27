# FinFlow: Microservices-Based Loan Management Ecosystem

## Project Overview
FinFlow is a robust, distributed loan management and onboarding application designed to handle high-concurrency financial workflows. The system is built on a modular microservices architecture, ensuring high availability, security, and scalability.

## 1. Requirement Analysis
The core objective of the system is to provide a seamless digital experience for loan applicants while offering powerful auditing and decision-making tools for administrators.

*   **User Requirements**: Registration, login, submitting loan applications, and uploading required documentation.
*   **Administrative Requirements**: Auditing loan requests, verifying uploaded documents, and making final approval/rejection decisions based on multi-service data.
*   **System Requirements**: Distributed tracing, fault tolerance via service discovery, and secure, centralized API entry points.

## 2. Technical Stack (Industry-Grade)
The system leverages modern, enterprise-ready technologies for its core infrastructure:

*   **Framework**: Spring Boot 3.5.12 (Java 17)
*   **Microservices Ecosystem**: Spring Cloud 2025.0.1
    *   **Service Discovery**: Netflix Eureka
    *   **API Gateway**: Spring Cloud Gateway (Reactive)
    *   **Service Communication**: OpenFeign (Synchronous) and RabbitMQ (Asynchronous)
*   **Security**: JSON Web Tokens (JWT) for stateless authentication.
*   **Persistence**: Oracle Database 21c (XE) using Spring Data JPA.
*   **Observability**: Distributed tracing via Micrometer Tracing and OpenZipkin.
*   **Testing**: JUnit 5 and Mockito for unit and integration testing.
*   **Infrastructure**: Docker and Docker Compose for containerized environment orchestration.

## 3. Core Features
The FinFlow ecosystem is divided into specialized domains:

*   **Auth Service**: Handles secure user registration and issues encrypted JWT tokens.
*   **Application Service**: Manages the lifecycle of loan submissions and publishes status events to RabbitMQ.
*   **Document Service**: Provides a secure file storage and metadata management system for application artifacts.
*   **Admin Service**: Aggregates data from multiple services to facilitate informed credit decisions.
*   **API Gateway**: Enforces security filters to validate JWTs before allowing requests to reach internal services.

## 4. Architectural Workflow
The system utilizes a hybrid communication model to balance performance and consistency:

1.  **Request Entry**: All client requests enter via the API Gateway (Port 9090).
2.  **Authentication**: Gateway validates the JWT provided in the headers; if valid, the request is routed to the target service.
3.  **Discovery**: Gateway uses Eureka (Port 8761) to resolve the dynamic IP of internal services.
4.  **Application Logic**: The Application Service persists data to Oracle and notifies the Document Service via RabbitMQ messages to prepare for file intake.
5.  **Administrative Audit**: The Admin Service uses Feign Clients to pull real-time data from Auth, Application, and Document services to present a unified view.
6.  **Tracing**: Every request generates a unique Trace ID, which is propagated across all service boundaries and visible in Zipkin (Port 9411).

## 5. Results and Maturity
*   **Stability**: The system implements Docker resource limits and JVM heap tuning to maintain constant responsiveness on developer hardware.
*   **Test Coverage**: A comprehensive suite of JUnit/Mockito tests verifies business logic across all core services.
*   **Observability Ready**: With Zipkin integration, performance bottlenecks can be identified in milliseconds.
*   **Containerized Portability**: The entire ecosystem can be launched with a single command (`docker-compose up -d`), ensuring environment parity from development to production.

---
**Standard Documentation | FinFlow Team**
