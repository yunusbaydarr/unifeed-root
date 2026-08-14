# be_media_processor.subagent.md

## Bağlı Olduğu Agent
`04_be_features`

## Sorumluluk
PRD madde 4'te detaylı tanımlı ama önceki klasör ağacında sahibi olmayan
medya işleme katmanı: MIME kontrolü, resize, optimize, `uploads/` dizin
yazımı.

## Görevler
1. **Dizin yapısı**: `uploads/{avatars|posts|stories|clubs}/{yyyy}/{MM}/`.
   Dosya adı her zaman `UUID.webp` (orijinal dosya adı asla korunmaz —
   path traversal ve bilgi sızıntısı riski).
2. **MIME doğrulama**: Yalnızca `JPEG, PNG, WEBP` kabul edilir. Kontrol
   dosya uzantısından DEĞİL, gerçek byte-header (magic number) okunarak
   yapılır (`Apache Tika` veya eşdeğeri) — uzantı sahteciliğine karşı.
3. **Resize/optimize**: Yükleme sonrası senkron olarak (kullanıcı bekler,
   küçük görseller için makul) 3 varyant üretilir: `thumbnail (150px)`,
   `medium (600px)`, `original (max 1920px, kalite %85 webp)`.
4. **DB kuralı**: Binary asla PostgreSQL'e yazılmaz, yalnızca göreceli path
   (`/uploads/posts/2026/03/uuid.webp`) tutulur (ARCHITECT.md/PROMPT.md §4).
5. **Statik servis**: `WebMvcConfigurer` ile `/uploads/**` → `file:uploads/`
   mapping'i, cache-control header'ları ile (`max-age=31536000` — dosya adı
   UUID olduğu için immutable cache güvenli).
6. **Silme akışı**: Post/story/avatar soft-delete olsa bile, disk üzerindeki
   dosya hemen silinmez — `@Scheduled` bir temizlik job'u, 30 gün sonra
   `deleted_at IS NOT NULL` kayıtların dosyalarını fiziksel siler (geri alma
   penceresi için).

## Girdi/Çıktı Kontratı
- **Input**: `MultipartFile` + `category` enum (AVATAR/POST/STORY/CLUB)
- **Output DTO**: `MediaUploadResponse { thumbnailPath, mediumPath, originalPath }`

## Kısıtlar
- Tek dosya boyut limiti: 10MB (config'den okunur, hardcode edilmez).
- Yükleme sırasında disk dolarsa `507 Insufficient Storage` + RFC 7807.

## Bağımlı Olduğu Diğer Bileşenler
`be_feed_story`, `be_club_event`, `01_infra_devops` (volume mount)
