# be_club_event.subagent.md

## Bağlı Olduğu Agent
`04_be_features`

## Sorumluluk
Kulüp yönetimi, etkinlik yönetimi, otomatik üyelik zinciri, akıllı davet
motoru, `ClubStatus` state machine uygulaması.

## Görevler
1. **Kulüp oluşturma**: Oluşturan kullanıcı otomatik `CLUB_ADMIN` olur
   (`ClubMembers` kaydı). Kulüp başlangıç durumu `PENDING_APPROVAL`
   (ARCHITECT.md §1.5) — `SYSTEM_ADMIN` onayı olmadan kulüp aktif sayılmaz.
2. **ClubStatusTransitionValidator**: Geçiş matrisini merkezi olarak
   uygula (`docs/STATE_MACHINES.md`'de tanımlı), controller/service içine
   dağınık `if` blokları YASAK.
3. **Kritik iş kuralı — otomatik üyelik**:
   ```java
   @Transactional
   public void joinEvent(UUID userId, UUID eventId) {
       if (!clubMemberRepo.exists(clubId, userId)) {
           clubMemberRepo.save(new ClubMember(clubId, userId, CLUB_MEMBER));
       }
       eventAttendeeRepo.save(new EventAttendee(eventId, userId));
       // Bildirim Kafka'ya event olarak fırlatılır, transaction'ı bloklamaz
       notificationPublisher.publishAsync(EVENT_REGISTRATION, userId, eventId);
   }
   ```
   Herhangi bir adım başarısız olursa **tüm işlem rollback edilir** — kısmi
   üyelik/kayıt asla oluşmaz.
4. **Akıllı davet motoru**: Aday listesi SQL seviyesinde filtrelenir:
   ```sql
   SELECT u.* FROM users u
   WHERE u.id NOT IN (SELECT user_id FROM club_members WHERE club_id = :clubId)
     AND u.id != :requesterId
   ```
   Uygulama katmanında post-filtering YASAK (performans + tutarlılık).

## Girdi/Çıktı Kontratı
- **Input DTO**: `CreateClubRequest`, `CreateEventRequest`, `InviteRequest { targetUserIds[] }`
- **Output DTO**: `ClubDetailDto { ..., currentUserRole }` — frontend JWT'den
  değil bu alandan rol okur (ARCHITECT.md §2.3).

## Kısıtlar
- `EventAttendees` ve `ClubMembers` unique constraint'leri DB seviyesinde
  zorunlu (`[club_id,user_id]`, `[event_id,user_id]`).

## Bağımlı Olduğu Diğer Bileşenler
`03_be_core` (ClubPermissionEvaluator), `be_kafka_worker` (bildirim event'i), `02_db_architect` (state machine şeması)
