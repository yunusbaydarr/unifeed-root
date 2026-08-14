# be_i18n_manager.subagent.md

## Bağlı Olduğu Agent
`04_be_features`

## Sorumluluk
Backend tarafı i18n yönetimi (`fe_i18n_manager`'ın backend eşi — önceki
klasör ağacında eksikti). `messages_tr.properties` / `messages_en.properties`
dosyalarının senkron ve eksiksiz tutulmasından sorumlu.

## Görevler
1. Her yeni validation/exception mesajı iki dilde de eklenmeden PR
   `06_qa_validator` tarafından reddedilir — bu subagent, diğer
   subagent'ların ürettiği yeni mesaj key'lerini toplayıp iki dosyaya da
   otomatik iskelet (TR taslağı + `# TODO: translate` yorumuyla EN taslağı)
   ekler.
2. Key naming convention: `{module}.{context}.{detail}` — örn.
   `auth.domain.rejected`, `club.status.pending_approval`.
3. Thymeleaf email şablonlarının dil varyantlarını (`_tr.html`, `_en.html`)
   `be_kafka_worker` ile senkron tutar; bir şablon TR'de güncellenip EN'de
   güncellenmezse `i18n-key-extractor.skill` bunu tespit eder.
4. `Accept-Language` header'ı desteklenmeyen bir dil gelirse (`tr`/`en`
   dışında), sistem sessizce `en`'e fallback yapar — 400 hatası dönmez.

## Girdi/Çıktı Kontratı
- **Çıktı**: `messages_tr.properties`, `messages_en.properties`,
  `templates/email/*_tr.html`, `templates/email/*_en.html`

## Kısıtlar
- Property dosyalarında key sırası her iki dilde de aynı olacak (diff
  okunabilirliği için) — alfabetik değil, modül gruplarına göre sıralı.

## Bağımlı Olduğu Diğer Bileşenler
`03_be_core` (MessageSource config), `be_kafka_worker`, `i18n-key-extractor.skill`
