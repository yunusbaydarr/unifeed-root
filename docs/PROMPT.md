
# 🎓 UNIFEED - ÜNİVERSİTE İÇİ SOSYAL MEDYA PLATFORMU
## 📋 Kapsamlı PRD, Mimari Tasarım ve Sistem İsterleri Belgesi (PROMPT.md)

---

## 1. 📌 PROJE GENEL BAKIŞI VE VİZYON
**UniFeed**, üniversite öğrencilerinin kampüs ekosistemi içerisinde güvenli, modüler ve yüksek performanslı bir şekilde sosyalleşmesini, akademik/sosyal kulüpler kurup yönetmesini, etkinlikler organize etmesini, anlık mesajlaşmasını ve kampüs içi etkileşimi (gönderi, hikaye, bildirim) tek bir çatı altında yürütmesini sağlayan kurumsal ölçekli bir sosyal ağ platformudur.

---

## 2. 🛠️ TEKNOLOJİ YIĞINI (TECH STACK)

| Katman | Teknoloji / Kütüphane | Kullanım Amacı |
| :--- | :--- | :--- |
| **Backend** | Java 21, Spring Boot 3.x | Ana uygulama motoru, REST API, WebSocket sunucusu |
| **Veritabanı** | PostgreSQL 16+ | İlişkisel veri saklama, transactional iş kuralları |
| **Önbellek & Message Broker** | Redis 7+ | Session, Refresh Token, Hikaye TTL, Redis Pub/Sub |
| **Event Streaming** | Apache Kafka (KRaft Mode) | Asenkron e-posta kuyruğu, denetim (audit) ve log eventleri |
| **Gerçek Zamanlı İletişim**| Spring WebSocket + STOMP | Anlık mesajlaşma ve canlı bildirim iletimi |
| **Güvenlik & Kimlik** | Spring Security 6, JWT, OAuth2 (Google) | RBAC, domain doğrulama, token rotasyonu |
| **Frontend** | React 18+ (Vite / Next.js), TypeScript | SPA/SSR kullanıcı arayüzü |
| **Durum & Veri Yönetimi** | TanStack Query (React Query), Zustand/Redux | Sunucu ve istemci durum yönetimi |
| **UI & Stil** | Tailwind CSS, Radix UI / Shadcn UI, Lucide Icons | Erişilebilir, atomik ve tekrar kullanılabilir UI bileşenleri |
| **Lokalizasyon (i18n)** | `react-i18next` (FE), Spring `MessageSource` (BE) | TR / EN çoklu dil desteği |
| **Kapsayıcılaştırma** | Docker, Docker Compose | Geliştirme ve prod ortamlarının izole çalıştırılması |

---

## 3. 📐 MİMARİ STANDARTLAR VE YAZILIM PRENSİPLERİ

### 3.1. %100 SOLID & DRY Uyumluluğu
* **Single Responsibility Principle (SRP):** Her sınıf, servis ve bileşen yalnızca tek bir sorumluluğa sahip olacaktır (Örn: `PostService` sadece post işlemlerini yapacak, post görselleriyle `MediaStorageService`, bildirimlerle `NotificationPublisher` ilgilenecektir).
* **Open/Closed Principle (OCP):** Yeni bir bildirim kanalı (SMS, Push) veya oturum sağlayıcı eklendiğinde mevcut kod değiştirilmeden arayüzler (`NotificationChannel`, `AuthProvider`) genişletilebilir olacaktır.
* **Liskov Substitution Principle (LSP):** Tüm alt sınıflar/implementasyonlar türedikleri arayüz veya abstract sınıfın yerine geçerken sistemi kırmayacaktır.
* **Interface Segregation Principle (ISP):** Şişirilmiş (fat) interface'ler yerine amaca yönelik atomik interface'ler tanımlanacaktır (`Auditable`, `SoftDeletable`, `Taggable`).
* **Dependency Inversion Principle (DIP):** Üst seviye modüller alt seviye modüllere doğrudan değil, abstraction (interface) üzerinden bağımlı olacaktır.
* **DRY (Don't Repeat Yourself):** Kod tekrarları engellenecek; cross-cutting concern'ler AOP (Aspect-Oriented Programming), interceptor ve custom hook yapılarıyla soyutlanacaktır.

### 3.2. Sıfır Konsol Çıktısı & Yapısal Loglama (Zero Console Log & SLF4J/Logback)
* Kod tabanında kesinlikle `System.out.println`, `System.err.println` veya `console.log` **kullanılmayacaktır**.
* Backend tarafında **SLF4J + Logback** ile yapılandırılmış JSON/Pattern loglama altyapısı kurulacaktır:
  * Log Seviyeleri: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR` standartlarına uyulacaktır.
  * Hassas veriler (şifre, token, PII) asla loglanmayacaktır (`Masking Pattern`).
  * Kritik sistem ve güvenlik logları asenkron olarak Kafka üzerinden log topic'lerine (`system-logs`, `audit-logs`) iletilebilecektir.

### 3.3. Çoklu Dil Desteği (Internationalization - i18n)
* Sistem **Türkçe (`tr`)** ve **İngilizce (`en`)** dillerini tam kapsamlı destekleyecektir.
* **Backend:** Tüm validation, exception mesajları ve e-posta şablonları HTTP `Accept-Language` başlığına göre `messages_tr.properties` ve `messages_en.properties` dosyalarından dinamik çözümlenecektir.
* **Frontend:** Tüm etiketler, butonlar, modal metinleri ve toast bildirimleri `react-i18next` dil dosyalarından çekilecektir.

---

## 4. 📁 MEDYA & DOSYA SAKLAMA STRATEJİSİ (LOCAL ENGINE)

* Harici bulut depolama servisleri (AWS S3, MinIO vb.) **kullanılmayacaktır**.
* Yüklenen dosyalar yerel dosya sistemindeki `uploads/` dizini altında kategorize edilerek saklanacaktır:
  ```text
  unifeed-backend/
  └── uploads/
      ├── avatars/
      ├── posts/
      ├── stories/
      └── clubs/

  - Veritabanı Kuralı: Veritabanında (PostgreSQL) dosya binary'si (BLOB) asla
    tutulmayacaktır. Yalnızca göreceli erişim yolu (URI) tutulacaktır (Örn:
    /uploads/posts/2026/03/uuid-v4.webp).
  - Statik Kaynak Yönetimi: Spring Boot WebMvcConfigurer üzerinden uploads/
    dizinini HTTP GET isteklerine statik kaynak olarak açacaktır (/uploads/** ->
    file:uploads/).
  - Dosya Güvenliği & Optimizasyon: Gelen görseller backend'de MIME-type
    denetiminden geçirilecek, izin verilen formatlar (JPEG, PNG, WEBP) harici
    dosyalar reddedilecek ve disk yazımı öncesi yeniden boyutlandırılıp optimize
    edilecektir.

5. 🔐 ROL VE YETKİLENDİRME MODELİ (FINE-GRAINED RBAC)

Sistemde hiyerarşik ve bağlamsal rol yönetimi uygulanacaktır:

| Rol               | Kapsam                     | Yetkiler & UI Görünürlüğü                                                                                                                                    |
| :---------------- | :------------------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **SYSTEM\_ADMIN** | Global                     | Tüm sistemi izleme, kulüp onaylama/kapatma, kullanıcı banlama, global logları görme.                                                                         |
| **STUDENT**       | Global                     | Gönderi/Hikaye paylaşma, yorum yapma, kulüplere üye olma, etkinliklere katılma, kulüp kurma başvurusu yapma.                                                 |
| **CLUB\_ADMIN**   | Bağlamsal (Kulüp Özelinde) | Sadece kendi kulübünün etkinliklerini oluşturma/silme, üye davet etme/çıkarma, kulüp profilini düzenleme. *Diğer kulüplerde standart öğrenci statüsündedir.* |
| **CLUB\_MEMBER**  | Bağlamsal (Kulüp Özelinde) | Kulüp içi özel tartışmalara katılma, etkinlik davetlerini görüntüleme.                                                                                       |

  - Frontend UI Uyarlaması: Kulüp sayfasında "Etkinlik Oluştur", "Kulübü
    Düzenle", "Üye Yönetimi" butonları sadece o kulüpte CLUB_ADMIN rolüne sahip
    kullanıcıya render edilecektir. Normal öğrenciye bu yönetim butonları
    gösterilmeyecektir (Conditional Rendering via Permission Guard).

6. 📱 DETAYLI İŞ AKIŞLARI VE MODÜL GEREKSİNİMLERİ

                       +-----------------------------------+
                       |        UNIFEED ARCHITECTURE       |
                       +-----------------------------------+
                                         |
     +-----------------+-----------------+-----------------+-----------------+
     |                 |                 |                 |                 |
     v                 v                 v                 v                 v
[Auth & RBAC]   [Feed & Story]     [Clubs/Events]     [Chat & WS]     [Kafka Email]
     |                 |                 |                 |                 |
Google OAuth2    Redis ZSET TTL     Transactional     STOMP Over       Thymeleaf i18n
Edu Domain /     Cursor-based       Auto-Enrollment   SockJS / Redis   Async Worker
Test Flag        Pagination         Smart Invite      Auto-Refresh     Event Engine

6.1. Kimlik Doğrulama & Oturum Yönetimi (Auth Engine)

1.  Google OAuth2 & Koşullu Domain Doğrulaması:
      - Canlı Modu (Production): Sadece .edu ve .edu.tr uzantılı e-postalar
        kabul edilir. Harici uzantılar için HTTP 403 Forbidden ve
        yerelleştirilmiş hata mesajı döner.
      - Test Modu (Development Feature Flag - app.security.allow-test-domains:
        true):
          - @gmail.com uzantısıyla girişlere izin verilir.
          - Bu kullanıcılara auth yanıtında is_test_account: true bayrağı
            dönülür.
          - Frontend bu bayrağı yakaladığında ekranın üstüne kapatılabilir bir
            uyarı bandı (Toast/Alert) basar:
            "⚠️ Test Sürümü Uyarısı: Şu an test aşamasında olduğumuz için Gmail
            adresinizle giriş yapabiliyorsunuz. Canlı sürümde yalnızca
            üniversite e-posta adresiniz (.edu / .edu.tr) geçerli olacaktır."
2.  Kayıt ve E-posta Doğrulama (OTP):
      - Manuel kayıt olan kullanıcıya 6 haneli OTP kodu Kafka üzerinden asenkron
        e-posta olarak gönderilir. Kod Redis üzerinde (TTL: 3 dk) tutulur.
      - Google OAuth2 ile gelen kullanıcıların e-postası Google tarafından
        onaylı olduğu için OTP adımı doğrudan atlanır ve JWT üretilir.
3.  Token Yaşam Döngüsü & Güvenlik:
      - Access Token: 15 dakika geçerlidir, bellek içi (in-memory) tutulur.
      - Refresh Token: 7 gün geçerlidir, Redis'te tutulur (refresh:{userId}).
        İstemciye HttpOnly, Secure, SameSite=Strict Cookie olarak verilir. Token
        Rotasyonu (RTR - Refresh Token Rotation) zorunludur.

6.2. Gerçek Zamanlı İletişim & Kesintisiz WebSocket Mimarisi

  - STOMP Bağlantısı & Token Doğrulama:
      - WebSocket bağlantısı kurulurken (STOMP CONNECT) başlıkta gönderilen
        Access Token, ChannelInterceptor seviyesinde valide edilir.
  - Kesintisiz Oturum & Arka Planda Yenileme (Silent Refresh):
      - Access token süresi dolduğunda aktif WebSocket bağlantısı ve
        kullanıcının uygulama içi gezintisi kesilmemelidir.
      - Frontend Axios/Fetch Interceptor mekanizması HTTP 401 aldığında bekleyen
        tüm istekleri kuyruğa alır (Queue), arka planda /api/v1/auth/refresh
        çağrısı yaparak yeni token'ı alır, kuyruktaki istekleri yeniler ve
        WebSocket STOMP oturumuna yeni token'ı enjekte eder. Kullanıcı hiçbir
        şekilde oturumdan atılmaz.
  - Ölçeklenebilirlik: Chat trafiği ve typing/presence durumları backend'de
    Redis Pub/Sub üzerinden dağıtılır.

6.3. Ana Akış, Hikayeler ve Önbellek Mimarisi

1.  Ana Akış (Feed):
      - Kullanıcının takip ettiği kişilerin ve üye olduğu kulüplerin
        gönderilerini listeler.
      - Performans için Cursor-based Pagination (Keyset Pagination) kullanılır
        (limit, cursor_created_at, cursor_id).
      - Gönderi Kartı Bileşeni: Like, Yorum, Paylaş/Davet ve Kaydet aksiyonları,
        reaktif optimistik UI güncellemeleri.
2.  Hikayeler (Stories Engine):
      - Hikayeler paylaşıldığı andan itibaren tam 24 saat geçerlidir.
      - Aktif hikaye ID'leri Redis ZSET (Sorted Set) veri yapısında score değeri
        expiration_timestamp olacak şekilde saklanır.
      - Süresi dolan kayıtlar hem Redis TTL hem de Spring @Scheduled arka plan
        işçisi ile pasife çekilir.

6.4. Kulüpler, Etkinlikler ve Akıllı Davet Modülü

1.  Kulüp Yönetimi:
      - Kulüp oluşturan kullanıcı o kulübün CLUB_ADMIN rolüne atanır.
      - Kulüp admini etkinlik oluşturabilir, afiş yükleyebilir, üye listesini
        yönetebilir.
2.  Kritik İş Kuralı (Otomatik Kulüp Üyeliği):
      - Kulübe üye olmayan bir öğrenci kulübün etkinliğine "Katıl" dediğinde,
        backend @Transactional metot içinde önce öğrenciyi kulübe CLUB_MEMBER
        olarak kaydeder, ardından etkinliğe kaydını gerçekleştirir. İşlem
        atomiktir; biri başarısız olursa rollback edilir.
3.  Filtrelenmiş Akıllı Davet Motoru (Smart Invite):
      - Bir kulübe veya etkinliğe arkadaş davet etme modalı açıldığında
        listelenen adaylar filtrelenir:
      - SQL Kuralı: Zaten o kulübe üye olan veya etkinliğe kayıt yaptırmış olan
        kullanıcılar SQL düzeyinde NOT EXISTS / NOT IN sorgusuyla elenir.
        Listede sadece davet edilebilir kişiler listelenir.

6.5. Bildirimler ve Asenkron Kafka E-Posta Motoru

1.  Bildirim Dağıtımı:
      - Uygulama içi anlık bildirimler WebSocket (/user/queue/notifications)
        üzerinden iletilir ve veritabanına yazılır.
2.  Kafka Event-Driven Mail Mimarisi:
      - HTTP thread'leri mail gönderimi için asla bloke edilmez.
      - Olay gerçekleştiğinde Kafka email-events topic'ine payload fırlatılır:
        {
          "eventType": "CLUB_ENROLLMENT | EVENT_REGISTRATION | USER_REGISTRATION | DIRECT_MESSAGE",
          "recipientEmail": "ogrenci@universite.edu.tr",
          "language": "tr",
          "templateData": {
            "username": "Deniz",
            "clubName": "Yazılım Kulübü",
            "eventDate": "2026-04-10 14:00"
          }
        }
      - Arka planda çalışan @KafkaListener tüketici servisi, gelen dile göre
        ilgili Thymeleaf HTML şablonunu derleyip e-postayı asenkron olarak
        gönderir.

7. 🛡️ RFC 7807 STANDARTLARINDA GLOBAL HATA YÖNETİMİ

Uygulamanın hiçbir noktasında yakalanmamış (unhandled) exception olmayacak, 500
hatalarında stack trace kullanıcıya sızdırılmayacaktır.

Standart Hata Yanıt Formatı (Problem Details)

{
  "type": "https://unifeed.app/errors/club-access-denied",
  "title": "Access Denied",
  "status": 403,
  "detail": "Bu kulübün yönetim paneline erişim yetkiniz bulunmamaktadır.",
  "instance": "/api/v1/clubs/42/events",
  "code": "AUTH_008_FORBIDDEN_CLUB_ACTION",
  "timestamp": "2026-03-30T10:15:30Z",
  "validationErrors": null
}

Backend Hata Yönetim Kuralları

  - @RestControllerAdvice ile tüm exception türleri
    (MethodArgumentNotValidException, ResourceNotFoundException,
    AccessDeniedException, BusinessException) yakalanacaktır.
  - Hata mesajları dile duyarlı (MessageSource) olarak property dosyalarından
    çekilecektir.

Frontend Hata Yakalama (Global Error Boundary & Interceptor)

  - React Global Error Boundary bileşeni UI kilitlenmelerini engelleyip şık bir
    fallback ekranı sunacaktır.
  - Axios response interceptor, gelen RFC 7807 hata kodlarına göre ilgili alanda
    (Form Validation, Alert veya Toast) kullanıcıya anlaşılır hata mesajını
    gösterecektir.


8. 🎨 FRONTEND BİLEŞEN VE TASARIM SİSTEMİ STANDARTLARI

  - Atomic & Reusable Design: Butonlar (Button), Modallar (Dialog), Form
    Alanları (Input, Select), Avatar kartları tek bir sorumluluğa sahip ve
    tamamen generic olacaktır.
  - Zero Logic in Presentational Components: UI bileşenleri sadece prop alıp
    render edecek, state ve API çağrıları custom hook'lara (useFeed,
    useClubActions, useChatSocket) devredilecektir.
  - Responsive & Accessible: Tailwind CSS ile mobil öncelikli (mobile-first),
    dark/light mode uyumlu ve WAI-ARIA standartlarına uygun erişilebilirlik.

🎯 GÖREV VE UYGULAMA TALİMATI

Yukarıda belirtilen tüm PRD isterleri, SOLID/DRY prensipleri, RBAC hiyerarşisi,
i18n lokalizasyonu, local media storage, kesintisiz WebSocket oturum yapısı,
RFC 7807 hata standartları ve Kafka asenkron mail motoru mimarisine sadık
kalarak projenin kaynak kodlarını, yapılandırma dosyalarını ve dizin ağacını
adım adım, eksiksiz bir şekilde inşa et.

