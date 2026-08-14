# be_auth_domain.subagent.md

## Bağlı Olduğu Agent
`04_be_features`

## Sorumluluk
Google OAuth2 domain doğrulaması, test flag mantığı, OTP akışı, JWT üretimi.

## Görevler
1. **Domain doğrulama**: `.edu` / `.edu.tr` uzantısı whitelist olarak
   `application.yml`'den okunur, hardcode edilmez. Prod'da eşleşmeyen
   domain'ler `403 Forbidden` + RFC 7807 `code: AUTH_001_DOMAIN_REJECTED`.
2. **Test modu**: `app.security.allow-test-domains=true` iken `@gmail.com`
   girişine izin ver, response'a `is_test_account: true` ekle. Bu bean
   `@Profile("!prod")` ile işaretli olmalı (ARCHITECT.md §4.1) — kendi
   kodunda bu annotation'ı unutma, `06_qa_validator` bunu ayrıca denetler.
3. **OTP**: Manuel kayıtta 6 haneli kod üret, Kafka `email-events` topic'ine
   `USER_REGISTRATION` event'i olarak yayınla (senkron SMTP çağrısı YASAK).
   Kod Redis'te `otp:{email}` key'i, TTL 3 dakika.
4. **Google OAuth2**: Email zaten Google tarafından doğrulanmış kabul edilir,
   OTP adımı atlanır, doğrudan `JwtService.issueTokens()` çağrılır.
5. **JWT üretimi**: Yalnızca `userId`, `email`, `globalRole` claim'leri
   (ARCHITECT.md §2.1) — CLUB_ADMIN/CLUB_MEMBER asla buraya yazılmaz.
6. Test hesapları (`is_test_account=true`) hiçbir koşulda `SYSTEM_ADMIN` veya
   `CLUB_ADMIN` rolüne atanamaz — DB seviyesinde `CHECK` constraint önerilir.

## Girdi/Çıktı Kontratı
- **Input DTO**: `OAuthCallbackRequest { code, provider }`,
  `ManualRegisterRequest { email, password, name, surname }`
- **Output DTO**: `AuthResponse { accessToken, refreshTokenCookie, isTestAccount }`

## Kısıtlar
- Şifreler `BCrypt` (strength ≥ 12) ile hash'lenir, asla loglanmaz.
- OTP kodu log satırlarında maskelenir (`***`).

## Bağımlı Olduğu Diğer Bileşenler
`03_be_core` (JwtService, SecurityConfig), `be_kafka_worker` (email event tüketimi)
