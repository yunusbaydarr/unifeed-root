# 📒 DECISIONS.md — Ajan Karar Günlüğü

Bu dosya, `ARCHITECT.md` veya `PROMPT.md` içinde açıkça tanımlanmamış bir konuda
bir ajanın verdiği kararların kaydını tutar. **Her ajan, kapsam dışı bir karar
verdiğinde bu dosyaya bir madde eklemek zorundadır** — aksi halde
`06_qa_validator` ilgili PR'ı reddeder (`on-contract-violation.sh`).

Format:

```
## [YYYY-MM-DD] <Kısa Başlık>
- **Ajan:** <agent-id>
- **Bağlam:** Hangi görev sırasında bu karara ihtiyaç duyuldu
- **Karar:** Ne yapıldı
- **Gerekçe:** Neden bu şekilde karar verildi (ARCHITECT.md'de kural yoktu)
- **Geri Dönüş Riski:** Bu karar sonradan değiştirilirse etkilenecek modüller
```

---

## [ÖRNEK] Presence Indicator TTL Değeri
- **Ajan:** be_chat_realtime.subagent
- **Bağlam:** "Yazıyor..." göstergesi implementasyonu sırasında Redis key TTL
  değeri ARCHITECT.md'de tanımlı değildi.
- **Karar:** `presence:{userId}:{threadId}` key'i 5 saniye TTL ile set edildi,
  frontend her keystroke'ta refresh ediyor.
- **Gerekçe:** WhatsApp/Slack benzeri UX standardı; 5sn kullanıcı deneyimini
  bozmadan Redis yükünü makul tutuyor.
- **Geri Dönüş Riski:** Değer değişirse sadece `be_chat_realtime` ve
  `fe_socket_manager` etkilenir, DB şeması etkilenmez.

---

<!-- Yeni kararlar bu satırın altına, en yeni en üstte olacak şekilde eklenir -->

## [2026-08-14] E-posta Olayları için Transactional Outbox
- **Ajan:** backend registration reliability
- **Bağlam:** Kafka erişilemezken kayıt isteğinin producer `max.block.ms` süresince beklemesi ve DB/Redis/Kafka arasında kısmi başarı riski.
- **Karar:** E-posta olayları HTTP transaction'ında Kafka'ya gönderilmeyecek. Kullanıcı/iş verisiyle aynı PostgreSQL transaction'ında `email_outbox` tablosuna yazılacak; zamanlanmış publisher olayları sınırlı timeout ile Kafka'ya gönderecek ve başarısızlıkları gecikmeli yeniden deneyecek. Consumer, `eventId` tabanlı mevcut idempotency kaydını koruyacak.
- **Gerekçe:** Kullanıcı kaydının broker erişilebilirliğine bağlı olmaması, DB değişikliği ile event üretiminin atomik olması ve geçici Kafka arızalarında olay kaybının önlenmesi.
- **Geri Dönüş Riski:** Auth ve etkinlik e-posta üreticileri, V4 migration, scheduler ve Kafka e-posta consumer akışı etkilenir.

## [2026-08-14] Frontend Oturum, Veri ve Responsive Kabuk Sınırları
- **Ajan:** 05_fe_architect ve fe_* alt ajanları
- **Bağlam:** UI mocklarının tek bir masaüstü görünümünden responsive uygulamaya dönüştürülmesi ve cookie tabanlı refresh sözleşmesine bağlanması.
- **Karar:** Sunucu verisi TanStack Query, kısa ömürlü auth durumu Zustand, access token yalnız bellek, refresh token HttpOnly cookie olarak ayrıldı. 320–1023px aralığında mobil alt navigasyon ve içerik odaklı tek sütun; 1024px üzerinde sabit sidebar; geniş ekranlarda isteğe bağlı üçüncü panel kullanılıyor. Tüm atomlar tasarım tokenları üzerinden tema değiştiriyor.
- **Gerekçe:** Güvenlik sözleşmesini korurken tekrar kullanılabilirlik, responsive işlev eşitliği ve server/client state ayrımını net tutmak.
- **Geri Dönüş Riski:** Auth bootstrap, API interceptor, uygulama kabuğu, tüm route sayfaları ve responsive test matrisi etkilenir.

## [2026-08-14] Frontend Sözleşmesi Tamamlama ve Güvenli OAuth Devri
- **Ajan:** 05_fe_architect / backend contract completion
- **Bağlam:** UI mocklarının ihtiyaç duyduğu veriler mevcut backend API'sinde yoktu; Google OAuth başarı akışı access token'ı URL fragment'ine yazıyordu.
- **Karar:** V3 migration ile profil/kulüp/etkinlik sunum alanları eklendi; frontend-facing REST yanıtları camelCase DTO'lara geçirildi ve feed cursor'ı iki açık alan olarak döndürüldü. OAuth callback artık access token yerine Redis'te 60 saniye yaşayan, tek kullanımlık bir code döndürüyor; frontend bu code'u `/api/v1/auth/oauth/exchange` ile değiştiriyor.
- **Gerekçe:** Mock ekranlarının gerçek API ile beslenebilmesi, OpenAPI tip üretiminin deterministik olması ve access token'ın tarayıcı URL/log/telemetri yüzeyinden çıkarılması gerekiyor.
- **Geri Dönüş Riski:** Auth callback, üretilen frontend tipleri, feed pagination ve V3 migration alanlarını kullanan istemciler etkilenir. Legacy snake_case feed cursor parametreleri geçiş için korunmuştur.

## [2026-08-14] Açık Sorular — v1 Kapsam Kararları
- **Ajan:** 00_discovery_planner
- **Bağlam:** ARCHITECT.md §10'da karar bekleyen konular bulunduğu için WBS
  planlaması yapıldı; implementasyon varsayımı yapılmadı.
- **Karar:** Karar verilmedi. Aşağıdaki düğümler `06_qa_validator` tarafından
  izlenecek açık soru olarak WBS'e `BLOCKED` durumuyla eklendi: rate limiting /
  abuse prevention, bildirim tercihleri, arama yaklaşımı, rol değişiminde aktif
  WebSocket oturumu invalidasyonu ve çoklu cihaz oturum politikası.
- **Gerekçe:** ARCHITECT.md §12 belirsizlik protokolü varsayım yapmayı yasaklar.
- **Geri Dönüş Riski:** Güvenlik, bildirim, arama, WebSocket ve refresh-token
  veri modelleri ile API sözleşmeleri etkilenebilir.

## [2026-08-14] Kafka Üretim Topolojisi
- **Ajan:** 01_infra_devops
- **Bağlam:** ARCHITECT.md, Kafka topic partition ve replication değerlerini üretim için tanımlamıyordu.
- **Karar:** Dev ortamında 1 broker ve 1 partition/replication; üretimde üç KRaft broker, üç topic için 3 partition/3 replication ve min ISR 2 kullanılacak.
- **Gerekçe:** Dev kaynak tüketimi düşük tutulurken üretimde tek broker arızasına dayanıklılık sağlanır.
- **Geri Dönüş Riski:** Kafka cluster maliyeti, topic konfigürasyonu ve consumer ölçeklendirmesi etkilenir.

## [2026-08-14] Google OAuth Akışının Kaldırılması ve Geliştirme Verisi
- **Bağlam:** Yerel/test kullanımında parola tabanlı giriş yeterlidir; Google OAuth yapılandırması hem gereksiz gizli değişkenler hem de dağıtım bağımlılığı oluşturuyordu.
- **Karar:** Google OAuth istemcisi, callback route'u, code-exchange endpoint'i ve ilgili Redis grant deposu frontend/backend'den kaldırıldı. Dev/local başlangıcında yalnızca veritabanı boşsa çalışan idempotent bir demo seeder eklendi.
- **Gerekçe:** Kimlik akışı e-posta/parola, OTP ve refresh-cookie ile sadeleşir; hazır demo ortamı mevcut veriyi ezmeden üretilebilir.
- **Geri Dönüş Riski:** Google ile daha önce giriş yapan kullanıcılar parola tanımlamadıysa manuel parola sıfırlama akışını kullanmalıdır. Seeder dolu veritabanını bilinçli olarak değiştirmez.
