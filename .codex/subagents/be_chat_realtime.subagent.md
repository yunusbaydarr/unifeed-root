# be_chat_realtime.subagent.md

## Bağlı Olduğu Agent
`04_be_features`

## Sorumluluk
STOMP/WebSocket altyapısı, Redis Pub/Sub ile yatay ölçeklenebilir chat,
`ConversationThread` modeli (DIRECT + CLUB tipi sohbetler).

## Görevler
1. **ChannelInterceptor**: Yalnızca STOMP `CONNECT` frame'inde `Authorization`
   header'ındaki JWT'yi doğrular (ARCHITECT.md §3.2). Sonraki `SEND`/`SUBSCRIBE`
   frame'lerinde tekrar parse etmez — `Principal` bir kez bağlanır.
2. **ConversationThread modeli**: PRD'deki ikili `sender_id/receiver_id` yapısı
   yetersizdi (kulüp içi grup sohbeti karşılanamıyordu). Bunun yerine:
   `ConversationThread(id, type=DIRECT|CLUB, club_id?)` +
   `ThreadParticipant(thread_id, user_id)` + `Message(thread_id, sender_id,
   content, read_at)`. DIRECT thread'ler 2 katılımcılı, CLUB thread'ler
   `club_members` ile senkron.
3. **Redis Pub/Sub**: Çoklu backend instance'ı arasında mesaj/typing/presence
   dağıtımı `chat:thread:{threadId}` kanalı üzerinden yapılır — WebSocket
   session'ları bir instance'a bağlı kalsa bile mesaj her instance'a ulaşır.
4. **Presence/typing** (ARCHITECT.md §7'de "açık karar" olarak bırakılmıştı):
   Bu subagent varsayılan olarak `presence:{userId}:{threadId}` key'i 5sn TTL
   ile uygular; farklı bir değer isteniyorsa `docs/DECISIONS.md`'e not
   düşülüp burada override edilir.
5. **Okundu bilgisi**: `Message.read_at` alanı, thread'e `SUBSCRIBE` olan
   kullanıcı için son mesajları toplu `UPDATE` ile işaretler (mesaj başına
   ayrı istek YASAK — N+1 riski).

## Girdi/Çıktı Kontratı
- **WS Destination**: `/app/chat.send/{threadId}` (client→server),
  `/topic/chat/{threadId}` (server→client)
- **Output DTO**: `MessageDto { id, threadId, senderId, content, sentAt, readAt }`

## Kısıtlar
- Tek bir WS session, birden fazla thread'e subscribe olabilir; her thread
  için ayrı authentication YAPILMAZ (§3.2 prensibiyle tutarlı).

## Bağımlı Olduğu Diğer Bileşenler
`03_be_core` (STOMP interceptor altyapısı), `fe_socket_manager` (client tarafı), `02_db_architect`
