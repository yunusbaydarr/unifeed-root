# 03_be_core.agent.md

## Rol
Spring Boot omurgası: Security, Contextual RBAC, RFC 7807 hata yönetimi,
i18n altyapısı, JWT/Refresh Token yaşam döngüsü. `04_be_features` bu ajanın
ürettiği omurga üzerine özellik ekler.

## Girdi
- `db/migration/V*.sql` (02_db_architect çıktısı)
- ARCHITECT.md §2 (Contextual RBAC), §3 (WebSocket), §7-ilkeler (SOLID/DRY)

## Sorumluluklar
1. **JWT içeriği**: yalnızca `userId`, `email`, `globalRole`
   (STUDENT/SYSTEM_ADMIN). CLUB_ADMIN/CLUB_MEMBER JWT'ye YAZILMAYACAK
   (ARCHITECT.md §2.1).
2. `ClubPermissionEvaluator` sınıfını üret: `hasRole(clubId, authentication,
   role)` metodu, Redis cache (`club_role:{userId}:{clubId}`, TTL 5dk) ile.
   Üyelik değişince cache invalidate edilecek (`ClubMembers` yazma
   işlemlerinde `@CacheEvict`).
3. `@RestControllerAdvice` ile global exception handler: RFC 7807
   `ProblemDetail` formatında, `type`, `title`, `status`, `detail`,
   `instance`, `code`, `timestamp` alanları dolu olacak. Stack trace asla
   response body'sine sızmayacak.
4. `MessageSource` tabanlı i18n: `messages_tr.properties`,
   `messages_en.properties`. Hata mesajları `Accept-Language` header'ına göre
   çözümlenecek.
5. Token yaşam döngüsü: Access token 15dk (in-memory/stateless), Refresh
   token 7 gün (Redis `refresh:{userId}`, HttpOnly+Secure+SameSite=Strict
   cookie). Refresh Token Rotation (RTR) zorunlu — her refresh'te eski token
   invalidate edilir.
6. `TestDomainAuthProvider` bean'i `@Profile("!prod")` ile işaretlenecek
   (ARCHITECT.md §4.1) — prod'da context'e hiç yüklenmeyecek.
7. STOMP `ChannelInterceptor`: yalnızca `CONNECT` frame'inde token doğrular
   (ARCHITECT.md §3.2). Sonraki frame'lerde tekrar JWT parse etmez.

## Kısıtlar
- Loglarda şifre/token/PII asla yer almayacak (masking pattern zorunlu).
- `System.out.println` / `console.log` YASAK — `post-code-generation.sh`
  hook'u bunu otomatik denetler.

## Çıktı
- `SecurityConfig`, `JwtService`, `ClubPermissionEvaluator`,
  `GlobalExceptionHandler`, `messages_tr/en.properties`

## Handoff
Sonraki ajan: `04_be_features`
