# UniFeed Yönetici Özeti

## Proje

UniFeed, üniversite topluluğunun dağınık duyuru, kulüp, etkinlik ve iletişim kanallarını tek bir Campus Hub uygulamasında birleştirir. Öğrenciler doğrulanmış hesaplarıyla sosyal akışı takip eder, kulüp/etkinlik keşfeder, davetlere cevap verir, bildirim alır ve doğrudan mesajlaşır.

## Teslim edilen ana değer

- Profil, takip, gönderi, yorum, beğeni ve hikâye akışları.
- Kulüp üyeliği, etkinlik katılımı, yaklaşan etkinlik listesi ve davet kabul/red.
- Arama, bildirim ve profil listeleri.
- Profil üzerinden doğrudan mesaj başlatma ve gerçek zamanlı mesaj altyapısı.
- Türkçe/İngilizce web arayüzü, Docker tabanlı çalışma ortamı.

## Teknik yapı

React/TypeScript frontend; Java 21/Spring Boot backend; PostgreSQL, Redis, Kafka ve WebSocket/STOMP birlikte kullanılır. API sözleşmesi `/api/v1` altında REST olarak tanımlıdır. Migration'lar Flyway ile sürümlenir.

## Kalite ve doğrulama

Kritik chat, kulüp/davet business logic testleri eklendi. PostgreSQL/Testcontainers entegrasyon testi kaynakta mevcut ve Docker erişimli CI/host ortamında çalışır. Frontend Vitest 5/5 başarılıdır. Projede henüz JaCoCo bulunmadığı için ölçülmüş coverage yüzdesi verilmemiştir.


## AI kullanımı

GPT-5 tabanlı OpenAI Codex; kod analizi, refaktör, test ve dokümantasyon yardımcısı olarak kullanıldı. Çalışma zamanında kullanıcı verisiyle çalışan AI özelliği yoktur. AI çıktıları kaynak kod, HTTP yanıtı, build ve test sonuçlarıyla doğrulandı.

