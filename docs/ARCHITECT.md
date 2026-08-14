# 🏛️ UNIFEED - ARCHITECT.md
## Mimari Standartlar, Tasarım Kararları ve Uygulama Kısıtları

> Bu belge PROMPT.md'nin (PRD) **nasıl** kod haline getirileceğini tanımlar.
> PROMPT.md "ne yapılacağını", bu belge "nasıl yapılacağını ve hangi kalıpların
> zorunlu olduğunu" tanımlar. Ajanlar bir tasarım kararı verirken önce bu
> belgeye bakacak; burada tanımlı değilse PROMPT.md'ye, o da yoksa Bölüm 12'deki
> "Belirsizlik Protokolü"ne uyacaktır.

---

## 1. VERİ MODELLEME STANDARTLARI (Tablo AI tarafından tasarlanacak, ama şu kurallara zorunlu uyulacak)

Veritabanı şeması artık ajanlar tarafından üretileceği için, üretilecek her
tabloya **uygulanması zorunlu** olan çerçeve kuralları:

### 1.1. Zorunlu Audit Alanları
Her entity (junction table'lar hariç) şu alanlara sahip olacaktır:
```

id (UUID, PK)
created_at (TIMESTAMP, NOT NULL, default now())
updated_at (TIMESTAMP, NOT NULL, on update trigger)
deleted_at (TIMESTAMP, NULLABLE)  --> Soft delete için

```
- `deleted_at IS NOT NULL` olan kayıtlar hiçbir `SELECT` sorgusunda
  görünmeyecektir. Bu, JPA `@Where(clause = "deleted_at IS NULL")` veya
  Hibernate `@SQLRestriction` ile enforce edilecek — kod tarafında manuel
  `WHERE deleted_at IS NULL` yazmaya güvenilmeyecek.
- `SoftDeletable` interface'i (PROMPT.md madde 3.1'de bahsi geçen) bu üç alanı
  garanti eden bir base entity/mapped superclass olarak somutlaştırılacaktır
  (`BaseAuditableEntity`).
- Hard delete sadece GDPR/KVKK "hesabımı sil" akışında, ayrı ve loglanan bir
  admin operasyonu olarak var olacaktır.

### 1.2. Durum (Status) Alanları Enum + State Machine Olacak
`status` alanı geçen her entity (Club, Event, User) için:
- Serbest string/boolean DEĞİL, açık bir Enum tanımlanacak.
- Örnek zorunlu enum: `ClubStatus { PENDING_APPROVAL, ACTIVE, SUSPENDED, CLOSED }`
- Geçişler (transition) bir service metodunda merkezi kontrol edilecek
  (örn. `ClubStatusTransitionService`); controller/repository katmanından
  doğrudan status güncellemesi YAPILMAYACAK.
- Geçersiz geçişler (örn. `CLOSED` → `ACTIVE`) `BusinessException` fırlatacak.

### 1.3. Sosyal Etkileşim Tabloları — Kapsam Kararı Zorunlu
PRD'de özellik olarak var olup şemada karşılığı belirsiz olan aşağıdaki
tablolar için ajan **kapsam varsayımı yapmayacak**, aşağıdaki varsayılan
kurallarla üretecek:
- `PostLikes(post_id, user_id, created_at)` — unique constraint `[post_id, user_id]`.
- `Comments(id, post_id, user_id, parent_comment_id NULLABLE, content, ...)`
  — `parent_comment_id` ile tek seviye yanıt (reply) desteklenecek, sınırsız
  nested thread YAPILMAYACAK (v1 kapsamı).
- `Follows(follower_id, following_id, created_at)` — unique constraint
  `[follower_id, following_id]`, self-follow DB seviyesinde `CHECK` ile engellenecek.
- `Notifications(id, recipient_id, type, payload JSONB, read_at NULLABLE, created_at)`.
- `PostMedia(id, post_id, media_path, order_index, media_type)` — Posts'un
  `media_path` tekil alanı yerine bire-çok ilişki; bir post 1-10 arası medya
  taşıyabilecek (limit config'den okunacak).

### 1.4. Kulüp İçi Grup Sohbeti — Ayrı Model
`CLUB_MEMBER` rolünün "kulüp içi özel tartışmalara katılma" yetkisi, mevcut
1-1 `Messages(sender_id, receiver_id)` modeliyle KARŞILANAMAZ. Bunun için:
- `ClubThreads(id, club_id, title)` ve `ClubThreadMessages(id, thread_id,
  sender_id, content, created_at)` ayrı bir domain olarak modellenecek.
- Bu, `be_chat_realtime.subagent.md` kapsamına DEĞİL, `be_club_event.subagent.md`
  kapsamına yazılacak (çünkü yetkilendirme kulüp üyeliğine bağlı).

### 1.5. Story Görüntüleyenler (StoryViews)
PRD'de "kim gördü" özelliği açıkça istenmiyor. Ajan bunu **varsayılan olarak
kapsam dışı bırakacak**; `Stories` tablosuna sadece bir `view_count` (INTEGER,
atomic increment) alanı ekleyecek, ayrı bir `StoryViews` tablosu KURMAYACAK.
(Bu bilinçli bir v1 kapsam kararıdır, sonradan genişletilebilir.)

---

## 2. BAĞLAMSAL (CONTEXTUAL) RBAC — JWT'NİN YETMEDİĞİ YER

**Kritik mimari uyarı:** `CLUB_ADMIN` / `CLUB_MEMBER` rolleri global değil,
kulüp bazlıdır. JWT içine `role: CLUB_ADMIN` gibi global bir claim YAZILMAYACAK
— bir kullanıcı 3 kulüpte üye, 1 kulüpte admin olabilir; JWT bunu ifade edemez.

### 2.1. Zorunlu Desen
- JWT sadece **global** rolü taşır: `SYSTEM_ADMIN` veya `STUDENT`.
- Kulüp bazlı yetki her istekte **veritabanından** çözülür:
  ```java
  @PreAuthorize("@clubPermissionEvaluator.isClubAdmin(#clubId, authentication)")
  public void createEvent(UUID clubId, ...)
  ```
- `ClubPermissionEvaluator`, `ClubMembers` tablosuna `(club_id, user_id)`
  bileşik indeksli sorgu atarak rolü anlık okur. Bu sorgu Redis'te
  `club-role:{userId}:{clubId}` anahtarıyla kısa TTL (60sn) cache'lenebilir —
  ama JWT'ye asla gömülmez (rol değişince JWT invalidasyonu gerektirmemesi için).
- Frontend, "Etkinlik Oluştur" gibi butonları JWT'den değil, kulüp detay
  API yanıtındaki `currentUserRole: "CLUB_ADMIN"` alanından render edecek.

### 2.2. Neden Bu Önemli
JWT'ye rol gömülürse: (a) kullanıcı kulüpten atıldığında token 15dk boyunca
geçerli kalmaya devam eder — yetki sızıntısı; (b) çoklu kulüp üyeliği JWT
boyutunu şişirir. Bu yüzden bu kural **negotiable değildir**.

---

## 3. TEST MODU GÜVENLİK KORKULUĞU (Production Guard)

`app.security.allow-test-domains: true` flag'inin production'a sızması kritik
bir güvenlik açığıdır (herkes @gmail.com ile üye olabilir hale gelir).
Bu yüzden tek bir config flag'ine güvenilmeyecek, **çift katman** korunacak:

1. **Compile-time/Profile guard:** Test domain kabul mantığı
   `@Profile("!prod")` işaretli ayrı bir `TestDomainAuthStrategy` bean'inde
   izole edilecek. `prod` profili aktifken bu bean hiç yüklenmeyecek — flag
   `true` olsa bile devre dışı kalacak.
2. **CI/CD Gate:** `hooks/pre-deploy-prod-guard.sh` adında yeni bir hook,
   prod'a deploy edilecek `application-prod.yml` içinde
   `allow-test-domains: true` string'i geçiyorsa build'i **FAIL** edecek.
   Bu hook Bölüm 9'da (güncellenmiş ajan ağacı) eklenmiştir.
3. Test hesapları (`is_test_account: true`) prod veritabanında hiçbir zaman
   normal kullanıcı sayımlarına (analytics, kulüp üye sayısı vb.) dahil
   edilmeyecek — sorgularda `WHERE is_test_account = false` filtre standardı
   olacak.

---

## 4. WEBSOCKET SILENT REFRESH — SOMUT MEKANİZMA

PRD'deki "bağlantı hiç kesilmeyecek" iddiası, somut bir teknik mekanizma
olmadan uygulanamaz. Zorunlu tasarım:

### 4.1. Access Token'ı Aktif Bağlantıya Nasıl Enjekte Ederiz?
STOMP bağlantısı sadece `CONNECT` frame'inde authenticate edilir; SockJS
canlı soketine "token güncelle" diye native bir yöntem yoktur. Bu yüzden:

- **Periyodik Re-Auth Frame Deseni** kullanılacak: Frontend, access token
  süresinin dolmasına ~1 dakika kala (client-side timer, JWT `exp` claim'i
  okunarak), STOMP `SEND` ile özel bir `/app/auth/reauth` destination'ına
  yeni access token'ı gönderir. Backend `ChannelInterceptor` bu frame'i
  yakalayıp o session'ın `Principal`'ını yeni token bilgisiyle günceller.
- Eğer refresh (frontend interceptor üzerinden `/api/v1/auth/refresh`)
  başarısız olursa (refresh token da süresi dolmuş/geçersiz), WebSocket
  bağlantısı bilinçli olarak kapatılır ve kullanıcı login ekranına yönlendirilir
  — "sonsuza kadar açık kalan soket" bir güvenlik açığı olur, bu YAPILMAYACAK.
- `fe_socket_manager.subagent.md` bu deseni uygulamaktan sorumlu olacak
  şekilde güncellenmiştir (Bölüm 9).

### 4.2. Reconnect Senaryosu
Ağ kopması durumunda (STOMP `disconnect` event), client exponential backoff
(1s, 2s, 4s, max 30s) ile yeniden bağlanmayı dener; her denemede güncel
access token kullanılır (refresh gerekiyorsa önce refresh tetiklenir).

---

## 5. MEDYA İŞLEME STANDARTLARI

PRD madde 4'te tanımlanan kurallar somutlaştırılıyor:

- Yükleme akışı: `MultipartFile` → MIME doğrulama (magic-byte kontrolü,
  sadece uzantıya güvenilmeyecek) → `libvips`/`Thumbnailator` ile resize →
  `UUID.randomUUID() + .webp` adıyla diske yazma.
- Dosya boyutu limitleri config'den okunacak: avatar ≤ 2MB, post medyası
  ≤ 8MB, kapak/afiş ≤ 5MB. Limit aşımı `413 Payload Too Large` + RFC 7807.
- Bu mantık `MediaStorageService` içinde tek sorumlu olarak izole edilecek
  (SRP), her feature'ın kendi resize kodu YAZMASI YASAK.
- Bu iş için ayrı bir subagent gerektiği tespit edildi — Bölüm 9'a eklendi.

---

## 6. API SÖZLEŞME (CONTRACT) STANDARTLARI

PROMPT.md'de bahsi geçmiyor ama zorunlu:

- Backend her endpoint için **springdoc-openapi** ile otomatik OpenAPI 3.0
  şeması üretecek (`/v3/api-docs`). Manuel yazılan DTO dokümantasyonuna
  güvenilmeyecek.
- WebSocket tarafı için AsyncAPI şeması (`stomp destinations`, payload
  şemaları) `docs/asyncapi.yaml` altında elle bakımlı tutulacak (STOMP için
  otomatik generation aracı olgun değil).
- `contract-validator.skill.md` her PR'da FE'nin TypeScript tiplerinin
  (`openapi-typescript` ile üretilen) backend DTO'larıyla senkron olduğunu
  doğrulayacak; sapma varsa build fail.

---

## 7. HATA YÖNETİMİ — RFC 7807 GENİŞLETMESİ

PROMPT.md madde 7'deki formatı temel alarak:

- Her `BusinessException` alt sınıfı zorunlu olarak bir `code` (örn.
  `AUTH_008_FORBIDDEN_CLUB_ACTION`) taşıyacak; bu kodlar merkezi bir
  `ErrorCode` enum'unda tanımlı olacak, string literal olarak dağınık
  YAZILMAYACAK.
- 4xx hataları `WARN`, 5xx hataları `ERROR` seviyesinde loglanacak; 4xx'ler
  asla stack trace ile loglanmayacak (log gürültüsünü önlemek için).
- Validation hataları (`validationErrors` alanı) field-level olacak:
  `[{ "field": "email", "message": "Geçerli bir üniversite e-postası giriniz" }]`.

---

## 8. GİT / COMMIT / BRANCH STANDARTLARI

- Branch adlandırma: `feature/<agent-domain>/<kisa-aciklama>` (örn.
  `feature/be-auth/otp-flow`), `fix/...`, `chore/...`.
- Commit mesajları Conventional Commits (`feat:`, `fix:`, `refactor:`,
  `chore:`) formatında olacak — `07_git_vcs_auditor.agent.md` bunu PR
  seviyesinde denetleyecek.
- Her PR, `06_qa_validator.agent.md` onayı olmadan `main`'e merge edilemez.

---

## 9. GÜNCELLENMİŞ AJAN/SUBAGENT/HOOK AĞACI İÇİN EKLENMESİ GEREKENLER

Aşağıdaki dosyalar mevcut ağaçta yoktu, bu belgedeki kararları uygulayabilmek
için eklenmesi gerekiyor (güncel ağacı bir sonraki mesajda paylaşıyorum):

| Dosya | Neden Gerekli |
|---|---|
| `subagents/be_media_processing.subagent.md` | Bölüm 5 — MIME doğrulama, resize, optimize mantığı hiçbir mevcut subagent'ın net sorumluluğunda değildi |
| `subagents/be_i18n_manager.subagent.md` | `fe_i18n_manager` vardı ama backend `messages_tr/en.properties` bakımı sahipsizdi |
| `skills/openapi-contract-generator.skill.md` | Bölüm 6 — API sözleşme üretimi/senkronizasyonu için |
| `hooks/pre-deploy-prod-guard.sh` | Bölüm 3 — test-domain flag'inin prod'a sızmasını engelleyen CI gate |

---

## 10. BİLİNÇLİ OLARAK EKLEMEDİĞİM / v1 KAPSAMI DIŞI BIRAKTIĞIM KONULAR

Şeffaflık için: aşağıdakileri bilerek bu dokümana dahil etmedim, çünkü ya
kapsam kararı gerektiriyor (senin onayına ihtiyaç var) ya da v1 için erken:

- **Rate limiting / abuse prevention** (örn. dakikada kaç post/mesaj) —
  hiç ele alınmamış, sosyal platformda spam riski var. Eklenmeli mi?
- **Bildirim tercihleri** (kullanıcı hangi event'ler için email/push istemiyor)
  — `Notifications` tablosu var ama opt-out mekanizması yok.
- **Arama (Search)** — kullanıcı/kulüp/etkinlik arama hiç PRD'de geçmiyor;
  Postgres full-text mi, ayrı bir arama motoru mu (Elasticsearch/Meilisearch)
  gerekecek, bu net değil.
- **Rol değişince aktif WebSocket session'ının anlık invalidasyonu** —
  Bölüm 2'deki cache TTL (60sn) bir kullanıcının banlanmasından sonra 60sn
  daha yetkili kalabileceği anlamına gelir. Bu kabul edilebilir mi, yoksa
  anlık invalidasyon (Redis Pub/Sub ile "kick" event'i) mi gerekiyor?
- **Çoklu cihaz oturum yönetimi** — refresh token `refresh:{userId}` tek
  anahtar; bir kullanıcı 2 cihazdan giriş yaparsa ikinci giriş birinciyi
  invalidate mi eder, yoksa çoklu oturum mu destekleniyor? PRD bunu açık
  bırakmış, karar senin.

---

## 11. UYGULAMA ÖNCELİK SIRASI (Ajanlar İçin)

1. `01_infra_devops` → `02_db_architect` (Bölüm 1 kurallarıyla)
2. `03_be_core` (Security + RBAC iskeleti, Bölüm 2 + 3)
3. `be_auth_domain`, `be_media_processing` (yeni), `be_i18n_manager` (yeni)
4. `be_feed_story`, `be_club_event`, `be_chat_realtime`
5. `be_kafka_worker`
6. Frontend katmanı (`05_fe_architect` ve subagent'ları)
7. `06_qa_validator` — Bölüm 10'daki açık kararlar netleşmeden final onay verilmeyecek

---

## 12. BELİRSİZLİK PROTOKOLÜ

Bir ajan, PROMPT.md ve bu belgede karşılığı olmayan bir tasarım kararıyla
karşılaşırsa: (a) varsayım YAPMAYACAK, (b) `docs/OPEN_QUESTIONS.md` dosyasına
soruyu ve önerdiği 2 seçeneği yazacak, (c) o özelliği "TODO: pending decision"
yorumuyla iskelet halinde bırakıp bir sonraki bağımsız modüle geçecektir.
