# Test, Güvenlik ve Risk Dokümanı

## Kabul test senaryoları

| ID | Senaryo | Beklenen sonuç |
|---|---|---|
| AT-01 | Geçerli kayıt ve OTP | Hesap oluşur; doğrulama sonrası erişim tokenı verilir |
| AT-02 | Hatalı şifre | `401`; hesap varlığı sızdırılmadan hata gösterilir |
| AT-03 | Refresh/logout | Token yenilenir; logout sonrası refresh geçersizdir |
| AT-04 | Kendi profilini güncelleme | Değişiklik kalıcıdır |
| AT-05 | Başkasının profilini güncelleme | `403`; veri değişmez |
| AT-06 | Aynı gönderiyi ikinci kez beğenme | Tek beğeni kaydı ve doğru sayaç |
| AT-07 | Boş yorum | `400`; yorum oluşmaz |
| AT-08 | Kendini takip etme | İş kuralı hatası; takip kaydı oluşmaz |
| AT-09 | Yetkisiz kulüp etkinliği oluşturma | `403`; etkinlik oluşmaz |
| AT-10 | Geçmiş etkinlik | Yaklaşan listede görünmez |
| AT-11 | Gelecek etkinlik | İlgili üye kulüp kullanıcısına görünür |
| AT-12 | Kendine davet | Reddedilir; invitation oluşmaz |
| AT-13 | Davet kabul | Kulüp üyeliği/etkinlik katılımı ve bildirim oluşur |
| AT-14 | Davet red | Yan etki oluşmaz; durum `DECLINED` olur |
| AT-15 | Konuşma dışı kullanıcı mesaj geçmişi | `403`; mesaj okunamaz |
| AT-16 | Geçersiz medya MIME/boyut | Yükleme reddedilir |
| AT-17 | SQL/XSS girdisi | Parametreli sorgu ve escaped render nedeniyle sistem bozulmaz |

## Mevcut otomatik testler

- Backend unit: Redis reset store, ChatService ve ClubEventService; toplam 7 test metodu.
- Frontend Vitest: auth service sözleşmesi ve erişilebilir UI atomları; 5/5 başarılı.
- Playwright: API route mock'lu ana akış, login ve navigasyon senaryoları.
- Testcontainers entegrasyonu: Flyway + PostgreSQL + JWT + MockMvc ile `/api/v1/events`; Docker socket erişimi olan ortamda çalıştırılmalıdır.

## Güvenlik kontrolleri

- JWT erişim tokenı, HTTP-only/SameSite refresh cookie, BCrypt parola hash'i.
- Controller sonrası service seviyesinde sahiplik/rol kontrolü.
- Parametreli SQL; React varsayılan escaping.
- CORS allowlist; medya MIME/boyut kontrolü.
- Secret'lar environment/secret manager üzerinden verilmelidir.


