# fe_auth_interceptor.subagent.md

## Bağlı Olduğu Agent
`05_fe_architect`

## Sorumluluk
401 Silent Token Refresh kuyruk mekanizması (Axios/Fetch interceptor).

## Görevler
1. Her isteğe access token'ı `Authorization: Bearer` header'ı olarak ekle.
   Access token bellek içinde (React context/Zustand store) tutulur,
   **asla `localStorage`'a yazılmaz** (XSS riski — refresh token zaten
   HttpOnly cookie'de).
2. `401 Unauthorized` yanıtı alındığında:
   - Bekleyen istek kuyruğa alınır (aynı anda birden fazla 401 gelirse tek
     bir refresh çağrısı yapılır — race condition önlenir, `isRefreshing`
     flag'i ile).
   - `POST /api/v1/auth/refresh` çağrılır (HttpOnly cookie otomatik gider).
   - Başarılıysa: yeni access token store'a yazılır, kuyruktaki tüm istekler
     yeni token ile tekrar denenir.
   - Başarısızsa (refresh token da geçersiz): kullanıcı login sayfasına
     yönlendirilir, store temizlenir.
3. Yeni token, `fe_socket_manager`'a **event/callback ile** iletilir ama
   bu **yeni bir WS CONNECT tetiklemez** (ARCHITECT.md §3.1) — sadece
   sonraki HTTP isteklerinde kullanılır.

## Girdi/Çıktı Kontratı
- Export: `apiClient` (Axios instance, interceptor önceden bağlı)
- Event: `onTokenRefreshed(newToken: string)` — dinleyiciler `fe_socket_manager`

## Kısıtlar
- Refresh isteğinin kendisi 401 dönerse **sonsuz döngüye girilmez** —
  bu istek interceptor'dan muaf tutulur (`skipAuthRefresh: true` flag'i).

## Bağımlı Olduğu Diğer Bileşenler
`03_be_core` (refresh endpoint sözleşmesi), `fe_socket_manager`
