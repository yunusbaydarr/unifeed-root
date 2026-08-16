# Sürüm Notları ve SSS

## Sürüm notları – teslim adayı 1.0

### Eklendi

- Kulüp ve etkinlik yönetimi, üyelik, katılım ve davet kabul/red.
- Gelecek tarihli yaklaşan etkinlik listeleme.
- Profilde takipçi, takip ve kulüp listelerini modal açma.
- Profil üzerinden mevcut doğrudan konuşmayı açma/oluşturma.
- REST API controller–service–repository ayrımları ve ortak frontend veri hook'ları.
- Chat, davet ve etkinlik API entegrasyon testleri.

### Düzeltildi

- `events` sorgusunda boş `clubId` parametresinin PostgreSQL UUID çıkarımı hatası.
- Tekrarlanan frontend tarih, mesaj ve mutation mantığı.
- Controller katmanındaki doğrudan SQL erişimleri.

### Bilinen sınırlamalar

- Docker socket erişimi olmayan ortamlarda Testcontainers testi otomatik çalışmaz.
- JaCoCo coverage yüzdesi henüz raporlanmıyor.
- Uygulama içi rate limiting ve gelişmiş moderasyon kapsam dışıdır.

## Sıkça Sorulan Sorular

**Uygulama nasıl başlatılır?**  `.env` değerleri sağlanarak Docker Compose `app` profiliyle frontend/backend ve bağımlılık servisleri başlatılır.

**Yaklaşan etkinlik ne demektir?**  Başlangıç tarihi mevcut zamandan ileri olan, kullanıcının erişebildiği kulüp etkinliğidir.

**Kulüp yöneticisi olmayan kullanıcı etkinlik oluşturabilir mi?**  Hayır. Backend `CLUB_ADMIN` yetkisini denetler.

**Davet kabul edilince ne olur?**  Davet türüne göre kulüp üyeliği veya etkinlik katılımı oluşturulur ve davet durumu `ACCEPTED` yapılır.

**Mesaj butonu yeni konuşma mı açar?**  Önce aynı katılımcılarla mevcut doğrudan konuşma aranır; varsa tekrar kullanılır.

**Kullanıcı verileri nasıl korunur?**  JWT/refresh cookie, BCrypt, backend yetki kontrolleri, parametreli SQL ve CORS allowlist kullanılır.

**AI uygulamanın içinde çalışıyor mu?**  Hayır. AI yalnızca geliştirme ve dokümantasyon yardımcısıdır.

