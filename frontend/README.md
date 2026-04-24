# FinFlow Frontend

Angular frontend for the FinFlow microservices backend.

## Backend Requirement

Start the backend from the project root:

```powershell
cd D:\FinFlow
docker compose up -d --build
```

The Angular dev server proxies API calls to the API Gateway:

```text
/api -> http://localhost:9090
```

Do not call individual service ports from Angular during normal usage. The proxy avoids browser CORS/preflight issues while still routing everything through the gateway.

## Run Frontend

```powershell
cd D:\FinFlow\frontend
npm install
npm start
```

Open:

```text
http://localhost:4200
```

## Default Accounts

Admin account is seeded by the backend:

```text
username: admin
password: admin123
```

Users can self-register from the frontend.

## Environment

API base URL lives in:

```text
src/environments/environment.ts
```

Development uses `apiBaseUrl: '/api'` and [proxy.conf.json](proxy.conf.json). During local development, each backend service is proxied directly to avoid gateway/Eureka 503 issues while you are building the UI:

```text
/api/auth        -> http://127.0.0.1:9091/auth
/api/application -> http://127.0.0.1:9092/application
/api/document    -> http://127.0.0.1:9093/document
/api/admin       -> http://127.0.0.1:9094/admin
```

The frontend sends `Authorization`, `X-Auth-Username`, and `X-Auth-Role` headers after login so direct service calls behave like gateway-routed calls. For production deployment, point `environment.prod.ts` at the deployed gateway URL or serve the frontend behind the same reverse proxy.

## Feature Coverage

- Login/register with JWT session storage
- Automatic `Authorization: Bearer <token>` header
- User loan application flow
- User document upload flow with multipart `FormData`
- User loan/document tracking
- Admin loan decisions
- Admin document verification
- Admin user role/status updates
- Admin reports and live backend data

## Backend Flow Notes

- Loan submission calls `POST /application/apply`
- Document upload calls `POST /document/upload`
- Admin decisions call `POST /admin/loans/{id}/decision`
- Admin document verification calls `PUT /admin/documents/{id}/verify`
- The frontend relies on the gateway injecting `X-Auth-Username` and `X-Auth-Role` into downstream backend calls.
