# fe_socket_manager.subagent.md

## Bağlı Olduğu Agent
`05_fe_architect`

## Sorumluluk
STOMP/WebSocket bağlantı yaşam döngüsü — ARCHITECT.md §3.1'de tanımlanan
"kesintisiz oturum" mekanizmasının frontend tarafı.

## Görevler
1. `useChatSocket()` hook'u: uygulama açılışında tek bir STOMP client
   (SockJS fallback ile) kurar, tüm thread subscription'ları bu tek
   bağlantı üzerinden yönetilir (her thread için ayrı connection YASAK).
2. **Token yenileme davranışı** (ARCHITECT.md §3.1, kritik nokta): Access
   token silent refresh ile yenilendiğinde **YENİ bir STOMP CONNECT frame'i
   AÇILMAZ**. Mevcut bağlantı, kurulduğu andaki token ile canlı kalmaya
   devam eder (Spring tarafı sadece CONNECT anında doğrular).
3. **Periyodik sessiz reconnect**: Access token ömründen bağımsız, ayrı bir
   zamanlayıcı ile **30 dakikada bir** `disconnect() + connect(freshToken)`
   yapılır. Bu sırada mevcut `subscription` callback referansları korunur —
   kullanıcı arayüzde kopma/mesaj kaybı görmez (subscribe listesi yeniden
   `.subscribe()` edilir ama state kaybolmaz).
4. **Reconnect backoff**: Ağ kopması durumunda exponential backoff
   (1s, 2s, 4s, 8s, max 30s), bağlantı kurulunca kaçırılan mesajlar
   `GET /api/v1/chat/threads/{id}/messages?after=lastMessageId` ile
   telafi edilir (WS mesaj garantisi yoktur, REST fallback şart).

## Girdi/Çıktı Kontratı
- Hook API: `{ sendMessage, subscribe, connectionStatus }`
- `connectionStatus`: `CONNECTING | CONNECTED | RECONNECTING | DISCONNECTED`

## Kısıtlar
- Bu subagent'ın ürettiği kod, `fe_atomic_ui` bileşenlerine doğrudan API
  çağrısı enjekte etmez — yalnızca hook arayüzü sağlar.

## Bağımlı Olduğu Diğer Bileşenler
`be_chat_realtime` (server tarafı sözleşme), `fe_auth_interceptor` (token kaynağı)
