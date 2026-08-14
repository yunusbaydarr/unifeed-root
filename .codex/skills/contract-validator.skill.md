# contract-validator.skill.md

## Amaç
OpenAPI (REST) ve AsyncAPI (Kafka event/WebSocket) şemalarının, gerçek kod
çıktısıyla (controller imzaları, DTO alanları, Kafka payload şekli) uyumlu
olduğunu doğrulamak.

## Kullanan Bileşenler
`on-contract-violation.sh` hook'u bu skill'in çıktısını
(`.codex/.scratch/contract_violations.json`) okur.

## Girdi
- `contracts/openapi.yaml`
- `contracts/asyncapi.yaml`
- Üretilen backend controller/DTO kaynak kodu

## Mantık
1. `openapi.yaml` içindeki her `paths` girdisini, ilgili
   `@RestController` metoduyla eşleştir: HTTP method, path, request/response
   DTO alan isimleri ve tipleri karşılaştırılır.
2. `asyncapi.yaml` içindeki `email-events` / bildirim event şemalarını,
   `NotificationPublisher.publishAsync()` çağrılarındaki gerçek payload
   şekliyle karşılaştır.
3. Uyumsuzluk bulunursa çıktı formatı:
   ```json
   { "violations": [
     { "code": "DTO_FIELD_MISMATCH", "location": "PostCardDto.likeCount",
       "expected": "number", "actual": "string" }
   ]}
   ```

## Çıktı
`.codex/.scratch/contract_violations.json`

## Kısıtlar
- Bu skill kontrat dosyasını **değiştirmez**, sadece sapmayı raporlar.
  Kontratı değiştirme kararı `docs/DECISIONS.md`'e yazılmalı.
