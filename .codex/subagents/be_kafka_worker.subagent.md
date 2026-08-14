# be_kafka_worker.subagent.md

## Bağlı Olduğu Agent
`04_be_features`

## Sorumluluk
Asenkron e-posta gönderim motoru. HTTP thread'lerini asla bloklamaz.

## Görevler
1. **Producer tarafı** (diğer subagent'lar çağırır, bu subagent üretir):
   `NotificationPublisher.publishAsync()` çağrıldığında Kafka
   `email-events` topic'ine şu payload fırlatılır:
   ```json
   {
     "eventType": "CLUB_ENROLLMENT | EVENT_REGISTRATION | USER_REGISTRATION | DIRECT_MESSAGE",
     "recipientEmail": "ogrenci@universite.edu.tr",
     "language": "tr",
     "templateData": { "username": "...", "clubName": "...", "eventDate": "..." }
   }
   ```
2. **Consumer tarafı**: `@KafkaListener(topics = "email-events")` — gelen
   `language` alanına göre doğru Thymeleaf şablonunu
   (`templates/email/{eventType}_{language}.html`) derler ve SMTP ile
   gönderir. Consumer hata alırsa **retry topic** (`email-events-retry`,
   max 3 deneme) + son çare **dead-letter topic** (`email-events-dlq`).
3. **Idempotency**: Aynı event'in iki kez işlenmesi ihtimaline karşı
   `eventId` (UUID) + `processed_events` tablosunda dedup kontrolü.
4. **Şablon genişletilebilirliği (OCP)**: Yeni bir `eventType` eklenirken
   mevcut `EmailTemplateResolver` implementasyonu değiştirilmez, yeni bir
   şablon dosyası + enum değeri eklenir.

## Girdi/Çıktı Kontratı
- **Kafka Topic**: `email-events` (producer), `email-events-retry`,
  `email-events-dlq`

## Kısıtlar
- SMTP gönderim hatası, ana iş akışını (örn. kulübe kayıt) asla etkilemez —
  consumer tamamen izole çalışır.
- Log'da alıcı e-posta adresi maskelenir (`o***@universite.edu.tr`).

## Bağımlı Olduğu Diğer Bileşenler
`be_auth_domain` (OTP/USER_REGISTRATION), `be_club_event`, `be_social_graph`, `be_i18n_manager` (şablon dil dosyaları)
