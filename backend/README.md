# TenderApp Backend

Minimal NestJS backend skeleton for TenderApp.

This backend is the API boundary between Android and infrastructure services such as Supabase and weather providers. Android must consume this API contract rather than Supabase internals.

## Current Status

Implemented by `BACKEND-TASK-001`:

- Project folder skeleton.
- NestJS package scripts.
- Root application module.
- Public health endpoint placeholder.

Implemented by `BACKEND-TASK-002`:

- `.env.example` with local development variables.
- TypeScript environment parser and validation.
- Configurable API prefix, port and API version.

Implemented by `BACKEND-TASK-003`:

- Dedicated health module, controller and service.
- Versioned route through the configured `API_PREFIX`.

Implemented by `TASK-013`:

- Removed hardcoded Supabase credentials from versionable backend code.
- Kept Supabase integration behind a future infrastructure adapter.
- Verified local Supabase key rotation after the local spike.

Implemented by `BACKEND-TASK-004`:

- Added Swagger/OpenAPI setup.
- Exposed API documentation under the configured API prefix.
- Documented the health endpoint response schema.

Implemented by `BACKEND-TASK-005`:

- Added Jest/ts-jest configuration.
- Added the first backend unit test for the health service.

Implemented by `BACKEND-TASK-006`:

- Added a backend-only Supabase module and service.
- Validated required Supabase environment variables.
- Rejected legacy JWT service-role keys in favor of rotated `sb_secret_...` keys.
- Kept Supabase access behind the backend adapter boundary.

Implemented by `BACKEND-TASK-007`:

- Added an authentication module.
- Added a reusable bearer-token guard.
- Added Supabase Auth access-token verification behind the backend boundary.
- Added tests for token extraction, guard behavior and auth service behavior.

Implemented by `BACKEND-TASK-008`:

- Added protected `GET /api/v1/me`.
- Added user profile DTO, controller, service and module.
- Enriched authenticated identity with `profiles.display_name` when available.
- Kept profile lookup errors behind a generic backend error.

Implemented by `BACKEND-TASK-009`:

- Documented Supabase RLS assumptions and ownership model in `SEC-001`.
- Linked the RLS assumptions from the Supabase setup and security review artifacts.

Implemented by `BACKEND-TASK-010`:

- Added protected washer CRUD endpoints.
- Added washer DTOs, validation, mapping, controller, service and module.
- Scoped every washer query by authenticated `user_id`.

Implemented by `BACKEND-TASK-011`:

- Added protected laundry load endpoints.
- Added laundry load DTOs, validation, mapping, controller, service and module.
- Scoped every laundry load query by authenticated `user_id`.

Implemented by `BACKEND-TASK-012`:

- Added protected weather current and forecast endpoints.
- Added a weather provider interface and deterministic mock implementation.
- Normalized weather responses behind the backend API boundary.

Implemented by `BACKEND-TASK-013`:

- Added a global API exception filter.
- Normalized backend errors to the documented `{ code, message, traceId }` envelope.
- Added Swagger error response DTO metadata for public and protected endpoints.
- Added unit tests for bad request, unauthorized, not found and server error mapping.

Implemented by `BACKEND-TASK-014`:

- Added protected `POST /api/v1/predictions/drying`.
- Added explicit drying prediction request and response DTOs.
- Added a rule-based drying prediction calculator aligned with the Android baseline heuristic.
- Orchestrated prediction from normalized weather data through the backend `WeatherService`.
- Added tests for validation, calculator behavior, service orchestration and controller delegation.

Implemented by `BACKEND-TASK-015`:

- Added protected `POST /api/v1/notifications/register-device`.
- Added explicit device registration request and response DTOs.
- Added a notification boundary service that respects opt-in state without sending push notifications yet.
- Added tests for device registration validation, service behavior and controller delegation.

Implemented by `BACKEND-PUSH-001`:

- Added Firebase Admin push configuration parsing behind the notifications module boundary.
- Kept push disabled by default through `PUSH_PROVIDER=disabled`.
- Documented Firebase project and credential environment variables without committing secrets.
- Added tests for disabled mode, FCM credential path mode and service-account JSON validation.

Implemented by `BACKEND-PUSH-002`:

- Persisted protected notification device registration through Supabase.
- Stored Android FCM registration tokens in `device_push_registrations`.
- Kept registration scoped to the authenticated Supabase user id.
- Added tests for notification registration persistence and backend error handling.

Implemented by `BACKEND-PUSH-003`:

- Added Firebase Admin SDK as a backend-only dependency.
- Added a Firebase Admin messaging gateway for controlled FCM sends.
- Added protected `POST /api/v1/notifications/test-push` for development smoke testing.
- Kept test push scoped to the authenticated user's latest active Android FCM registration.

Implemented by `BACKEND-TASK-016`:

- Added backend deployment and release readiness checklist in `docs/09-implementation/backend/DEPLOY-001-Backend-Deployment-and-Release-Checklist.md`.
- Documented local, staging and thesis demo readiness checks.
- Documented environment, Supabase security and release evidence expectations.

Implemented by `INTEGRATION-TASK-001`:

- Added public `GET /api/v1/health/supabase`.
- Kept Supabase connectivity verification behind the centralized `SupabaseService`.
- Added Supabase health DTO and controller/service tests.
- Kept health responses free of secrets and raw provider error details.

Implemented by `INTEGRATION-TASK-002`:

- Extracted laundry-load Supabase queries into `LaundryLoadsSupabaseDataSource`.
- Kept laundry-load controller contract unchanged.
- Kept `LaundryLoadsService` focused on validation, orchestration and backend-level errors.
- Added data source tests for user-scoped read/write/update operations.

## Local Setup

Dependencies are declared in `package.json` and locked in `package-lock.json`.

```bash
npm install
npm run start:dev
npm test
```

For local development, copy `.env.example` to `.env` and provide values through your shell or runtime. The backend loads `.env` through `dotenv/config`.

Push notification configuration is disabled by default:

```text
PUSH_PROVIDER=disabled
```

To prepare Firebase Cloud Messaging in a backend runtime, set:

```text
PUSH_PROVIDER=fcm
FIREBASE_PROJECT_ID=your-firebase-project-id
GOOGLE_APPLICATION_CREDENTIALS=/etc/secrets/firebase-service-account.json
```

Use Render secret files or runtime environment variables for Firebase Admin credentials. Do not commit Firebase service-account JSON.

After Android has registered a device token for the signed-in user, a protected push smoke can be executed with:

```text
POST /api/v1/notifications/test-push
Authorization: Bearer <supabase-access-token>
Content-Type: application/json

{
  "title": "TenderApp test",
  "body": "Push channel is ready."
}
```

The endpoint is intended for development/demo validation only. It sends a generic `SYSTEM_TEST` message and must not be used for domain notification orchestration.

Expected health endpoint after dependencies are installed and the server is running:

```text
GET /api/v1/health
GET /api/v1/health/supabase
```

Expected OpenAPI documentation endpoint:

```text
GET /api/v1/docs
```

Backend release readiness checklist:

```text
docs/09-implementation/backend/DEPLOY-001-Backend-Deployment-and-Release-Checklist.md
```

## Scope Notes

- Supabase client exists as an infrastructure adapter, but no domain endpoint uses it yet.
- Authentication boundary protects the current user profile endpoint.
- Washer persistence is connected through the backend Supabase adapter.
- Laundry load persistence is connected through a module-level Supabase data source backed by the centralized adapter.
- Weather endpoints use a mock provider; no external weather provider is connected yet.
- Drying prediction uses a rule-based backend calculator and mock weather data; no prediction persistence is implemented yet.
- Notification device registration persists Android FCM token metadata through the backend; a protected FCM test-send endpoint exists for smoke validation.
- Runtime errors are normalized through a global API error envelope.
- API shape must remain aligned with `docs/09-implementation/backend/API-001-Backend-API-Contract-Draft.md`.
- Supabase secret keys must never be hardcoded or committed. If a privileged key is exposed during a local spike, disable or delete it in Supabase before continuing integration.
