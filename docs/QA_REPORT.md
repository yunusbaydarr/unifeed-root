# QA Report — Backend Foundation and Features

## Result: PASS

- Java 21 Maven test lifecycle: PASS.
- PostgreSQL 16 migrations V1/V2 applied with `ON_ERROR_STOP`: PASS.
- Backend production Docker image build: PASS.
- Docker Compose runtime with PostgreSQL, Redis and Kafka: PASS.
- OpenAPI 3.1 document at `/v3/api-docs`: PASS (17 paths).
- Academic-domain registration: HTTP 200.
- Rejected non-academic domain: HTTP 403.
- Unauthenticated feed access: HTTP 401.
- Production test-domain guard remains `false`: PASS.
- Forbidden console output scan: PASS.
- Service classes over 300 lines: none.

## Notes

SMTP delivery was not asserted against a real provider; Kafka retry/DLT behavior is configured and the asynchronous path does not block registration. Google OAuth requires valid deployment credentials for provider-level verification.

## Frontend Contract Completion — 2026-08-14

- Java 21 production Docker image build: PASS.
- Java 21 Maven unit tests: PASS (2 tests, 0 failures/errors).
- PostgreSQL 16 migration chain V1/V2/V3 with `ON_ERROR_STOP=1`: PASS.
- AsyncAPI contract added for chat, notifications and STOMP auth refresh.
- Forbidden console output scan: PASS.
- Provider-level Google OAuth and real SMTP delivery still require deployment credentials and therefore remain environment verification items, not code failures.

## Frontend QA — 2026-08-14

- Strict TypeScript typecheck: PASS.
- Vite production build: PASS.
- Vitest UI/auth suite: PASS (5/5).
- Playwright Chromium desktop/mobile suite: PASS (7 passed, 3 viewport-conditional skipped).
- Responsive coverage: desktop navigation, Pixel 7 navigation, mobile composer and message list/thread return flow.
- Contract coverage: refresh/login/me, feed/stories, post creation and duplicated API-prefix regression assertion.
- Forbidden generated artifacts are ignored (`dist`, `test-results`, `playwright-report`).
- Route-level code splitting is enabled; the production build completes without chunk-size warnings.

## Registration Reliability QA — 2026-08-14

- Java 21 Maven test lifecycle: PASS (2/2).
- Flyway schema validation and migration through V4: PASS.
- Live registration response: HTTP 200, under 1 second.
- Transactional outbox publish and Kafka consumption: PASS.
- Mailpit verification-email delivery: PASS.
- OTP verification, access token and HttpOnly refresh cookie: PASS.
- Duplicate registration: HTTP 409, `AUTH_014_EMAIL_ALREADY_REGISTERED`.
- Malformed JSON: HTTP 400, `COMMON_001_VALIDATION_FAILED`.
- Frontend production Docker build and Vitest: PASS (5/5).
- Nginx frontend (`5173:80`) and `/api` reverse proxy: PASS.
- Runtime containers: PostgreSQL healthy, Redis healthy, Kafka healthy, Mailpit healthy, backend up, frontend up.
