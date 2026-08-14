# openapi-spec-generator.skill.md

## Amaç
Backend controller/DTO kaynak kodundan otomatik OpenAPI 3.1 spesifikasyonu
üretmek — önceki klasör ağacında bu yetenek eksikti (ARCHITECT.md §6).

## Kullanan Bileşenler
`03_be_core` (ilk iskelet), `04_be_features` (her yeni endpoint sonrası
günceller), `contract-validator.skill` (bu çıktıyı referans alır).

## Mantık
1. `springdoc-openapi` (veya eşdeğeri) kütüphanesini backend'e entegre et,
   `/v3/api-docs` endpoint'inden ham şemayı çek.
2. RFC 7807 `ProblemDetail` response şemasını **her endpoint için** ortak
   `4xx/5xx` response olarak enjekte et (tek tek yazılmasın, merkezi
   component reference kullanılsın: `#/components/schemas/ProblemDetail`).
3. i18n'e duyarlı alanlar (`detail` mesajı) için `x-i18n: true` custom
   extension ekle — dokümantasyonu okuyanın bu alanın dile göre
   değişeceğini bilmesi için.
4. Üretilen spec'i `contracts/openapi.yaml` olarak diske yaz, git'e commit
   edilir (generated ama source-controlled — CI'da drift kontrolü için).

## Çıktı
- `contracts/openapi.yaml`
- Opsiyonel: Swagger UI statik HTML (`docs/api-reference/index.html`)

## Kısıtlar
- Elle düzenleme YASAK — spec her zaman koddan üretilir (tek doğruluk
  kaynağı kod, spec ondan türetilir).
