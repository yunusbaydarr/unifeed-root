# be_feed_story.subagent.md

## Bağlı Olduğu Agent
`04_be_features`

## Sorumluluk
Ana akış (feed) pagination'ı ve Hikayeler (Stories) motoru.

## Görevler
1. **Feed**: Kullanıcının takip ettiği kişilerin (`Follows` tablosu — bkz.
   `be_social_graph`) ve üye olduğu kulüplerin (`ClubMembers`) gönderilerini
   birleştirip **cursor-based (keyset) pagination** ile döndür:
   `GET /api/v1/feed?limit=20&cursor_created_at=...&cursor_id=...`.
   `OFFSET/LIMIT` YASAK (büyük veri setinde performans sorunu).
2. **Story yaşam döngüsü**:
   - Paylaşım anında `stories` tablosuna INSERT + Redis ZSET'e
     `ZADD active_stories {expiration_timestamp} {storyId}`.
   - Redis key `TTL` ile paralel expire olur AMA DB tarafında da
     `@Scheduled(fixedRate = 60000)` bir job, süresi dolan story'leri
     `is_active=false` yapar (Redis tek başına DB tutarlılığı garanti etmez).
3. **Optimistic UI**: Like/comment/save aksiyonları frontend'de optimistic
   güncellenir; backend idempotent endpoint sağlar (`PUT` semantics, aynı
   isteğin tekrarı hata vermez).

## Girdi/Çıktı Kontratı
- **Output DTO**: `FeedPageResponse { items: PostCardDto[], nextCursor }`
- `PostCardDto` alanları: `id, author, content, mediaPath, likeCount,
  commentCount, isLikedByMe, createdAt`

## Kısıtlar
- Feed sorgusu N+1 query üretmeyecek — `@EntityGraph` veya explicit `JOIN
  FETCH` kullanılacak, `sql-explain-analyzer.skill` ile doğrulanacak.
- Story medyası `be_media_processor` üzerinden optimize edilmeden DB'ye
  yazılmaz.

## Bağımlı Olduğu Diğer Bileşenler
`be_social_graph` (like/comment sayaçları), `be_media_processor`, `02_db_architect` (index: `idx_stories_expires_at`)
