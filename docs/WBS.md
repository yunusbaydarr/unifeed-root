# UniFeed — Work Breakdown Structure

## Planlama İlkeleri

- Zorunlu faz sırası değiştirilemez: `infra → db → be_core → be_features → fe → qa → vcs`.
- Yeni entity'ler (junction tablolar hariç) `BaseAuditableEntity` / soft-delete,
  UUID ve audit alanlarıyla ARCHITECT.md §1.1'e uyacaktır.
- Uygulama kodu bu planın kapsamında değildir; her faz başlamadan önce ilgili
  teslim ve bağımlılık onayı aranır.

## Faz 0 — Keşif ve Planlama

| WBS | İş Paketi | Sorumlu | Bağımlılık | Çıktı | Durum |
|---|---|---|---|---|---|
| 0.1 | PRD ve mimari kısıtlarını WBS'e dönüştürme | `00_discovery_planner` | — | `docs/WBS.md`, açık soru kaydı | COMPLETE |
| 0.2 | Açık kapsam kararlarını takip etme | `06_qa_validator` | `docs/DECISIONS.md` | QA blokaj listesi | BLOCKED |

## Faz 1 — Altyapı ve Geliştirme Ortamı

| WBS | İş Paketi | Sorumlu | Bağımlılık | Çıktı | Karmaşıklık |
|---|---|---|---|---|---|
| 1.1 | Java 21/Spring Boot, React/TypeScript çalışma alanı ve Docker Compose | `01_infra_devops` | 0.1 | servis iskeletleri, Docker yapılandırması | M |
| 1.2 | PostgreSQL 16, Redis 7 ve Kafka KRaft yerel ortamı | `01_infra_devops` | 1.1 | ortam yapılandırması | M |
| 1.3 | CI kapıları: console log, kontrat, prod test-domain guard | `01_infra_devops`, hooks | 1.1 | çalıştırılabilir kalite kapıları | M |
| 1.4 | Local `uploads/` dizin stratejisi ve çevresel yapılandırma | `01_infra_devops` | 1.1 | güvenli yol/limit konfigürasyonu | S |

## Faz 2 — Veri Modeli ve Sözleşme Temeli

| WBS | İş Paketi | Entity / API kapsamı | Sorumlu | Bağımlılık | Karmaşıklık |
|---|---|---|---|---|---|
| 2.1 | Ortak persistence temeli | `BaseAuditableEntity`, soft delete, UUID, audit triggerları | `02_db_architect`, `entity-audit-checker.skill` | 1.x | M |
| 2.2 | Kimlik ve yetki modeli | User, global role, refresh token, club membership/role | `02_db_architect`, `be_auth_domain` | 2.1 | L |
| 2.3 | Sosyal grafik ve içerik modeli | Post, PostMedia (1–10), PostLikes, Comments (tek seviye), Follows, Stories (`view_count`) | `02_db_architect`, `be_feed_story`, `be_social_graph` | 2.1 | L |
| 2.4 | Kulüp ve etkinlik modeli | Club, ClubMember, Event, registration/invitation, ClubThread, ClubThreadMessage; status enum/state machine | `02_db_architect`, `be_club_event` | 2.1 | L |
| 2.5 | Mesaj, bildirim ve denetim modeli | Direct Message, Notification (`payload JSONB`), audit/email event kaydı | `02_db_architect`, `be_chat_realtime`, `be_kafka_worker` | 2.1 | M |
| 2.6 | API/olay sözleşmesi tabanı | OpenAPI, AsyncAPI STOMP destinations/payload şemaları | `02_db_architect`, `openapi-spec-generator.skill`, `contract-validator.skill` | 2.2–2.5 | M |

## Faz 3 — Backend Çekirdeği: Auth, RBAC ve Ortak Katmanlar

| WBS | İş Paketi | Endpoint / davranış | Sorumlu | Bağımlılık | Karmaşıklık |
|---|---|---|---|---|---|
| 3.1 | Security ve RFC 7807 çekirdeği | JWT, `ErrorCode`, `ProblemDetail`, global exception advice | `03_be_core`, `rfc7807-error-generator.skill` | 2.1, 2.2 | L |
| 3.2 | Domain doğrulamalı kayıt/giriş | register, OTP doğrulama, Google OAuth2, test hesap bayrağı | `03_be_core`, `be_auth_domain` | 3.1 | L |
| 3.3 | Token yaşam döngüsü | 15 dk access, 7 gün Redis refresh, HttpOnly cookie, rotation, logout/refresh | `03_be_core`, `be_auth_domain` | 3.1 | L |
| 3.4 | Bağlamsal kulüp yetkisi | `ClubPermissionEvaluator`, 60 sn cache opsiyonu, `@PreAuthorize` | `03_be_core` | 2.4, 3.1 | M |
| 3.5 | Ortak i18n ve loglama | MessageSource TR/EN fallback, SLF4J/Logback, PII maskeleme | `03_be_core`, `be_i18n_manager` | 3.1 | M |

## Faz 4 — Backend Özellikleri

| WBS | İş Paketi | Endpoint / davranış | Sorumlu subagent / skill | Bağımlılık | Karmaşıklık |
|---|---|---|---|---|---|
| 4.1 | Ortak medya işleme | MIME magic-byte, resize/WEBP, kategori yolu, static `/uploads/**`, limitler | `be_media_processor` | 1.4, 3.1 | L |
| 4.2 | Feed ve post etkileşimleri | post CRUD, medya, like, comment, save/share/invite, cursor feed | `be_feed_story`, `be_social_graph` | 2.3, 4.1 | L |
| 4.3 | Stories | oluşturma/listeleme, Redis ZSET TTL, scheduled expiry, atomic `view_count` | `be_feed_story`, `redis-ttl-optimizer.skill` | 2.3, 4.1 | M |
| 4.4 | Kulüpler ve etkinlikler | club CRUD/onay, üyelik, event CRUD, poster, transactional auto-enrollment | `be_club_event` | 2.4, 3.4, 4.1 | L |
| 4.5 | Smart Invite ve kulüp içi sohbet | SQL ile davet edilebilir adaylar; thread/message üyelik yetkisi | `be_club_event`, `sql-explain-analyzer.skill` | 2.4, 3.4 | L |
| 4.6 | Birebir chat ve canlı durum | REST geçmişi, STOMP auth, Redis Pub/Sub, typing/presence | `be_chat_realtime` | 2.5, 3.1 | L |
| 4.7 | Bildirimler | DB persist, `/user/queue/notifications`, read durumları | `be_chat_realtime` | 2.5, 3.1 | M |
| 4.8 | Asenkron e-posta | `email-events`, Kafka consumer, Thymeleaf TR/EN şablonları | `be_kafka_worker`, `be_i18n_manager` | 2.5, 3.5 | L |
| 4.9 | Geliştirme verisi | güvenli/temsili başlangıç verileri | `be_data_seeder` | 2.x–4.x | S |

## Faz 5 — Frontend Mimarisi ve Deneyim

| WBS | İş Paketi | Sorumlu | Bağımlılık | Karmaşıklık |
|---|---|---|---|---|---|
| 5.1 | Tasarım tokenları ve uygulama kabuğu | `05_fe_architect`, `fe_atomic_ui` | `docs/DESIGN_SYSTEM.md`, 1.1 | M |
| 5.2 | Atomik, erişilebilir UI | `fe_atomic_ui` | 5.1 | M |
| 5.3 | i18n ve hata deneyimi | `fe_i18n_manager`, `05_fe_architect` | 3.5, 5.1 | M |
| 5.4 | Auth, test hesap uyarısı ve silent refresh | `fe_auth_interceptor`, `05_fe_architect` | 3.2, 3.3 | L |
| 5.5 | WebSocket yönetimi | `fe_socket_manager`, `silent-refresh-simulator.skill` | 4.6, 4.7, 5.4 | L |
| 5.6 | Feed, story, club/event ve chat ekranları | `05_fe_architect` ve ilgili FE subagent'lar | 4.2–4.8, 5.2–5.5 | L |
| 5.7 | Kulüp yetki görünürlüğü | `05_fe_architect`, `fe_atomic_ui` | 3.4, 5.2 | M |

## Faz 6 — Kalite Doğrulama

| WBS | İş Paketi | Sorumlu | Bağımlılık | Çıktı |
|---|---|---|---|---|
| 6.1 | Mimari, entity, RBAC, i18n, RFC 7807, sözleşme ve console-log denetimi | `06_qa_validator` ve kalite skill'leri | 1–5 | `docs/QA_REPORT.md` |
| 6.2 | Açık kararların kapatılmasını doğrulama | `06_qa_validator` | 0.2 | PASS veya BLOCKED |

## Faz 7 — VCS Denetimi

| WBS | İş Paketi | Sorumlu | Bağımlılık | Çıktı |
|---|---|---|---|---|
| 7.1 | Branch/commit standardı ve merge kapısı | `07_git_vcs_auditor` | 6.1–6.2 PASS | VCS denetim raporu |

## Karar Bekleyen Blokajlar

| ID | Konu | Etkilenen WBS | Sorumlu | Durum |
|---|---|---|---|---|
| OQ-01 | Rate limiting ve abuse prevention kapsamı | 3.1, 4.2, 4.6 | `06_qa_validator` | BLOCKED |
| OQ-02 | Bildirim tercihleri / opt-out modeli | 4.7, 4.8, 5.6 | `06_qa_validator` | BLOCKED |
| OQ-03 | Arama kapsamı ve teknolojisi | 2.x, 4.x, 5.6 | `06_qa_validator` | BLOCKED |
| OQ-04 | Rol değişiminde WebSocket oturumu invalidasyonu | 3.4, 4.6, 5.5 | `06_qa_validator` | BLOCKED |
| OQ-05 | Çoklu cihaz refresh-token oturum politikası | 2.2, 3.3, 5.4 | `06_qa_validator` | BLOCKED |
