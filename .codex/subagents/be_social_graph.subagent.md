# be_social_graph.subagent.md

## Bağlı Olduğu Agent
`04_be_features`

## Sorumluluk
PRD'de özellik olarak tanımlı ama şemada eksik olan sosyal etkileşim
katmanı: Comments, PostLikes, Follows, Notifications. Bu subagent
ARCHITECT.md §1.3'te zorunlu tutulan junction table'ları ve senkron
sayaçları uygular.

## Görevler
1. **PostLikes**: `POST /api/v1/posts/{id}/like` idempotent — kullanıcı
   zaten beğenmişse `409 Conflict` değil, no-op + mevcut state döner.
   Unique constraint `[post_id, user_id]` ihlali uygulama seviyesinde önce
   kontrol edilir, DB constraint son savunma hattıdır.
2. **Comments**: Tek seviye reply (`parent_comment_id`) desteklenir, ikinci
   seviye nested reply YASAK (ARCHITECT.md §1.3). Yorum silme soft-delete
   (`deleted_at`), silinen yorum "Bu yorum silindi" placeholder'ı ile
   gösterilir (thread bütünlüğü bozulmasın diye).
3. **Follows**: `follower_id = following_id` DB `CHECK` constraint ile
   engellenir. Takip/takipten çıkma idempotent.
4. **Notifications**: `payload_json` (JSONB) polymorphic içerik taşır.
   Tip listesi: `LIKE, COMMENT, FOLLOW, CLUB_INVITE, EVENT_REMINDER,
   CLUB_ENROLLMENT`. WebSocket üzerinden anlık push (`/user/queue/notifications`)
   + DB kalıcı kayıt eşzamanlı yapılır.
5. **Sayaç senkronizasyonu** (ARCHITECT.md §1.4): `like_count`/`comment_count`
   asla `count = count + 1` ile güncellenmez. Ya DB trigger, ya da:
   ```sql
   UPDATE posts SET like_count = (
     SELECT COUNT(*) FROM post_likes WHERE post_id = :postId
   ) WHERE id = :postId
   ```
   `@Transactional` sınırı içinde, like/unlike işlemiyle aynı transaction'da.

## Girdi/Çıktı Kontratı
- **Output DTO**: `CommentDto { id, author, content, parentId, createdAt }`,
  `NotificationDto { id, type, payload, isRead, createdAt }`

## Kısıtlar
- Bildirim gönderimi ana iş transaction'ını bloklamaz — Kafka event olarak
  asenkron fırlatılır (`be_kafka_worker` tüketir).

## Bağımlı Olduğu Diğer Bileşenler
`02_db_architect` (junction table şeması), `be_chat_realtime` (WS push altyapısı paylaşımı), `be_kafka_worker`
