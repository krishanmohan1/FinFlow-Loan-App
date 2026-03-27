# Development Journal: Technical Challenges & Industry-Grade Solutions

The FinFlow ecosystem was developed using advanced microservices best practices. During the development and stabilization phases, several technical challenges were encountered. This document outlines those problems and the professional solutions applied to ensure a production-ready system.

---

##  Project-Wide Challenges

### **1. Oracle DB XE Connectivity & Healthchecks (Docker)**
*   **Problem**: Microservices (Auth, Application, Document) were failing to start because they attempted to connect to the Oracle DB before it was fully healthy. Standard `depends_on` only checks container status, not service health.
*   **Solution**: 
    1.  Implemented a robust **SQL-based healthcheck** in the `oracle-db` container.
    2.  Configured `depends_on` with `condition: service_healthy`.
    3.  Added a strategic `sleep 60` in the application startup commands to allow the Oracle listener to fully initialize.

### **2. RabbitMQ Permissions on Windows (Docker)**
*   **Problem**: The RabbitMQ container was crashing repeatedly on Windows environments due to permission issues with the `.erlang.cookie` file found in the `/var/lib/rabbitmq` directory.
*   **Solution**: 
    1.  Modified the `docker-compose.yml` to specify a root-level startup command.
    2.  Added a `chown -R rabbitmq:rabbitmq` and `chmod 600` instruction directly into the service's startup process to force the correct permissions at runtime.

### **3. Microservices Networking & Hostname Resolution**
*   **Problem**: In the Docker environment, services were unable to resolve `localhost` when trying to reach the Eureka Server, which resulted in failed registration and broken inter-service communication.
*   **Solution**: 
    1.  Migrated all configuration from `localhost` to **internal Docker service names** (`eureka-server:8761`).
    2.  Ensured all services were joined to the same bridge network (`finflow-network`).

---

##  Service-Specific Challenges

### **1. API Gateway: JWT Parsing & RBAC**
*   **Problem**: The gateway was occasionally failing to parse valid JWTs, causing unauthorized access errors even for valid users.
*   **Solution**: 
    1.  Synchronized the **cryptographic signing key** across both the `auth-service` and `api-gateway`.
    2.  Refined the Gateway Filter to extract and inject custom headers (`X-Auth-Username`, `X-Auth-Role`) into the downstream request, allowing subsequent services to bypass redundant token parsing.

### **2. Auth Service: Database Sequence Issues**
*   **Problem**: The user registration process was failing due to identifier generation conflicts because the default JPA sequence did not match the Oracle DB sequence.
*   **Solution**: 
    1.  Standardized the **Entity Model** using a specific `@SequenceGenerator` matching the database schema.
    2.  Increased the `allocationSize` to ensure higher efficiency during concurrent registration requests.

### **3. Admin Service: Feign Client Fault Tolerance**
*   **Problem**: When one service (e.g., Document Service) was transiently unavailable, the entire Admin Service orchestration would crash.
*   **Solution**: 
    1.  Implemented **Spring Cloud OpenFeign** with a decentralized discovery strategy via Eureka.
    2.  Leveraged Jackson mapping to gracefully handle partial data responses if a non-critical downstream service returned a 404.

---

##  Industry Best Practices Applied
*   **Zero-Downtime Design**: Services automatically retry registration with Eureka if the server is not immediately available.
*   **Externalized Configuration**: All environment-sensitive parameters (DB URLs, Message Broker Hosts) are injected via **Docker Environment Variables**, keeping the source code clean and portable.
*   **Standardized Exception Handling**: Every service implements a `GlobalExceptionHandler` returning a uniform JSON error response mapping (`timestamp`, `status`, `error`), ensuring a consistent API experience for consumers.
*   **Asynchronous Decoupling**: Critical paths (Applications) are detached from non-critical paths (Documents) via **RabbitMQ**, reducing system latencies and preventing cascading failures.
