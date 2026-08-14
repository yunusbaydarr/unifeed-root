# Backend Integration Report

Auth, JWT/RTR, contextual club RBAC, RFC 7807/i18n, feed keyset pagination, social actions, story expiry, transactional club-event enrollment, smart invite SQL, STOMP chat, local media processing and Kafka email event ingestion are integrated under the Spring Boot backend.

Cross-module access uses services/controllers and shared contracts. Club roles remain outside JWT. User-visible error keys exist in both Turkish and English.
