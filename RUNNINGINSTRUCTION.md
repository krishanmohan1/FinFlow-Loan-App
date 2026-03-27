Sure. Here is the complete step-by-step guide to run the entire FinFlow system.

---

## Pre-checks — Before starting anything

Open Task Manager or run these commands to confirm your external services are running:

**Check Oracle XE is running:**
```bash
# In SQL Plus or any Oracle client — just verify connection works
sqlplus capgdb/capgdb@localhost:1521/XE
```

**Check RabbitMQ is running:**
```bash
# Open browser and go to
http://localhost:15672
# Login: guest / guest
# If this page opens, RabbitMQ is running
```

If RabbitMQ is not running, start it:
```bash
# If installed as Windows service
net start RabbitMQ

# Or navigate to RabbitMQ sbin folder and run
rabbitmq-server.bat
```

---

## Start Order — This order is critical

Services must start in this exact order because each one depends on the previous.

```
1. Eureka Server        (everything registers here — must be first)
2. API Gateway          (needs Eureka to be up)
3. Auth Service         (needs Eureka)
4. Application Service  (needs Eureka + RabbitMQ)
5. Document Service     (needs Eureka + RabbitMQ)
6. Admin Service        (needs all above via Feign)
```

---

## Open 6 separate terminal windows

Open 6 CMD or PowerShell windows. Run one command per window.

---

### Terminal 1 — Eureka Server

```bash
cd C:\Users\krish\OneDrive\Desktop\FinFlow\eureka-server
mvn clean install -DskipTests
mvn spring-boot:run
```

Wait until you see:
```
Started EurekaServerApplication
```
Then open `http://localhost:8761` in browser — Eureka dashboard should be visible.

---

### Terminal 2 — API Gateway

```bash
cd C:\Users\krish\OneDrive\Desktop\FinFlow\api-gateway
mvn clean install -DskipTests
mvn spring-boot:run
```

Wait until you see:
```
Started ApiGatewayApplication
```

---

### Terminal 3 — Auth Service

```bash
cd C:\Users\krish\OneDrive\Desktop\FinFlow\auth-service
mvn clean install -DskipTests
mvn spring-boot:run
```

Wait until you see:
```
Auth Service started on port 9091
```

---

### Terminal 4 — Application Service

```bash
cd C:\Users\krish\OneDrive\Desktop\FinFlow\application-service
mvn clean install -DskipTests
mvn spring-boot:run
```

Wait until you see:
```
Application Service Started on port 9092
```

---

### Terminal 5 — Document Service

```bash
cd C:\Users\krish\OneDrive\Desktop\FinFlow\document-service
mvn clean install -DskipTests
mvn spring-boot:run
```

Wait until you see:
```
Document Service Started on port 9093
```

---

### Terminal 6 — Admin Service

```bash
cd C:\Users\krish\OneDrive\Desktop\FinFlow\admin-service
mvn clean install -DskipTests
mvn spring-boot:run
```

Wait until you see:
```
Admin Service started on port 9094
```

---

## Verify everything is registered

Open your browser and go to:
```
http://localhost:8761
```

You should see all 5 services registered like this:

```
Application       AMIs    Availability Zones    Status
ADMIN-SERVICE      n/a           (1)            UP (1)
API-GATEWAY        n/a           (1)            UP (1)
APPLICATION-SERVICE n/a          (1)            UP (1)
AUTH-SERVICE       n/a           (1)            UP (1)
DOCUMENT-SERVICE   n/a           (1)            UP (1)
```

---

## Port Reference

| Service | Port |
|---|---|
| Eureka Server | 8761 |
| API Gateway | 9090 |
| Auth Service | 9091 |
| Application Service | 9092 |
| Document Service | 9093 |
| Admin Service | 9094 |
| RabbitMQ Management | 15672 |
| RabbitMQ AMQP | 5672 |
| Oracle XE | 1521 |

---

## Test the system end to end in Postman

Once all 6 services are running, test in this exact sequence:

### Step 1 — Register a user
```
POST http://localhost:9090/auth/register
Content-Type: application/json

{
  "username": "user1",
  "password": "password123"
}
```

### Step 2 — Register an admin
```
POST http://localhost:9090/auth/register
Content-Type: application/json

{
  "username": "admin1",
  "password": "admin123"
}
```

Then update role in Oracle:
```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'admin1';
COMMIT;
```

### Step 3 — Login as user, save token
```
POST http://localhost:9090/auth/login
Content-Type: application/json

{
  "username": "user1",
  "password": "password123"
}
```
Copy the `token` from response — this is your `USER_TOKEN`.

### Step 4 — Login as admin, save token
```
POST http://localhost:9090/auth/login
Content-Type: application/json

{
  "username": "admin1",
  "password": "admin123"
}
```
Copy the `token` from response — this is your `ADMIN_TOKEN`.

### Step 5 — Apply for a loan
```
POST http://localhost:9090/application/apply
Authorization: Bearer USER_TOKEN
Content-Type: application/json

{
  "username": "user1",
  "amount": 50000,
  "loanType": "HOME",
  "purpose": "Buying a house"
}
```

### Step 6 — Check Document Service received the event
```
GET http://localhost:9090/document/all
Authorization: Bearer USER_TOKEN
```
You should see a document record auto-created by RabbitMQ consumer.

### Step 7 — Admin approves the loan
```
POST http://localhost:9090/admin/loans/1/decision
Authorization: Bearer ADMIN_TOKEN
Content-Type: application/json

{
  "decision": "APPROVED",
  "remarks": "All documents verified",
  "interestRate": 8.5,
  "tenureMonths": 60,
  "sanctionedAmount": 48000
}
```

### Step 8 — Check Document Service again
```
GET http://localhost:9090/document/all
Authorization: Bearer USER_TOKEN
```
You should now see a second record with event type `LOAN_STATUS_UPDATED`.

### Step 9 — Admin generates report
```
GET http://localhost:9090/admin/reports
Authorization: Bearer ADMIN_TOKEN
```

---

## Common problems and fixes

| Problem | Cause | Fix |
|---|---|---|
| Service not showing in Eureka | Started too fast before Eureka was ready | Wait 30 seconds and restart that service |
| 401 on all requests | Wrong or expired token | Login again and copy the new token |
| 403 on admin endpoints | Using USER token instead of ADMIN token | Use ADMIN_TOKEN for `/admin/**` routes |
| RabbitMQ connection refused | RabbitMQ not running | Start RabbitMQ first |
| Oracle connection failed | Oracle XE not running | Start Oracle XE service from Windows Services |
| Feign call failing | Target service not running | Check all 6 terminals are running without errors |
| Document not auto-created | RabbitMQ queue not connected | Check Application Service terminal for queue errors |

---

## Quick restart if something crashes

If any single service crashes, you do not need to restart everything. Just restart that one service in its terminal:

```bash
# Example — if Application Service crashes
cd C:\Users\krish\OneDrive\Desktop\FinFlow\application-service
mvn spring-boot:run
```

The only exception is if **Eureka crashes** — in that case restart all services in order because they will have lost their registration.