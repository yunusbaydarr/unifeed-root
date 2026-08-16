# AI Çalışma Günlüğü — unifeed-root

> Proje boyunca yapay zeka ile gerçekleştirilen çalışmaların prompt bazlı kaydı.

---

## Oturum 1 — Keşif, Planlama ve Backend Altyapısı

---

**User:**
Bu repoda `docs/PROMPT.md` (PRD), `docs/ARCHITECT.md` (mimari standartlar) ve `.codex/` klasörü altında agent/subagent/skill/hook tanımları var.

Önce `docs/PROMPT.md` ve `docs/ARCHITECT.md`'yi oku.  
Sonra `.codex/config.json` içindeki `agent_execution_order`'ı takip et, sırayla `.codex/agents/` altındaki dosyaları rol tanımı olarak uygula.  
`DECISIONS.md`, `DESIGN_SYSTEM.md`, `PROMPT_HISTORY.md` dosyalarını en son ekledim — agent/subagent'ların bu dosyalarla işi varsa önce yeni görevlerini ekle; dosyayı en baştan sona düzenleme, sadece gerekiyorsa sona ekle.

Şu an sadece `00_discovery_planner.agent.md` rolünü çalıştır: `docs/PROMPT.md`'yi WBS'e böl, `docs/WBS.md` dosyasını oluştur.  
Kod yazmaya **başlama**, önce WBS'i bana sun, onay bekle.

**AI:**
WBS hazır: `docs/WBS.md`.

Ayrıca ilgili rol/hook tanımlarına yeni doküman entegrasyon görevlerini yalnızca sona ekledim; açık mimari kararları BLOCKED olarak `docs/DECISIONS.md`'ye kaydettim. Kod yazılmadı.

Onay verirseniz sıradaki `01_infra_devops` fazına geçeceğim.

*(7 dosya düzenlendi, +137 / -1)*

---

**User:**
WBS'i inceledim, onaylıyorum. `01_infra_devops` fazına geç.

**AI:**
`01_infra_devops` tamamlandı.

- Dev ve prod Compose topolojileri oluşturuldu: `docker-compose.yml`, `docker-compose.prod.yml`
- Ortam şablonu: `.env.example`
- Dev/prod Spring yapılandırmaları ve prod test-domain kilidi eklendi: `application-prod.yml`
- Kalıcı upload dizinleri oluşturuldu.
- Kafka üretim topolojisi (3 broker, 3 partition/replication, min ISR 2) karar günlüğüne eklendi.
- Compose doğrulaması ve prod-domain kontrolü başarılı.

Not: Bash tabanlı ön-hook, bu Windows ortamında Bash servis izni nedeniyle çalışmadı; zorunlu belge kontrolleri manuel yapıldı.

Sıradaki rol: `02_db_architect`.

*(13 dosya düzenlendi, +160 / -1)*

---

**User:**
`02_db_architect` fazına geç, ancak kodlamaya geçmeden önce ne yapacağını bana anlat ve onay bekle.

**AI:**
`02_db_architect` için kodlama öncesi plan hazır. Henüz migration/entity yazmadım.

Önerilen ilk şema teslimi:

- Ortak temel: UUID, audit alanları, soft-delete, optimistic locking ve FK indeksleri
- Kimlik/RBAC: `users`, global roller, refresh token ve `club_members`
- Sosyal içerik: `posts`, `post_media`, `post_likes`, `comments` (tek seviye reply), `follows`, `stories`
- Kulüp/etkinlik: `clubs`, `events`, etkinlik katılımı/davetleri, kulüp thread ve mesajları; Club/Event state-machine belgeleri
- İletişim: birebir konuşmalar/katılımcılar/mesajlar ve JSONB payload'lı bildirimler
- Flyway migration'ları, `docs/ERD.md` ve `docs/STATE_MACHINES.md`

Açık kararlar (arama, bildirim tercihleri, rate limit, anlık rol invalidasyonu, çoklu cihaz oturumu) çekirdek şemaya varsayımla eklenmeyecek; WBS'deki gibi BLOCKED kalacak.

Onay verirseniz bu planla DB migration ve belgelerini oluşturmaya başlayacağım.

---

**User:**
Refresh token'ları veritabanında tutmak yerine Redis'te sakla; hem performans hem de token iptal hızı açısından daha uygun. Planı bu şekilde revize et ve onayımın ardından ilerle.

**AI:**
Revizyon tamam. Refresh token'lar artık Redis hash olarak saklanacak; HttpOnly + SameSite=Strict cookie ile çıkacak. Geri kalan plan aynı. Onay verirseniz migration'ları ve belgeleri oluşturuyorum.

---

**User:**
Onaylıyorum, ilerle.

**AI:**
DB migration ve belgeler tamamlandı. Flyway migration'ları (V1–V5) başarılı; `docs/ERD.md` ve `docs/STATE_MACHINES.md` oluşturuldu.

---

## Oturum 2 — Backend Eksikliklerinin Tamamlanması ve Frontend Geçişi

---

**User:**
Bu proje `unifeed-root` üzerinde devam ediyor. Backend fazı önceki oturumda tamamlandı, sadece refactor kısımları kaldı çünkü `ARCHITECT.md`'ye uymamış  SOLID ve DRY'a göre geliştirme yapmamış. Şimdilik kalsın, en son refactor kısmına geçeriz.

Şimdi frontend fazına geçiyoruz. Önce bağlamı şu dosyalardan çıkar (kod yazmadan önce):

- `docs/PROMPT.md`
- `docs/ARCHITECT.md`
- `docs/DECISIONS.md`
- `docs/DESIGN_SYSTEM.md`
- `docs/PROMPT_HISTORY.md`
- `unifeed-backend/` dizinindeki mevcut API yapısı (route'lar, DTO'lar, varsa OpenAPI/Swagger dosyası)
- `docs/ui-mocks/` altındaki tüm ekran görselleri

Ardından:
- Backend'in şu an dışarıya sunduğu gerçek API sözleşmesinin özetini çıkar
- `05_fe_architect` ve ilgili `fe_*` subagent'ların görev dağılımını ve çalışma sırasını planla
- Eksik/belirsiz gördüğün noktaları listele

Bunu `docs/PROMPT_HISTORY.md`'ye ekleyerek bana rapor et. Henüz **kod üretme**, önce planı onaylayayım.

**AI:**
Raporu ve onay bekleyen planı `docs/PROMPT_HISTORY.md` dosyasına ekledim.

Önemli bulgular:
- Mevcut backend, mock ekranların önemli bölümünü besleyecek endpoint'leri henüz sunmuyor
- E-posta/parola login, profil, kulüp/etkinlik listeleme, story, chat thread listesi ve bildirim WebSocket yayını eksik
- API response'larında `Map` kullanımı ve `snake_case`/`camelCase` karışımı var; tip üretiminden önce standardize edilmeli
- Feed cursor sözleşmesi tutarsız: response tek string, request iki ayrı alan bekliyor
- Planlanan sıra: `05_fe_architect` → atomic UI/i18n → auth interceptor → socket manager → architect entegrasyonu

Frontend kodu üretmedim. Onayınızla bu sırada ilerlerim; sözleşme boşlukları için önce backend tamamlama mı, yoksa ayrıştırılmış mock/read-only frontend mi istediğinizi belirtmeniz yeterli.

---

**User:**
Backend'deki tüm eksiklikleri tamamla, response standardizasyonunu da dahil et; sonrasında frontend'e geçelim.

**AI:**
Backend sözleşme eksiklikleri tamamlandı; frontend koduna henüz başlamadım.

Tamamlanan ana alanlar:
- Login/logout, parola sıfırlama ve güvenli OAuth code exchange
- Profil, kullanıcı postları, story ve global arama API'leri
- Medyalı feed, açık cursor sözleşmesi, post detay/silme ve yorum listeleme
- Kulüp/etkinlik listeleme, detay, üyelik, oluşturma alanları ve davet adayları
- Chat thread oluşturma/listeleme, geçmiş ve okundu işlemleri
- Kalıcı + WebSocket bildirimleri, STOMP token yenileme
- CORS, güvenli cookie ve production callback ayarları
- V3 veritabanı migration'ı, OpenAPI bearer tanımı ve AsyncAPI sözleşmesi

Doğrulamalar: Java 21 build PASS, Maven testleri 2/2 PASS, PostgreSQL V1→V3 migration PASS.

Frontend fazına geçmeye hazırız.

*(41 dosya düzenlendi, +415 / -11)*

---

**User:**
Frontend'e geç. Bir önceki prompttaki kurallar aynen geçerlidir. Responsive ve adaptive bir tasarımla gerçekleştir. `docs/ui-mocks` altında tasarlanacak ekranların görselleri ve renk paletinin `.md` dosyası var, orayı oku. Görseller Google Stitch ile tasarlandı. Bazı ekranlar tasarlanmamış olabilir  aynı kural, font ve renk paletine göre o ekranları da tamamla. Agent'larda test agent'ı varsa her yeri test ettirt; çalışmayan hiçbir buton veya başka bir şey istemiyorum. Backend tarafında eksik çıkarsa gerekli yerleri en son belirt, oraları kodlayıp API şemalarını sana vereyim.

**AI:**
Frontend ana fazı tamamlandı. UI mockları ve tasarım sistemi temel alınarak responsive/adaptive React uygulaması geliştirildi.

Tamamlananlar:
- Auth, OTP, parola sıfırlama ve Google OAuth
- Feed, gönderi, görsel yükleme, beğeni, yorum ve paylaşım
- Hikâye oluşturma/görüntüleme, global arama
- Kulüp ve etkinlik listeleme, detay, katılım ve oluşturma
- Bildirim yönetimi ve gerçek zamanlı mesajlaşma
- Profil düzenleme, takip ve ayarlar
- Türkçe/İngilizce i18n, koyu/açık tema
- Mobil alt navigasyon ve masaüstü sidebar
- Global error boundary, RFC 7807 hata işleme ve mutation bildirimleri
- Route bazlı code splitting

Doğrulama sonuçları: TypeScript PASS, Production build PASS, Vitest 5/5 PASS, Playwright desktop + Pixel 7: 7 PASS / 3 viewport-koşullu SKIP.

Backend tarafında hâlâ gereken sözleşmeler:
- Kulüp/etkinlik daveti gönderme endpoint'i
- Kulüp profilini güncelleme, üye çıkarma ve kulüp rolü yönetimi
- Etkinlik güncelleme ve silme

Bu aksiyonlar için sahte veya çalışmayan buton eklenmedi. Endpoint ve güncel OpenAPI şemaları sağlandığında ilgili yönetim ekranlarına bağlanabilir.

*(39 dosya düzenlendi, +578 / -60)*

---

## Oturum 3 — Hata Ayıklama ve Uçtan Uca Doğrulama

---

**User:**
Kayıt olmaya çalıştım; uzunca bir süre bekledikten sonra hata verdi, kayıt olamadım. Orayı kontrol et. Docker'da gerekli her container ayakta mı bak. Paralel bir şekilde backend'de ki gerekli geliştirmeleri yap.

**AI:**
İlk somut bulgu: UniFeed backend container'ı ayakta görünse de ona bağlı PostgreSQL, Redis ve Kafka container'ları yaklaşık iki saat önce durmuş. Bu durum özellikle kayıt sırasında bağlantı zaman aşımına kadar bekleyip hata üretmesini açıklıyor.

İkinci sorun: backend `MAIL_HOST=replace-with-local-mail-host` ile çalışıyor ve Compose'ta SMTP servisi yok; kayıt başarılı olsa bile OTP e-postası teslim edilemiyor.

Kalıcı çözüm olarak: kayıt sırasında Kafka'ya doğrudan bağlanmak yerine transaction içi outbox kaydı oluşturuyorum. Böylece Kafka geçici olarak kapansa bile HTTP kayıt isteği beklemiyor ve e-posta olayı kaybolmadan yeniden gönderiliyor. Mailpit de Compose'a ekleniyor.

*(24 dosya düzenlendi, +272 / -29)*

Kayıt akışı uçtan uca doğrulandı:
- Kayıt yanıtı: 200, 1 saniyenin altında
- Doğrulama e-postası: Mailpit'e ulaşıyor
- OTP doğrulama: 200
- Access token + HttpOnly refresh cookie: başarılı
- Tekrar kayıt: 409 / AUTH_014_EMAIL_ALREADY_REGISTERED
- Bozuk JSON: 400 / COMMON_001_VALIDATION_FAILED
- Outbox: 4 yayınlanmış, 0 bekleyen olay

---

## Oturum 4 — Güvenlik İncelemesi ve SOLID/DRY Refactor

---

**User:**
Teslimden önce güvenlik incelemesi yap:
- Input validation/sanitization var mı (SQL injection, XSS açıkları)
- Auth/authorization doğru uygulanmış mı (her endpoint gerçekten yetki kontrolü yapıyor mu, yoksa sadece token varlığı mı kontrol ediliyor)
- Şifreler/secret'lar kod içinde hardcoded mı, yoksa env/secret manager'da mı
- Rate limiting, CORS ayarları var mı

**AI:**
Güvenlik incelemesi tamamlandı; kod değiştirmedim.

**Kritik bulgu — WebSocket sohbet dinleme yetkisi:**
WebSocketConfig bağlantıda JWT doğruluyor, fakat SUBSCRIBE için sohbet katılımcılığı kontrol etmiyor. Token sahibi herhangi bir kullanıcı, thread UUID'sini öğrenirse o kanala subscribe olmayı deneyebilir. Bu P0/P1 önceliğindedir.

**Input validation eksiklikleri:**
- `ClubRequest` ve `EventRequest` alanlarında Bean Validation kısıtları yok; `@Valid` yeterli koruma sağlamıyor
- `SocialGraphController` yorum request'ine `@Valid` koymuyor; maksimum yorum uzunluğu yok
- `DirectThreadRequest.participantId` için `@NotNull` yok

**Authorization eksiklikleri:**
- `GET /clubs/{clubId}/invite-candidates` ve `GET /events/{eventId}/invite-candidates` authenticated kullanıcıya açık; kulüp yöneticisi kontrolüyle korunmalı
- `/v3/api-docs/**` ve Swagger endpointleri herkese açık; prod'da kapatılmalı

**Olumlu bulgular:**
- SQL parametreli bağlama mevcut, SQL injection açığı yok
- Parolalar BCrypt cost 12 ile hashleniyor
- JWT access token süresi 15 dakika, refresh token Redis'te HttpOnly+SameSite=Strict cookie ile

**Rate limiting:** Uygulama seviyesinde yok; login, register, OTP ve medya upload başta olmak üzere tüm mutation'lar için Redis tabanlı rate limiter gerekli.

---

**User:**
WebSocket SUBSCRIBE yetkilendirmesini ve davet adayı endpoint'lerindeki admin kontrolünü düzelt. Request DTO validation kısıtlarını ekle. Swagger'ı prod'da kapat. Mevcut işlevleri bozmadan yap.

**AI:**
Düzeltmeler uygulandı ve doğrulandı. Java 21 build ve testler geçti.

---

**User:**
Backend'de SOLID ve DRY'a uymayan yerleri tespit etmiştin. İlgili agent'ları çalıştır; paralel olarak frontend ve backend'de düzenlemeleri yapsınlar. Mevcut projeyi bozma, sadece prensiplere uygun hale getir.

**AI:**
Mimari/DRY düzenlemeleri tamamlandı ve canlı Docker uygulamasına alındı.

Backend:
- `PostController` ve `SearchController`'dan doğrudan SQL/JDBC kaldırıldı; akış artık `controller → service → repository`
- Tekrarlanan authenticated-user dönüşümü ve limit sınırlandırması ortaklaştırıldı: `CurrentUser.java`, `RequestLimits.java`

Frontend:
- Ortak tarih biçimleyici eklendi: `date.ts`
- Tekrarlanan API çağrıları data hook'larına taşındı
- Profil liste modalı sayfadan ayrıldı: `ProfileListsDialog.tsx`
- `AccountPages.tsx` ve `CommunityPages.tsx` mevcut route export'larını ve UI davranışını koruyan façade dosyaları olarak sadeleştirildi

Doğrulama: Backend Java 21 Docker Maven build/test başarılı. Frontend Docker production build başarılı. Canlı search, profil ve kulüp liste endpoint'leri doğrulandı.

Not: Çok sorumluluğu olan `ClubEventService`'i davet/kulüp/etkinlik servislerine bölmek davranışsal riski yüksek daha büyük bir refactor — mevcut işlevleri bozmamak için bu turda dokunmadım; sonraki güvenli iyileştirme adımı bu olmalı.

---

**User:**
Veritabanı ve veri modelini kontrol et:
- Normalizasyon seviyesi mantıklı mı (aşırı normalize/denormalize)
- Index'ler doğru yerlerde mi (foreign key'ler, sık sorgulanan alanlar)
- Migration geçmişi tutarlı mı, geri alınabilir mi
- N+1 query problemi var mı

**AI:**
İnceleme tamamlandı; kod değiştirmedim. Canlı PostgreSQL'de Flyway geçmişi V1–V5 sıralı ve başarılı görünüyor.

**Normalizasyon:** Genel olarak mantıklı bir 3NF çekirdeği var. İki kontrollü denormalizasyon mevcut: `posts.like_count` / `posts.comment_count` trigger ile güncelleniyor (feed performansı için makul); `notifications.payload` ve `email_outbox.payload` JSONB.

**Dikkat gerektiren nokta:** Hem `club_threads/club_thread_messages` hem `conversation_threads/direct_messages` yapıları var, `conversation_threads.type` içinde `CLUB` değeri de bulunuyor. İki farklı grup sohbet modeli gelecekte veri modelini belirsizleştirebilir.

**İndeksler:** FK'lar, feed sorguları, mesaj geçmişi ve bildirimler iyi kapsanmış. İyileştirilebilir noktalar: `events(club_id, starts_at) WHERE deleted_at IS NULL` bileşik partial indeksi ve bildirim sorgusu için benzer partial indeks.

**Migration geçmişi:** V1–V5 zinciri tutarlı ve ileriye dönük eklemelerden oluşuyor. Flyway Community'de down/rollback yok — bu bir hata değil; geri alma yerine yeni ileri migration ile düzeltme yapılmalı.

**N+1:** JPA/Hibernate kullanılmadığı için klasik ORM N+1 yok; sorgular JDBC ile açıkça yazılmış. İki potansiyel nokta: `ClubEventService` kulüp listeleme için correlated subquery kullanıyor (ölçek büyürse join+aggregate önerilir) 

---
**User:**
Unit test var mı, kritik business logic test edilmiş mi
Integration test var mı (gerçek DB/API akışını test eden)
Test coverage yüzdesi anlamlı mı yoksa sadece sayı şişirmek için yazılmış yüzeysel testler mi (assert'siz testler gibi)
yoksa ilgili agenti görevlendir testler yazılsın


**AI:**

Başlangıçta test kapsamı yetersizdi; test ajanını görevlendirdim ve kritik backend testleri eklendi.
Başlangıç durumu:
Backend: yalnızca 1 Redis mock unit testi vardı.
Frontend: 5 anlamlı Vitest testi ve API’leri mock’layan Playwright senaryoları vardı.
Assert’siz/yüzeysel değillerdi; fakat kulüp, davet ve mesajlaşma iş kurallarını kapsamıyorlardı.
Gerçek PostgreSQL/API entegrasyon testi ve coverage yüzdesi yoktu.

Eklenen unit testler:
[ChatServiceTest.java](C:\\Users\\Yunus\\Desktop\\unifeed-root\\unifeed-backend\\src\\test\\java\\app\\unifeed\\chat\\ChatServiceTest.java): kişinin kendine mesaj açamaması, mevcut konuşmanın tekrar oluşturulmaması, mesajın kaydı + WebSocket yayını + alıcı bildirimi.
[ClubEventServiceTest.java](C:\\Users\\Yunus\\Desktop\\unifeed-root\\unifeed-backend\\src\\test\\java\\app\\unifeed\\club\\ClubEventServiceTest.java): kendine davet engeli, davet kabulünde üyelik/katılım oluşması, reddedilen davette yan etki olmaması.

Eklenen entegrasyon testi:
[EventsApiIntegrationTest.java](C:\\Users\\Yunus\\Desktop\\unifeed-root\\unifeed-backend\\src\\test\\java\\app\\unifeed\\club\\EventsApiIntegrationTest.java)
Testcontainers PostgreSQL üzerinde Flyway migration’larını çalıştırır, JWT ile gerçek GET /api/v1/events isteği yapar ve yalnızca gelecek tarihli etkinliklerin döndüğünü doğrular.
Gerekli Testcontainers bağımlılıkları [pom.xml](C:\\Users\\Yunus\\Desktop\\unifeed-root\\unifeed-backend\\pom.xml) dosyasına eklendi.

Doğrulama:
Frontend Vitest: 5/5 başarılı.
Backend test kaynakları Java 21 Docker derlemesinde başarıyla derlendi.
