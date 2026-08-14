# i18n-key-extractor.skill.md

## Amaç
Koddan (backend + frontend) kullanılan tüm i18n key'lerini çıkarıp,
`messages_tr/en.properties` ve `src/locales/tr|en/*.json` dosyalarıyla
karşılaştırarak eksik/kullanılmayan (orphan) key'leri tespit etmek.

## Kullanan Bileşenler
`be_i18n_manager`, `fe_i18n_manager`, `06_qa_validator`.

## Mantık
1. **Backend tarama**: `MessageSource.getMessage("key", ...)` çağrılarını
   ve `@ExceptionHandler` içindeki `i18nKey` referanslarını regex ile
   topla.
2. **Frontend tarama**: `t('namespace:key')` çağrılarını AST üzerinden
   (basit regex yeterli olmayabilir, JSX içinde dinamik key kullanımı
   ayrıca işaretlenir — `t(variable)` gibi durumlar "manuel inceleme
   gerekli" olarak raporlanır) topla.
3. Karşılaştırma:
   - **Missing**: Kodda kullanılan ama property/json dosyasında olmayan
     key → `06_qa_validator` bunu FAIL sebebi sayar.
   - **Orphan**: Dosyada olan ama kodda hiç kullanılmayan key → uyarı
     (temizlik önerisi, blocking değil).
   - **TR/EN mismatch**: Bir dilde olup diğerinde olmayan key → FAIL.
4. `be_kafka_worker`'ın email şablonları (`_tr.html`/`_en.html`) için de
   aynı çift-dil tutarlılık kontrolünü yapar.

## Çıktı
```json
{ "missing": ["club.status.suspended"],
  "orphan": ["auth.legacy.unused_key"],
  "tr_en_mismatch": ["event.reminder.title"] }
```

## Kısıtlar
- Bu skill dil dosyalarını otomatik doldurmaz (çeviri kalitesi riski);
  yalnızca eksikleri raporlar, `be_i18n_manager`/`fe_i18n_manager` taslak
  ekler.
