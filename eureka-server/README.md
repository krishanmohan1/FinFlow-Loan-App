# Eureka Server - FinFlow Service Registry

The **Eureka Server** is the heart of the FinFlow microservices ecosystem, providing specialized service registration and discovery capabilities.

---

## 🚀 Role in System
*   **Centralized Registry**: Maintains an up-to-date map of every active service instance.
*   **Dynamic Discovery**: Allows services (like `api-gateway` and `admin-service`) to find each other without hardcoding physical IP addresses or port numbers.
*   **Health Monitoring**: Automatically evicts instances that fail periodic heartbeats, ensuring request traffic is only routed to healthy nodes.

---

## 🛠️ Stack Breakdown
| Technology | Description |
| :--- | :--- |
| **Spring Cloud Netflix Eureka Server** | High-availability service discovery engine. |
| **Spring Boot Actuator** | Performance monitoring and health endpoints. |

---

## ⚙️ Configuration Details
*   **Port**: `8761`
*   **Dashboard URL**: `http://localhost:8761`
*   **Default Zone**: `http://localhost:8761/eureka/`

---

## 📑 Registry Management
Every FinFlow service (Auth, Admin, Application, Document, Gateway) is configured as a **Eureka Client**, periodically sending heartbeats to this registry. This ensures the cluster remains elastic and resilient to single-instance failures.
