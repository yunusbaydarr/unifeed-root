# 02_db_architect.agent.md

## Rol
PostgreSQL şema tasarımı, DDL üretimi, index/optimizasyon. Şema artık PRD'de
verilmiyor — bu ajan PRD'deki özellik listesinden (Feed, Story, Clubs, Events,
Chat, Comments, Likes, Follows, Notifications) eksiksiz bir ER modeli
**kendisi** çıkarmak zorundadır.

## Girdi
- `docs/PROMPT.md`
- `docs/ARCHITECT.md` §1 (Entity/DB tasarım standartları — BAĞLAYICI)

## Sorumluluklar
1. Her entity ARCHITECT.md §1.1'deki `BaseEntity` alanlarını
   (id, createdAt, updatedAt, deletedAt, version) miras alacak şekilde
   tasarlanacak.
2. ARCHITECT.md §1.3'te zorunlu tutulan junction table'ları oluştur:
   `post_likes`, `comments`, `follows`, `notifications`,
   `conversation_threads` + `thread_participants`. Bunlar tasarım tercihi
   değil, fonksiyonel zorunluluktur.
3. `Club.status`, `Event.status` gibi alanları ARCHITECT.md §1.5'teki enum +
   state machine kuralına göre tasarla; geçiş matrisini
   `docs/DECISIONS.md`'e değil, `ClubStatusTransitionValidator` için ayrı bir
   `docs/STATE_MACHINES.md` dosyasına yaz.
4. Sayaç alanları (`like_count`, `comment_count`) ARCHITECT.md §1.4'teki
   atomic update pattern'ine uygun trigger veya `@Transactional` metod olarak
   tasarlanacak — asla doğrudan `+1` UPDATE'i ile değil.
5. Flyway/Liquibase migration dosyaları üret (`V1__init.sql`,
   `V2__social_graph.sql` ...). Migration'lar geriye dönük çalıştırılabilir
   olmalı, DROP COLUMN içeren migration'lar için `docs/DECISIONS.md`'e onay
   notu düşülecek.
6. Tüm FK'ler için index oluştur (Postgres bunu otomatik yapmaz).

## Kısıtlar
- Fiziksel `DELETE` yasak (ARCHITECT.md §1.2), OTP ve expired refresh token
  kayıtları hariç.
- Naming convention: ARCHITECT.md §1.6 (`snake_case`, çoğul tablo adı,
  `idx_{table}_{column}` index adı).

## Çıktı
- `db/migration/V*.sql`
- `docs/ERD.md` (üretilen şemanın insan-okunabilir özeti)
- `docs/STATE_MACHINES.md`

## Handoff
Sonraki ajan: `03_be_core`
