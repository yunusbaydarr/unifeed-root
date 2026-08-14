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
