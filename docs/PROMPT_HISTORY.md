# Unifeed - AI Prompt Geçmişi

## [2026-08-14] 00_discovery_planner — WBS Hazırlığı
- **İstek:** `docs/PROMPT.md` ve `docs/ARCHITECT.md` okunarak yalnızca
  `00_discovery_planner` rolü çalıştırılsın; PRD WBS'e bölünsün ve
  `docs/WBS.md` oluşturulsun. Kod yazılmadan önce kullanıcı onayı beklensin.
- **Durum:** WBS hazırlanıyor; implementasyon başlatılmadı.

## [2026-08-14] 01_infra_devops — Altyapı Fazına Geçiş
- **İstek:** Onaylanan WBS sonrasında sıradaki `01_infra_devops` rolüne geç.
- **Durum:** Docker Compose, ortam yapılandırmaları, kalıcı upload volume'ları
  ve secret içermeyen `.env.example` hazırlanıyor.

## [2026-08-14] 02_db_architect — Kodlama Öncesi Bilgilendirme
- **İstek:** Sıradaki faza devam et; ancak kodlama başlamadan önce kullanıcıyı
  bilgilendir.
- **Durum:** Yalnızca şema planı ve bağımlılık analizi hazırlanacak; migration,
  entity veya uygulama kodu kullanıcı onayı olmadan yazılmayacak.

## [2026-08-14] 02_db_architect — Şema Uygulama Onayı
- **İstek:** Sunulan veri modeli ve migration planı onaylandı.
- **Durum:** Flyway şeması, ERD özeti ve kulüp durum makinesi uygulanıyor.

## [2026-08-14] 02_db_architect — Devam
- **İstek:** Şema uygulamasına devam et.
- **Durum:** Migration bütünlük ve indeks denetimi sürüyor.

## [2026-08-14] 03_be_core — Backend Çekirdeğini Başlat
- **İstek:** `03_be_core` fazını başlat.
- **Durum:** Spring Boot, güvenlik, JWT, bağlamsal RBAC, RFC 7807 ve i18n omurgası uygulanıyor.

## [2026-08-14] 04_be_features — Backend Tamamlama
- **İstek:** Backend tamamlanana kadar devam et; frontend fazına geçmeden önce kullanıcıyı bilgilendir.
- **Durum:** Auth, feed/story, sosyal grafik, kulüp/etkinlik, chat, medya ve Kafka modülleri entegre ediliyor.

## [2026-08-14] Backend API Doğrulama ve Git Commit
- **İstek:** Backend API'lerini Swagger/Postman seviyesinde doğrula, tamamlanan işleri sonraki faza geçmeden commit et ve frontend geçişinde haber ver.
- **Durum:** OpenAPI/Swagger entegrasyonu, canlı HTTP smoke testleri ve Git commit hazırlığı yapılıyor. `messages_tr.properties` kullanıcı tarafından düzeltileceği için değiştirilmeyecek.
