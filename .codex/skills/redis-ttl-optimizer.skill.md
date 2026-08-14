# redis-ttl-optimizer.skill.md

## Amaç
Redis'te tutulan ZSET (story) ve key (refresh token, OTP, club_role cache,
presence) yapılarının TTL/bellek maliyetini hesaplamak ve öneri sunmak.

## Kullanan Bileşenler
`be_feed_story` (story ZSET), `be_auth_domain` (OTP/refresh TTL),
`03_be_core` (club_role cache), `be_chat_realtime` (presence).

## Mantık
1. Her Redis key pattern'i için beklenen kayıt sayısını (aktif kullanıcı
   tahmini × pattern başına ortalama key sayısı) hesaplar:
   - `refresh:{userId}` → aktif kullanıcı sayısı kadar, TTL 7 gün
   - `otp:{email}` → günlük kayıt sayısı kadar, TTL 3 dk (kısa ömürlü,
     ihmal edilebilir bellek etkisi)
   - `active_stories` (ZSET) → aktif story sayısı, üye TTL 24 saat
   - `club_role:{userId}:{clubId}` → aktif kullanıcı × ortalama kulüp
     üyeliği, TTL 5 dk (sık invalidate)
   - `presence:{userId}:{threadId}` → eşzamanlı chat kullanıcı sayısı,
     TTL 5 sn (çok kısa ömürlü)
2. Toplam tahmini bellek kullanımını (`key sayısı × ortalama value boyutu`)
   raporlar, `maxmemory-policy` önerisi sunar (öneri: `allkeys-lru`, çünkü
   çoğu key zaten kendi TTL'iyle expire oluyor).
3. TTL değeri ARCHITECT.md'de veya `docs/DECISIONS.md`'de tanımlı değilse,
   bu skill bir varsayılan önerir ama kararı **vermez** — ilgili subagent
   `docs/DECISIONS.md`'e yazmak zorundadır.

## Çıktı
`docs/REDIS_CAPACITY_REPORT.md`

## Kısıtlar
- Bu skill üretim trafiğine bağlanmaz, yalnızca statik/tahmini hesap yapar.
