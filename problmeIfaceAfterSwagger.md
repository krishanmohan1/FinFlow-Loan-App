
---

#  1. WHAT PROBLEMS I FACED (FULL STORY)

My system = **Microservices + Docker + Oracle + RabbitMQ + Eureka + Gateway**

Everything worked yesterday, then after changes (Swagger + config), everything broke.

---

##  Problem 1: RabbitMQ Crash (Permission Error)

### Error:

```
.erlang.cookie: eacces
BOOT FAILED
```

### What it meant:

* RabbitMQ couldn’t read/write its internal cookie file
* Container permissions were broken

### Root Cause:

 Windows + Docker + Alpine image
 File permissions mismatch

---

##  Problem 2: Docker Compose YAML Error

### Error:

```
services.image must be a mapping
```

### Root Cause:

 Wrong indentation in YAML

Example wrong:

```yaml
rabbitmq:
image: rabbitmq   
```

---

##  Problem 3: Docker Pull / Network Issue

### Error:

```
TLS handshake timeout
```

### Root Cause:

 Internet / DNS issue while pulling image

---

##  Problem 4: Spring Boot Crash (HikariCP)

### Error:

```
Failed to bind properties under spring.datasource.hikari
The configuration of the pool is sealed once started
```

---

#  2. DEEP ROOT CAUSE ANALYSIS

This was the **most important issue**

You had:

### In `application.yml`

```yaml
hikari:
  initializationFailTimeout: -1
```

### AND in `docker-compose`

```yaml
SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT: -1
```

---

 What happened internally:

1. Spring Boot start
2. Hikari connection pool initializes 
3. Then environment variables try to override config 
4. But pool is already "sealed"
5.  Application crashes

---

#  3. HOW YOU FIXED EVERYTHING

---

##  Fix 1: RabbitMQ

```yaml
user: root
command: chown + chmod
```

✔ Fixed permission issue
✔ Container started properly

---

##  Fix 2: YAML Structure

✔ Correct indentation
✔ Proper `services:` hierarchy

---

##  Fix 3: Network

✔ Retried / adjusted image
✔ Used working version

---

##  Fix 4 (MAIN FIX): Hikari Conflict

###  Removed from docker-compose:

```yaml
SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT
SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT
```

###  Kept only in application.yml

✔ No duplicate config
✔ App started successfully

---

#  4. WHAT I LEARNED (VERY IMPORTANT)

---

##  1. Docker ≠ Just Run Command

You learned:

* Containers have **file permissions**
* OS differences matter (Windows vs Linux)
* Volumes can break apps

---

##  2. YAML is Strict

* Indentation = structure
* One wrong space = full failure

---

##  3. Spring Boot Config Priority

Order:

1. Environment variables (highest)
2. application.yml
3. defaults

 Mixing them = dangerous

---

##  4. HikariCP Behavior

* Pool initializes early
* After that → config is locked
* Runtime changes not allowed

---

##  5. Microservices Startup Dependency

I handled:

* Oracle readiness
* Eureka registration
* Service startup timing (`sleep 60`)

---

##  6. Debugging Like Engineer

I moved from:
 Guessing

 Reading logs
 Identifying root cause
 Fixing systematically

---

#  5. WHAT LEVEL YOU ARE NOW

Honestly:

 I crossed **beginner level**

I am  now at:

 **Intermediate Backend Engineer (Real-world debugging exposure)**

Because you handled:

* Docker orchestration
* Distributed services
* DB + Messaging + Discovery
* Runtime config conflicts

---

#  6. HOW YOU CAN EXPLAIN THIS IN INTERVIEW

Say this:

> "I faced a multi-service Docker failure where RabbitMQ had permission issues, YAML config broke the compose file, and Spring Boot services failed due to HikariCP configuration conflict between environment variables and application.yml.
>
> I debugged logs, identified duplicate configuration causing runtime binding failure, and resolved it by removing conflicting env variables and fixing container permissions."


---


