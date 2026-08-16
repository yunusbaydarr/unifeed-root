# Backend Integration Report

Auth, JWT/RTR, contextual club RBAC, RFC 7807/i18n, feed keyset pagination, social actions, story expiry, transactional club-event enrollment, smart invite SQL, STOMP chat, local media processing and Kafka email event ingestion are integrated under the Spring Boot backend.

Cross-module access uses services/controllers and shared contracts. Club roles remain outside JWT. User-visible error keys exist in both Turkish and English.

## Frontend Contract Completion

The backend now exposes the read and mutation contracts required by the supplied UI mocks: password and Google authentication, profiles, media-aware feed/posts/comments, stories, club/event discovery and enrollment, search, notification persistence plus STOMP delivery, and complete direct-message thread flows. OAuth uses a 60-second one-time exchange code instead of exposing an access token in the callback URL. `docs/asyncapi.yaml` is the maintained realtime contract; runtime OpenAPI remains available at `/v3/api-docs`.

## Frontend Integration

The React client consumes the versioned REST contract through one credentialed Axios client and keeps access tokens in memory. Route guards bootstrap sessions through refresh-cookie rotation; mutation/query state is centralized with TanStack Query. STOMP/SockJS chat and notification subscriptions share the refreshed auth session. The UI is adaptive from 320px mobile layouts through desktop sidebar layouts and follows the design tokens in `DESIGN_SYSTEM.md`.

Club/event administration is intentionally limited to contracts that exist. Invitation submission, club profile/member administration, and event update/delete still require backend endpoints; no non-functional placeholder action is exposed for them.
